package rester.model.input;

import lombok.Data;

import org.hibernate.validator.constraints.NotBlank;

import java.util.Map;

@Data
public class RequestTemplate {
    private String method = "get";
    @NotBlank
    private String url;
    private String body;
    private String bodyPath;
    private Map<String, String> header;
    private String timeout;
    private String basicAuth;
    private String asserts;
}
