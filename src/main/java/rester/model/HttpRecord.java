package rester.model;

import lombok.Data;

import java.util.Map;
import java.util.Objects;

@Data
public class HttpRecord implements Comparable<HttpRecord> {
    private String taskName;
    private int index;
    private String method;
    private String url;
    private String body;
    private Map<String, String> header;
    private int code;
    private String resp;
    private String reason;
    private String succeedAsserts;
    private String failedAsserts;
    private long cost;

    private String identity() {
        return taskName + "-" + index;
    }

    @Override
    public int compareTo(HttpRecord o) {
        return identity().compareTo(o.identity());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HttpRecord that = (HttpRecord) o;
        return index == that.index && Objects.equals(taskName, that.taskName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskName, index);
    }
}