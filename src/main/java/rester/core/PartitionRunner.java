package rester.core;

import kong.unirest.Unirest;
import kong.unirest.UnirestInstance;
import rester.core.proc.ProcChain;
import rester.model.HttpContainer;
import rester.tools.LOG;
import rester.tools.UTIL;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PartitionRunner implements Runnable {
    private static final Logger LOGGER = LOG.getLog("PartitionRunner");

    private final Task task;
    private final List<Integer> partition;

    private ProcChain procChain = ProcChain.inst();
    private UnirestInstance instance = Unirest.spawnInstance();
    private VelocityEngine velocityEngine;

    public PartitionRunner(Task task, List<Integer> part) {
        this.task = task;
        this.partition = part;
        velocityEngine = new VelocityEngine();
        velocityEngine.init();

        Optional.ofNullable(task.getCfg()).ifPresent(cfg -> {
            VelocityContext context = new VelocityContext(args(task));
            Optional.ofNullable(cfg.getProxy()).ifPresent(pStr -> cfgProxy(task, context, pStr));
            Optional.ofNullable(cfg.getSsl()).ifPresent(ssl -> instance.config().verifySsl(ssl));
        });
        instance.config().automaticRetries(false).concurrency(1, 1);
        instance.config().socketTimeout(0);
        instance.config().connectTimeout(0);
    }

    @Override
    public void run() {
        LOGGER.info("request ids to proc:{}", partition);
        partition.forEach(index -> procChain.process(task, index, new HttpContainer(instance, velocityEngine)));
    }

    private Map<String, Object> args(Task task) {
        Map<String, Object> context = new HashMap<>();
        task.getContext().getGlobalArgs().forEach((key, values) -> context.put(key, values.get(0)));
        task.getArgs().forEach((key, values) -> context.put(key, values.get(0)));
        context.put("index", 0);
        context.put("name", task.getName());
        return context;
    }

    private void cfgProxy(Task task, VelocityContext context, String pStr) {
        StringWriter proxyStr = new StringWriter();
        velocityEngine.evaluate(context, proxyStr, task.getName(), pStr);
        UTIL.uphp(proxyStr.toString()).ifPresent(proxy -> {
            if (StringUtils.isBlank(proxy.getUsername()) || StringUtils.isBlank(proxy.getPassword())) {
                instance.config().proxy(proxy.getHost(), Integer.parseInt(proxy.getPort()));
            }
            if (StringUtils.isNotBlank(proxy.getUsername()) && StringUtils.isNotBlank(proxy.getPassword())) {
                instance.config()
                    .proxy(proxy.getHost(), Integer.parseInt(proxy.getPort()), proxy.getUsername(),
                        proxy.getPassword());
            }
        });
    }
}
