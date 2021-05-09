package com.github.theintelligentone.fgotracker.domain.servant.propertyobjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpgradeMaterialCost {
    private UpgradeMaterial item;
    private int amount;
}
