package com.github.theintelligentone.fgotracker.ui.view;

import com.github.theintelligentone.fgotracker.domain.item.UpgradeMaterialCost;
import javafx.collections.ObservableList;
import lombok.Data;

@Data
public class InventoryView {
    private String label;
    private ObservableList<UpgradeMaterialCostView> inventory;
}
