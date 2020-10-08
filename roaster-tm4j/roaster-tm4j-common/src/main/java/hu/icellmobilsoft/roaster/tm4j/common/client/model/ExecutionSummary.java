package hu.icellmobilsoft.roaster.tm4j.common.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExecutionSummary {

    @JsonProperty("Pass")
    private Integer pass;

    @JsonProperty("Fail")
    private Integer fail;
}