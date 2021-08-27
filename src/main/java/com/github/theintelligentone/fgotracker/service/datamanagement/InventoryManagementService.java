package com.github.theintelligentone.fgotracker.service.datamanagement;

import com.github.theintelligentone.fgotracker.domain.item.Inventory;
import com.github.theintelligentone.fgotracker.domain.item.UpgradeMaterial;
import com.github.theintelligentone.fgotracker.domain.item.UpgradeMaterialCost;
import com.github.theintelligentone.fgotracker.domain.view.InventoryView;
import com.github.theintelligentone.fgotracker.service.transformer.InventoryToViewTransformer;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class InventoryManagementService {
    private final InventoryToViewTransformer inventoryToViewTransformer;
    @Getter
    private InventoryView inventory;

    public InventoryManagementService() {inventoryToViewTransformer = new InventoryToViewTransformer();}

    private InventoryView createInventoryWithAssociatedMatList(Inventory inventory, List<UpgradeMaterial> materials) {
        if (inventory.getInventory().size() == 0) {
            inventory = createEmptyInventory(materials);
        } else {
            for (UpgradeMaterialCost mat : inventory.getInventory()) {
                mat.setItem(materials.stream().filter(
                        material -> material.getId() == mat.getId()).findFirst().get());
            }
        }
        inventory.setLabel("Inventory");
        return inventoryToViewTransformer.transform(inventory);
    }

    public Inventory createEmptyInventory(List<UpgradeMaterial> materials) {
        Inventory inventory = new Inventory();
        List<UpgradeMaterialCost> inventoryList = new ArrayList<>();
        materials.forEach(material -> {
            UpgradeMaterialCost mat = new UpgradeMaterialCost();
            mat.setId(material.getId());
            mat.setItem(material);
            mat.setAmount(0);
            inventoryList.add(mat);
        });
        inventory.setInventory(inventoryList);
        return inventory;
    }

    public void refreshInventory(Inventory loadedInventory, List<UpgradeMaterial> materials) {
        inventory = createInventoryWithAssociatedMatList(loadedInventory, materials);
    }

    public Inventory getExportInventory() {
        return inventoryToViewTransformer.transform(inventory);
    }
}
