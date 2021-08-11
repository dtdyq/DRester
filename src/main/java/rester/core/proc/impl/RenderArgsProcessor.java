package rester.core.proc.impl;

import rester.core.Task;
import rester.core.proc.Processor;
import rester.model.HttpContainer;
import rester.model.Request;
import rester.model.input.RequestTemplate;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RenderArgsProcessor extends Processor {

    @Override
    public void proc(Task task, int index, HttpContainer container) {
        Request req = new Request();
        RequestTemplate template = task.getTemplate();

        VelocityContext context = new VelocityContext(args(index - 1, task));

        StringWriter url = new StringWriter();
        container.getEngine().evaluate(context, url, task.getName(), template.getUrl());
        req.setUrl(url.toString());

        StringWriter method = new StringWriter();
        container.getEngine().evaluate(context, method, task.getName(), template.getMethod());
        req.setMethod(method.toString());

        if (StringUtils.isNotEmpty(template.getBody())) {
            StringWriter body = new StringWriter();
            container.getEngine().evaluate(context, body, task.getName(), template.getBody());
            req.setBody(body.toString());
        }
        Optional.ofNullable(template.getHeader()).ifPresent(head -> {
            Map<String, String> tmp = new HashMap<>();
            head.forEach((k, v) -> {
                StringWriter vs = new StringWriter();
                container.getEngine().evaluate(context, vs, task.getName(), v);
                tmp.put(k, vs.toString());
            });
            req.setHeader(tmp);
        });
        Optional.ofNullable(template.getBasicAuth())
            .ifPresent(aStr -> cfgBasicAuth(task, container, context, aStr, req));
        Optional.ofNullable(template.getTimeout()).ifPresent(to -> {
            StringWriter toSW = new StringWriter();
            container.getEngine().evaluate(context, toSW, "timeout", to);
            req.setTimeout(Integer.parseInt(toSW.toString()));
        });

        Optional.ofNullable(template.getAsserts()).ifPresent(asserts -> {
            StringWriter assertSW = new StringWriter();
            container.getEngine().evaluate(context, assertSW, "asserts", asserts);
            String[] assertR = assertSW.toString().split(",");
            req.getAssertions()
                .addAll(Stream.of(assertR)
                    .map(s -> task.getContext()
                        .getAssertions()
                        .stream()
                        .filter(as -> as.getId().equals(s))
                        .findFirst()
                        .orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));
        });
        container.setRequest(req);

        nextProc(task, index, container);
    }

    private void cfgBasicAuth(Task task, HttpContainer container, VelocityContext context, String baStr, Request req) {
        StringWriter basicAuthSW = new StringWriter();
        container.getEngine().evaluate(context, basicAuthSW, task.getName(), baStr);
        String basicAuth = basicAuthSW.toString();
        if (basicAuth.contains(":")) {
            String user = basicAuth.split(":")[0].trim();
            String pwd = basicAuth.split(":")[1].trim();
            req.setBasicAuthUser(user);
            req.setBasicAuthPwd(pwd);
        }
    }
}
