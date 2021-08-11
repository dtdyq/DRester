package rester.model.input;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Min;

@Data
public class TaskCfg {

    private String proxy;
    private Boolean ssl = false;
    private Boolean skip = false;
    @Min(1)
    private Integer thread = 1;
    @Min(1)
    private Integer reqTimes = 1;
    private ThreadAssignPolicy threadAssignPolicy = ThreadAssignPolicy.segment;
    private Boolean retryFailedOnFinish = false;
    @Min(1)
    private Integer retryTimes = 1;
    private List<String> dependencies = new ArrayList<>();
    private Boolean exportFailedToJson = false;
    private Boolean stopOnFail = false;
}
