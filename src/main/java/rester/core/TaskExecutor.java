package rester.core;

import lombok.SneakyThrows;
import rester.core.handler.CleanTailHandler;
import rester.core.handler.ExportFailedTailHandler;
import rester.core.handler.RetryTailHandler;
import rester.core.handler.StatisticTailHandler;
import rester.core.handler.TailHandler;
import rester.core.policy.AssignPolicy;
import rester.core.policy.RandomAssignPolicy;
import rester.core.policy.RotationAssignPolicy;
import rester.core.policy.SegmentAssignPolicy;
import rester.model.input.TaskCfg;
import rester.model.input.ThreadAssignPolicy;
import rester.tools.UTIL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TaskExecutor implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskExecutor.class);
    private final Task task;
    private List<TailHandler> tailHandlers = new ArrayList<>();

    public TaskExecutor(Task task) {
        this.task = task;
        task.setTaskExecutor(this);
        tailHandlers.add(new RetryTailHandler());
        tailHandlers.add(new ExportFailedTailHandler());
        tailHandlers.add(new StatisticTailHandler());
        tailHandlers.add(new CleanTailHandler());
    }

    private AssignPolicy<Integer> getPolicy(ThreadAssignPolicy threadAssignPolicy) {
        switch (threadAssignPolicy) {
            case segment:
                return new SegmentAssignPolicy();
            case rotation:
                return new RotationAssignPolicy();
            case random:
                return new RandomAssignPolicy();
        }
        return (source, count) -> Stream.of(source).collect(Collectors.toList());
    }

    @SneakyThrows
    @Override
    public void run() {
        long start = System.currentTimeMillis();
        task.getStatistic().setStartTime(start);

        TaskCfg cfg = task.getCfg();
        int threadCnt = cfg.getThread();
        List<Integer> ids = IntStream.range(1, cfg.getReqTimes() + 1).boxed().collect(Collectors.toList());

        List<List<Integer>> partitions = getPolicy(cfg.getThreadAssignPolicy()).assign(ids, threadCnt)
            .stream()
            .filter(list -> !list.isEmpty())
            .collect(Collectors.toList());

        task.getStatistic().setThread(partitions.size());

        ThreadPoolExecutor service = UTIL.threadPool(partitions.size(), task.getName() + "-%d");
        partitions.stream()
            .map(part -> service.submit(new PartitionRunner(task, part)))
            .collect(Collectors.toList())
            .forEach((Consumer<Future<?>>) future -> {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    LOGGER.error("partition execute exception.", e);
                }
            });

        tailHandlers.forEach(tailHandler -> tailHandler.handle(task));
        service.shutdown();
    }
}