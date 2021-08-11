package rester.core;

import rester.model.HttpRecord;
import rester.tools.LOG;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Statisticer {
    private static final Logger LOGGER = LOG.getLog("Statistic");

    static void summary(List<Task> tasks) throws IOException {
        String gName = tasks.get(0).getContext().getName();
        StringBuilder ret = new StringBuilder();
        ret.append("run ").append(gName).append(" finished").append(System.lineSeparator());
        ret.append("name\t")
            .append("reqCnt\t")
            .append("failed\t")
            .append("totalCost(s)\t")
            .append("succeedCost(s)\t")
            .append("avgCost(s)")
            .append(System.lineSeparator());
        tasks.stream()
            .map(Task::getStatistic)
            .forEach(statistic -> ret.append(statistic.getName())
                .append("\t")
                .append(statistic.getRequestCnt())
                .append("\t")
                .append(statistic.getFailedCnt())
                .append("\t")
                .append(statistic.getTaskCost() / 1000.0)
                .append("\t")
                .append(statistic.getSucceedCost() / 1000.0)
                .append("\t")
                .append(statistic.getSucceedAvgCost() / 1000.0)
                .append(System.lineSeparator()));
        System.out.println("=======================statistic=======================");
        System.out.println(ret);
        if (!tasks.get(0).getContext().isEnableStatistic()) {
            return;
        }
        Path html = Paths.get(tasks.get(0).getContext().getWorkDir() + "\\" + gName + ".html");
        VelocityEngine engine = new VelocityEngine();
        Map<String, Object> global = new HashMap<>();

        global.put("name", gName);
        global.put("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-mm-dd HH:MM:ss.SSS")));
        List<Map<String, Object>> summaries = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            Map<String, Object> summary = new HashMap<>();
            summary.put("index", i);
            summary.put("thread", task.getStatistic().getThread());
            summary.put("name", task.getName());
            summary.put("reqCnt", task.getStatistic().getRequestCnt());
            summary.put("failed", task.getStatistic().getFailedCnt());
            summary.put("totalCost", task.getStatistic().getTaskCost() / 1000.0);
            summary.put("succeedCost", task.getStatistic().getSucceedCost() / 1000.0);
            summary.put("avgCost", task.getStatistic().getSucceedAvgCost() / 1000.0);
            summaries.add(summary);
        }
        global.put("summaries", summaries);

        List<Map<String, Object>> details = new ArrayList<>();
        tasks.forEach(task -> {
            Map<String, Object> detail = new LinkedHashMap<>();
            detail.put("name", task.getName());
            List<Map<String, Object>> requests = new ArrayList<>();
            task.getRecords().stream().sorted(Comparator.comparingInt(HttpRecord::getIndex)).forEach(record -> {
                Map<String, Object> request = new HashMap<>();
                request.put("id", record.getIndex());
                request.put("method", record.getMethod());
                request.put("url", StringEscapeUtils.escapeHtml(record.getUrl()));
                request.put("body",
                    StringUtils.isBlank(record.getBody()) ? "no body" : StringEscapeUtils.escapeHtml(record.getBody()));
                request.put("code", record.getCode());
                request.put("ret", getRet(record));
                request.put("cost", record.getCost());
                request.put("asserts", record.getSucceedAsserts() + " / " + record.getFailedAsserts());
                if (!record.getFailedAsserts().isEmpty()) {
                    request.put("assertFail", true);
                } else {
                    request.put("assertFail", false);
                }
                requests.add(request);
            });
            detail.put("requests", requests);
            details.add(detail);
        });
        global.put("details", details);

        VelocityContext context = new VelocityContext();
        context.put("global", global);

        StringWriter writer = new StringWriter();
        engine.evaluate(context, writer, "test",
            new InputStreamReader(Statisticer.class.getResourceAsStream("/statistic.vm"), StandardCharsets.UTF_8));
        Files.write(html, writer.toString().getBytes());

        System.out.println("see detail in " + html.toString());
    }

    private static String getRet(HttpRecord record) {
        return StringUtils.isBlank(record.getResp()) ? StringUtils.isBlank(record.getReason())
            ? "no content"
            : StringEscapeUtils.escapeHtml(record.getReason()) : StringEscapeUtils.escapeHtml(record.getResp());
    }

}
