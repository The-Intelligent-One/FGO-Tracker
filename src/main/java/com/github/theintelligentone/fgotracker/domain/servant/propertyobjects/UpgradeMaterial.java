package com.github.theintelligentone.fgotracker.domain.servant.propertyobjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpgradeMaterial {
    private long id;
    private String name;
    private String icon;
    private String background;
}
