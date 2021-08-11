package rester.core.proc.impl;

import rester.core.Task;
import rester.core.proc.Processor;
import rester.model.HttpContainer;
import rester.tools.LOG;

import org.slf4j.Logger;

public class PreLogProcessor extends Processor {
    private static final Logger LOGGER = LOG.getLog("PreLog");

    @Override
    public void proc(Task task, int index, HttpContainer container) {
        LOGGER.info(
            String.format("start to request for task %s index %s:req method %s url %s body %s", task.getName(), index,
                container.getRequest().getMethod(), container.getRequest().getUrl(), container.getRequest().getBody()));

        nextProc(task, index, container);
    }
}
