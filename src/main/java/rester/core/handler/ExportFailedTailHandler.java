package rester.core.handler;

import rester.core.Task;
import rester.model.HttpRecord;
import rester.tools.JSON;
import rester.tools.LOG;

import org.slf4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExportFailedTailHandler implements TailHandler {
    private static final Logger LOGGER = LOG.getLog("ExportFailed");

    @Override
    public void handle(Task task) {
        if (!task.getCfg().getExportFailedToJson()) {
            return;
        }
        LOGGER.info("begin to export failed req to json");
        List<Integer> ids = new ArrayList<>(task.getFailedIndex());
        if (!ids.isEmpty()) {
            List<Map<String, Object>> export = new ArrayList<>();
            ids.forEach(index -> {
                HttpRecord record = task.getRecords()
                    .stream()
                    .filter(respRecord -> respRecord.getIndex() == index)
                    .findFirst()
                    .orElse(new HttpRecord());
                Map<String, Object> one = new LinkedHashMap<>();
                one.put("method", record.getMethod());
                one.put("url", record.getUrl());
                one.put("body", record.getBody());
                one.put("header", record.getHeader());
                one.put("code", record.getCode());
                one.put("ret", record.getReason() == null ? record.getResp() : record.getReason());
                export.add(one);
            });
            String fName = task.getContext().getWorkDir() + "/" + task.getName() + "-failed-"
                + System.currentTimeMillis() + ".json";
            try {
                Files.write(Paths.get(fName), JSON.toJson(export).getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                LOGGER.error("export data to json error:{}", e.getMessage());
            }
        }
    }
}
