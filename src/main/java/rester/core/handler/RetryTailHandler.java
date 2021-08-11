package rester.core.handler;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import lombok.SneakyThrows;
import rester.core.PartitionRunner;
import rester.core.Task;
import rester.tools.LOG;

import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RetryTailHandler implements TailHandler {
    private static final Logger LOGGER = LOG.getLog("Retryer");

    @SneakyThrows
    @Override
    public void handle(Task task) {
        if (task.getCfg().getRetryFailedOnFinish()) {
            List<Integer> failedList = new ArrayList<>(task.getFailedIndex());
            task.getFailedIndex().clear();
            failedList.sort(Integer::compareTo);
            LOGGER.info("failed requests id list:{},begin to recall", failedList);
            ExecutorService service = Executors.newSingleThreadExecutor(
                new ThreadFactoryBuilder().setPriority(Thread.MAX_PRIORITY).setNameFormat(task.getName()).build());
            service.submit(new PartitionRunner(task, failedList)).get();
            service.shutdown();
        }
    }
}
