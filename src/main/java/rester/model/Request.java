package rester.model;

import lombok.Data;
import rester.model.assertion.Assertion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class Request {
    private String method;
    private String url;
    private String body;
    private Map<String, String> header;
    private String basicAuthUser;
    private String basicAuthPwd;
    private Integer timeout;
    private List<Assertion> assertions = new ArrayList<>();
}
