package rester.tools;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * description
 *
 * @author third party management
 * @since NCE IP V1R18C00, 2018/1/8
 */
public final class JSON {
    /**
     * description
     */
    private static final String PARSER_OBJECT_ERROR = "Parser to object error.";
    private static final Logger LOGGER = LoggerFactory.getLogger(JSON.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 私有构造函数，避免外部实例化
     */
    private JSON() {
    }

    /**
     * object to json
     *
     * @param obj data
     * @param <T> t
     * @return data str
     */
    public static <T> String toJson(final T obj) {
        try {
            return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (final IOException ex) {
            LOGGER.error("Parser to json error.", ex);
            throw new IllegalArgumentException("Parser obj to json error, obj = " + obj, ex);
        }
    }

    /**
     * fromJson
     *
     * @param jsonStr jsonStr
     * @param objClass objClass
     * @param <T> T
     * @return str
     */
    public static <T> T fromJson(final String jsonStr, final Class<T> objClass) {
        try {
            if (jsonStr == null) {
                LOGGER.error("The jsonStr is empty");
                throw new IllegalArgumentException("Exception: the jsonStr is empty");
            }

            // 兼容性
            return MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .readValue(jsonStr, objClass);
        } catch (IOException ex) {
            LOGGER.error(PARSER_OBJECT_ERROR, ex);
            throw new IllegalArgumentException("Parser json to object error, expect class = " + objClass, ex);
        }
    }

    /**
     * find value from json str
     * 
     * @param data data json
     * @param field key
     * @return value
     * @throws JsonProcessingException e
     */
    public static JsonNode valueFromJsonStr(String data, String field) throws JsonProcessingException {
        LOGGER.debug("resolve field:{} in data:{}", field, data);
        JsonNode node = new ObjectMapper().readTree(data).findValue(field);
        LOGGER.debug("field {} value:{}", field, node);
        return node;
    }
}
