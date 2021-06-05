package com.github.theintelligentone.fgotracker.domain.item;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpgradeCost {
    private List<UpgradeMaterialCost> items;
    private long qp;
}
