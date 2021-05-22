package com.github.theintelligentone.fgotracker.domain.view;

import javafx.collections.ObservableList;
import lombok.Data;

@Data
public class InventoryView {
    private String label;
    private ObservableList<UpgradeMaterialCostView> inventory;
}
