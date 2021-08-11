package rester.core;

import rester.schedule.Digraph;
import rester.schedule.Job;
import rester.schedule.Scheduler;
import rester.tools.LOG;
import rester.tools.UTIL;

import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

public class Dispatcher {
    private static final Logger LOGGER = LOG.getLog("Dispatcher");

    public static void dispatch(RestContext context, List<Task> tasks) {
        LOGGER.info("dispatch tasks list:{}", tasks.stream().map(Task::getName).collect(Collectors.toList()));
        int threadCnt = context.isSerial() ? 1 : tasks.size();
        ThreadPoolExecutor executor = UTIL.threadPool(threadCnt, context.getName() + "-%d");

        List<Job> jobs =
            tasks.stream().map(task -> new Job(task.getName(), new TaskExecutor(task))).collect(Collectors.toList());

        Digraph digraph = new Digraph();
        digraph.addJobs(jobs);
        if (threadCnt == 1) {
            for (int i = jobs.size() - 1; i > 0; i--) {
                digraph.addDependency(jobs.get(i), jobs.get(i - 1));
            }
        } else {
            jobs.forEach(job -> tasks.stream()
                .filter(task -> task.getName().equals(job.name()))
                .findFirst()
                .ifPresent(task -> Optional.ofNullable(task.getCfg().getDependencies())
                    .ifPresent(deps -> deps.forEach(depName -> jobs.stream()
                        .filter(j -> depName.equals(j.name()))
                        .findFirst()
                        .ifPresent(j -> digraph.addDependency(job, j))))));
        }
        Scheduler.schedule(digraph, executor);
        executor.shutdown();
        try {
            Statisticer.summary(tasks);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
