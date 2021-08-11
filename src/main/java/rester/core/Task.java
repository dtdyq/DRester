package rester.core;

import lombok.Data;
import rester.model.HttpRecord;
import rester.model.input.RequestTemplate;
import rester.model.Statistic;
import rester.model.input.TaskCfg;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListSet;

@Data
public class Task {
    private String name;
    private Map<String, List<String>> args = new HashMap<>();
    private int index;
    private Statistic statistic = new Statistic();
    private RequestTemplate template;
    private TaskCfg cfg = new TaskCfg();
    private RestContext context;
    private TaskExecutor taskExecutor;
    private ConcurrentSkipListSet<Integer> failedIndex = new ConcurrentSkipListSet<>();
    private ConcurrentSkipListSet<HttpRecord> records = new ConcurrentSkipListSet<>();

    public String getArg(String key, int index) {
        if ("index".equals(key)) {
            return String.valueOf(index);
        }
        List<String> valList = args.containsKey(key) ? args.get(key) : context.getGlobalArgs().getOrDefault(key, null);
        if (valList == null) {
            return key;
        }
        if (index >= valList.size()) {
            return valList.get(valList.size() - 1);
        }
        return valList.get(index);
    }
}
