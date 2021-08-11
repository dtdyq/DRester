package rester.core.proc.impl;

import rester.core.Task;
import rester.core.proc.Processor;
import rester.model.HttpContainer;
import rester.tools.LOG;

import org.slf4j.Logger;

public class PostLogProcessor extends Processor {
    private static final Logger LOGGER = LOG.getLog("PostLog");

    @Override
    public void proc(Task task, int index, HttpContainer container) {
        LOGGER.info(String.format(
            "end request for task %s index %s cost:%s ms, code:%s, resp:%s, error:%s, succeed asserts:%s, failed asserts:%s",
            task.getName(), index, container.getResponse().getCost(), container.getResponse().getCode(),
            container.getResponse().getBody(), container.getResponse().getExceptMsg(),
            String.join(",", container.getResponse().getSucceedAsserts()),
            String.join(",", container.getResponse().getFailedAsserts())));
        nextProc(task, index, container);
    }
}
