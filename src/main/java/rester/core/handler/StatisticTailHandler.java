package rester.core.handler;

import rester.core.Task;
import rester.model.HttpRecord;

public class StatisticTailHandler implements TailHandler {
    @Override
    public void handle(Task task) {
        task.getStatistic().setName(task.getName());
        task.getStatistic().setRequestCnt(task.getCfg().getReqTimes());
        task.getStatistic().setFailedCnt(task.getFailedIndex().size());
        task.getStatistic().setSucceedCnt(task.getStatistic().getRequestCnt() - task.getStatistic().getFailedCnt());
        task.getStatistic().setEndTime(System.currentTimeMillis());
        task.getStatistic().setTaskCost(task.getStatistic().getEndTime() - task.getStatistic().getStartTime());
        if (task.getStatistic().getSucceedCnt() <= 0) {
            task.getStatistic().setSucceedAvgCost(0);
            task.getStatistic().setSucceedCost(0);
        } else {
            long total = task.getRecords()
                .stream()
                .filter(record -> String.valueOf(record.getCode()).startsWith("2"))
                .mapToLong(HttpRecord::getCost)
                .sum();
            task.getStatistic().setSucceedCost(total);
            task.getStatistic().setSucceedAvgCost(total / task.getStatistic().getSucceedCnt());
        }
    }
}
