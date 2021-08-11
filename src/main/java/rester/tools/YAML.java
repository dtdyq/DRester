package rester.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public final class YAML {
    private static final Logger LOGGER = LoggerFactory.getLogger(YAML.class);

    public static <T> T parse(String path, Class<T> clz) {
        LOGGER.debug("begin to load yaml:{}", path);
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        try {
            return mapper.readValue(new File(path), clz);
        } catch (IOException e) {
            LOGGER.error("load yaml file error.", e);
        }
        return null;
    }
}
