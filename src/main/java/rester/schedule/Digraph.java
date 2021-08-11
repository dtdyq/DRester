package rester.schedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Digraph {
    private Set<Job> jobs;
    private Map<Job, List<Job>> map;

    public Digraph() {
        this.jobs = new HashSet<>();
        this.map = new HashMap<>();
    }

    public void addDependency(Job job, Job prev) {
        if (!jobs.contains(job) || !jobs.contains(prev)) {
            throw new IllegalArgumentException();
        }
        List<Job> pre = map.computeIfAbsent(job, k -> new ArrayList<>());
        if (pre.contains(prev)) {
            throw new IllegalArgumentException();
        }
        pre.add(prev);
    }

    public void addDependencies(Job job, List<Job> dependencies) {
        dependencies.forEach(tmp -> addDependency(job, tmp));
    }

    public void addJob(Job job) {
        if (jobs.contains(job)) {
            throw new IllegalArgumentException();
        }
        jobs.add(job);
    }

    public void remove(Job job) {
        if (!jobs.contains(job)) {
            return;
        }
        map.remove(job);
        for (List<Job> set : map.values()) {
            set.remove(job);
        }
    }

    public Set<Job> getJobs() {
        return jobs;
    }

    public void addJobs(List<Job> jobs) {
        this.jobs.addAll(jobs);
    }

    public List<Job> getDependencies(Job job) {
        return map.get(job);
    }
}