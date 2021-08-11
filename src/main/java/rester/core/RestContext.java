package rester.core;

import lombok.Data;
import rester.model.assertion.Assertion;
import rester.model.input.ConfigInput;
import rester.model.input.RequestTemplate;
import rester.model.input.TaskExecPolicy;
import rester.model.input.TaskInput;
import rester.tools.CSV;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

@Data
public class RestContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestContext.class);
    private String name;
    private TaskExecPolicy taskExecPolicy = TaskExecPolicy.serial;
    private Map<String, List<String>> globalArgs = new HashMap<>();
    private List<Assertion> assertions = new ArrayList<>();
    private String workDir;
    private boolean enableStatistic = true;

    private RestContext() {
    }

    public static RestContext inst() {
        return new RestContext();
    }

    public List<Task> load(ConfigInput configInput) throws IOException {
        LOGGER.debug("begin to resolve rest suite:{}", configInput);
        name = configInput.getName();
        workDir = configInput.getWorkDir();
        Optional.ofNullable(configInput.getTaskExecPolicy()).ifPresent(policy -> taskExecPolicy = policy);

        Optional.ofNullable(configInput.getStatisticEnable()).ifPresent(aBoolean -> enableStatistic = aBoolean);

        Optional.ofNullable(configInput.getArgs()).ifPresent(argsInput -> {
            Optional.ofNullable(argsInput.getKvArgs())
                .ifPresent(map -> map
                    .forEach((key, val) -> globalArgs.put(key, Collections.singletonList(String.valueOf(val)))));
            Optional.ofNullable(argsInput.getCsvArgs())
                .ifPresent(csvPaths -> procCSVArgs(csvPaths, configInput)
                    .forEach((key, valList) -> globalArgs.put(key, valList)));
            Optional.ofNullable(argsInput.getPropertyArgs())
                .ifPresent(csvPaths -> procPropertiesArgs(csvPaths, configInput)
                    .forEach((key, valList) -> globalArgs.put(key, valList)));
        });

        Optional.ofNullable(configInput.getAssertions()).ifPresent(as -> this.assertions = as);

        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < configInput.getTasks().size(); i++) {
            TaskInput taskInput = configInput.getTasks().get(i);
            if (taskInput.getCfg() != null && taskInput.getCfg().getSkip()) {
                continue;
            }
            Task task = new Task();
            task.setName(taskInput.getName());

            Optional.ofNullable(taskInput.getArgs()).ifPresent(argsInput -> {
                Optional.ofNullable(argsInput.getKvArgs())
                    .ifPresent(map -> map.forEach(
                        (key, val) -> task.getArgs().put(key, Collections.singletonList(String.valueOf(val)))));
                Optional.ofNullable(argsInput.getCsvArgs())
                    .ifPresent(csvPaths -> procCSVArgs(csvPaths, configInput)
                        .forEach((key, valList) -> task.getArgs().put(key, valList)));
                Optional.ofNullable(argsInput.getPropertyArgs())
                    .ifPresent(csvPaths -> procPropertiesArgs(csvPaths, configInput)
                        .forEach((key, valList) -> task.getArgs().put(key, valList)));
            });

            RequestTemplate template = taskInput.getTemplate();
            if (template.getBody() == null && template.getBodyPath() != null) {
                String realPath = findPath(configInput, template.getBodyPath());
                if (Files.exists(Paths.get(realPath))) {
                    template.setBody(new String(Files.readAllBytes(Paths.get(realPath))));
                }
            }
            task.setTemplate(template);
            task.setIndex(i);

            Optional.ofNullable(taskInput.getCfg()).ifPresent(task::setCfg);

            task.setContext(this);
            tasks.add(task);
        }
        LOGGER.debug("resolve rest context ret:{}", tasks);
        return tasks;
    }

    private Map<String, List<String>> procCSVArgs(List<String> csvArgs, ConfigInput input) {
        Map<String, List<String>> ret = new HashMap<>();
        Optional.ofNullable(csvArgs).orElse(new ArrayList<>()).forEach(csvPath -> {
            String csvDir = findPath(input, csvPath);
            if (!Files.exists(Paths.get(csvDir))) {
                LOGGER.error("can not find csv config file {},pass", csvPath);
            }
            CSV.read(csvDir).forEach(ret::put);
        });
        return ret;
    }

    private Map<String, List<String>> procPropertiesArgs(List<String> propertiesArgs, ConfigInput input) {
        Map<String, List<String>> ret = new HashMap<>();
        Optional.ofNullable(propertiesArgs).orElse(new ArrayList<>()).forEach(csvPath -> {
            String csvDir = findPath(input, csvPath);
            if (!Files.exists(Paths.get(csvDir))) {
                LOGGER.error("can not find properties config file {},pass", csvPath);
            }
            Properties properties = new Properties();
            try {
                properties.load(new FileReader(csvDir));
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (String key : properties.stringPropertyNames()) {
                ret.put(key, Collections.singletonList(properties.getProperty(key)));
            }
        });
        return ret;
    }

    private String findPath(ConfigInput input, String path) {
        String ret = path;
        if (!Files.exists(Paths.get(ret))) {
            ret = input.getWorkDir() + "/" + path;
        }
        if (!Files.exists(Paths.get(ret))) {
            ret = input.getCurrentDir() + "/" + path;
        }
        return ret;
    }

    boolean isSerial() {
        return taskExecPolicy == TaskExecPolicy.serial;
    }
}
