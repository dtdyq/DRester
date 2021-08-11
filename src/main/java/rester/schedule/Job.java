package rester.schedule;

import lombok.SneakyThrows;
import rester.tools.LOG;

import org.slf4j.Logger;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class Job {
    private static final Logger LOGGER = LOG.getLog("Job");
    private final Runnable task;

    private final String name;
    private boolean flag = false;
    private Future<?> future;

    public Job(String name, Runnable task) {
        this.name = name;
        this.task = task;
    }

    @SneakyThrows
    public void execute(ExecutorService executor) {
        LOGGER.info("begin to execute task:{}", name);
        this.future = executor.submit(task);
    }

    @SneakyThrows
    public void await() {
        this.future.get();
        flag = true;
    }

    @Override
    public String toString() {
        return name;
    }

    public Runnable getTask() {
        return task;
    }

    public void schedule(boolean flag) {
        this.flag = flag;
    }

    public boolean scheduled() {
        return flag;
    }

    public String name() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Job job = (Job) o;
        return Objects.equals(name, job.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}