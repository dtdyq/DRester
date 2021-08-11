package rester.model.input;

import ch.qos.logback.classic.Level;
import lombok.Data;
import rester.model.assertion.Assertion;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

@Data
public class ConfigInput {
    @NotBlank
    private String name;
    private Level logLevel = Level.INFO;
    private TaskExecPolicy taskExecPolicy;
    private ArgsInput args;
    @NotEmpty
    @Valid
    private List<TaskInput> tasks;
    private String workDir;
    private String currentDir;
    private Boolean statisticEnable;
    @Valid
    private List<Assertion> assertions = new ArrayList<>();
}
