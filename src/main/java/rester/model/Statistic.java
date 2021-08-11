package rester.model;

import lombok.Data;

@Data
public class Statistic {
    private String name;
    private int requestCnt;
    private int thread;
    private int failedCnt;
    private int succeedCnt;
    private long startTime;
    private long endTime;
    private long taskCost;
    private long succeedCost;
    private long succeedAvgCost;
}
