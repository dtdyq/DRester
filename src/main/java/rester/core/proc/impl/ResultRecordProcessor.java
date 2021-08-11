package rester.core.proc.impl;

import rester.core.Task;
import rester.core.proc.Processor;
import rester.model.HttpContainer;
import rester.model.HttpRecord;

public class ResultRecordProcessor extends Processor {
    @Override
    public void proc(Task task, int index, HttpContainer container) {
        HttpRecord record = new HttpRecord();
        record.setTaskName(task.getName());
        record.setCode(container.getResponse().getCode());
        record.setCost(container.getResponse().getCost());
        record.setIndex(index);
        record.setMethod(container.getRequest().getMethod());
        record.setUrl(container.getRequest().getUrl());
        record.setBody(container.getRequest().getBody());
        record.setHeader(container.getRequest().getHeader());
        record.setResp(container.getResponse().getBody().replaceAll(System.lineSeparator(), ""));
        record.setReason(container.getResponse().getExceptMsg());
        record.setSucceedAsserts(String.join(",", container.getResponse().getSucceedAsserts()));
        record.setFailedAsserts(String.join(",", container.getResponse().getFailedAsserts()));

        task.getRecords().add(record);
        if (!String.valueOf(container.getResponse().getCode()).startsWith("2")) {
            task.getFailedIndex().add(index);
        }
        nextProc(task, index, container);
    }
}
