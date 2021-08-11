package rester.core.handler;

import rester.core.Task;

public class CleanTailHandler implements TailHandler {
    @Override
    public void handle(Task task) {
        task.getFailedIndex().clear();
    }
}
