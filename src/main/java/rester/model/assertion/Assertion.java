package rester.model.assertion;

import lombok.Data;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Data
public class Assertion {
    @NotBlank
    private String id;
    @NotNull
    private Ref ref;
    @NotNull
    private Operator oper;
    @NotBlank
    private String expect;
}