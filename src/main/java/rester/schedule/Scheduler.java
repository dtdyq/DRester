package rester.schedule;

import rester.tools.LOG;

import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Scheduler {
    private static final Logger LOGGER = LOG.getLog("Scheduler");

    public static void main(String[] args) {
        Digraph digraph = new Digraph();
        Job job1 = new Job("job1", new Runnable() {
            @Override
            public void run() {
                System.out.println("job1");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("job1 finish");
            }
        });
        Job job2 = new Job("job2", new Runnable() {
            @Override
            public void run() {
                System.out.println("job2");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("job2 finish");
            }
        });
        Job job3 = new Job("job3", new Runnable() {
            @Override
            public void run() {
                System.out.println("job3");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("job3 finish");

            }
        });
        Job job4 = new Job("job4", new Runnable() {
            @Override
            public void run() {
                System.out.println("job4");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("job4 finish");

            }
        });
        Job job5 = new Job("job5", new Runnable() {
            @Override
            public void run() {

                System.out.println("job5");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("job5 finish");
            }
        });
        Job job6 = new Job("job6", new Runnable() {
            @Override
            public void run() {

                System.out.println("job6");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("job6 finish");
            }
        });
        digraph.addJob(job1);
        digraph.addJob(job2);
        digraph.addJob(job3);
        digraph.addJob(job4);
        digraph.addJob(job5);
        digraph.addJob(job6);
        digraph.addDependency(job1, job2);
        digraph.addDependency(job1, job5);
        digraph.addDependency(job6, job2);
        digraph.addDependency(job2, job3);
        digraph.addDependency(job2, job4);

        schedule(digraph, Executors.newFixedThreadPool(10));
    }

    public static void schedule(Digraph digraph, ExecutorService executor) {
        List<List<Job>> jobSeq = new ArrayList<>();
        while (true) {
            List<Job> todo = digraph.getJobs()
                .stream()
                .filter(job -> !job.scheduled())
                .filter(job -> digraph.getDependencies(job) == null || digraph.getDependencies(job).isEmpty()
                    || digraph.getDependencies(job).stream().allMatch(Job::scheduled))
                .collect(Collectors.toList());
            if (!todo.isEmpty()) {
                jobSeq.add(todo);
                todo.forEach(job -> job.schedule(true));
            } else {
                break;
            }
        }
        LOGGER.info("jobs schedule sequence:{}", jobSeq);
        jobSeq.forEach(jobs -> {
            jobs.forEach(job -> job.execute(executor));
            jobs.forEach(Job::await);
        });
    }
}