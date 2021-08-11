package rester.model.input;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ArgsInput {
    private Map<String, Object> kvArgs;
    private List<String> csvArgs;
    private List<String> propertyArgs;
}
