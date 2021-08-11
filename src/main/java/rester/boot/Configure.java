package rester.boot;

import ch.qos.logback.classic.Level;
import rester.core.Dispatcher;
import rester.core.RestContext;
import rester.core.Task;
import rester.model.input.ConfigInput;
import rester.tools.JSON;
import rester.tools.LOG;
import rester.tools.YAML;

import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;

public class Configure {
    private static final Logger LOGGER = LOG.getLog("Configure");

    private List<Task> tasks;
    private RestContext context;

    public Configure config(String path) throws IOException {
        ConfigInput input = null;
        if (path.endsWith("yaml")) {
            input = YAML.parse(path, ConfigInput.class);
            System.out.println(input);
        }
        if (path.endsWith("json")) {
            input = JSON.fromJson(new String(Files.readAllBytes(Paths.get(path))), ConfigInput.class);
        }
        if (input == null) {
            LOGGER.error("please specify valid config file");
            System.exit(-1);
        }
        preConfig(input.getLogLevel());
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<ConfigInput>> validate = validator.validate(input, Default.class);
        if (validate != null && !validate.isEmpty()) {
            for (ConstraintViolation<ConfigInput> cv : validate) {
                LOGGER.error("model config validate error:{}:{}", cv.getPropertyPath().toString(), cv.getMessage());
            }
            System.exit(-1);
        }
        input.setWorkDir(new File(path).getCanonicalFile().getParent());
        input.setCurrentDir(new File(".").getCanonicalPath());
        context = RestContext.inst();
        tasks = context.load(input);
        if (tasks.isEmpty()) {
            LOGGER.warn("no rest tasks to exec,exit");
            System.exit(-1);
        }
        return this;
    }

    private void preConfig(Level level) {
        LOG.setLevel(level);
    }

    public void start() {
        Dispatcher.dispatch(context, tasks);
    }
}
