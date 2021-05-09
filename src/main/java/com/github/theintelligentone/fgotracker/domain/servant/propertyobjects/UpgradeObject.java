package com.github.theintelligentone.fgotracker.domain.servant.propertyobjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpgradeObject {
    private List<UpgradeMaterialCost> items;
//    private long qp;
}
