package com.github.theintelligentone.fgotracker.service.transformer;

import com.github.theintelligentone.fgotracker.domain.item.Inventory;
import com.github.theintelligentone.fgotracker.domain.view.InventoryView;
import javafx.collections.FXCollections;

import java.util.stream.Collectors;

public class InventoryToViewTransformer {
    private final UpgradeMaterialCostToViewTransformer matToViewTransformer;

    public InventoryToViewTransformer() {
        this.matToViewTransformer = new UpgradeMaterialCostToViewTransformer();
    }

    public InventoryView transform(Inventory inventory) {
        InventoryView result = new InventoryView();
        result.setLabel(inventory.getLabel());
        result.setInventory(FXCollections.observableArrayList(
                inventory.getInventory().stream().map(matToViewTransformer::transform).collect(Collectors.toList())));
        return result;
    }

    public Inventory transform(InventoryView inventory) {
        Inventory result = new Inventory();
        result.setLabel(inventory.getLabel());
        result.setInventory(inventory.getInventory().stream().map(matToViewTransformer::transform).collect(Collectors.toList()));
        return result;
    }
}
