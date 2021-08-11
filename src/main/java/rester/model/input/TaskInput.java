package rester.model.input;

import lombok.Data;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
public class TaskInput {
    @NotBlank
    private String name;
    @NotNull
    @Valid
    private RequestTemplate template;
    private ArgsInput args;
    @Valid
    private TaskCfg cfg;
}
