package com.github.theintelligentone.fgotracker.domain.item;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpgradeMaterialCost {
    private long id;
    private UpgradeMaterial item;
    private int amount;
}
