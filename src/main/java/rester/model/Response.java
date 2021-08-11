package rester.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class Response {
    private int code;
    private String body;
    private Map<String, String> header;
    private int cost;
    private String exceptMsg;
    private List<String> failedAsserts = new ArrayList<>();
    private List<String> succeedAsserts = new ArrayList<>();
}