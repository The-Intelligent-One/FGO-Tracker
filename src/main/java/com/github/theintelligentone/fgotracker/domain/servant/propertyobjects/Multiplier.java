package com.github.theintelligentone.fgotracker.domain.servant.propertyobjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Multiplier {
    @JsonProperty("Rate")
    private int rate;
    @JsonProperty("Value")
    private int value;
}
