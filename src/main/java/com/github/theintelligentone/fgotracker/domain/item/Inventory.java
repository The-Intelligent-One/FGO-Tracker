package com.github.theintelligentone.fgotracker.domain.item;

import lombok.Data;

import java.util.List;

@Data
public class Inventory {
    private String label;
    private List<UpgradeMaterialCost> inventory;
}
