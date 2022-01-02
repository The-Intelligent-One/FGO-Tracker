package com.github.theintelligentone.fgotracker.service.datamanagement.user;

import com.github.theintelligentone.fgotracker.domain.item.Inventory;
import com.github.theintelligentone.fgotracker.domain.item.UpgradeMaterial;
import com.github.theintelligentone.fgotracker.domain.item.UpgradeMaterialCost;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class InventoryManagementService {
    @Getter
    private Inventory inventory;

    private Inventory createInventoryWithAssociatedMatList(Inventory inventory, List<UpgradeMaterial> materials) {
        Inventory inventoryToSave = inventory;
        if (inventory.getInventory().size() == 0) {
            inventoryToSave = createEmptyInventory(materials);
        } else {
            for (UpgradeMaterial mat : materials) {
                Optional<UpgradeMaterialCost> optionalMat = inventoryToSave.getInventory().stream()
                        .filter(upgradeMaterialCost -> upgradeMaterialCost.getId() == mat.getId())
                        .findAny();
                if (optionalMat.isPresent()) {
                    optionalMat.get().setItem(mat);
                } else {
                    UpgradeMaterialCost newMat = new UpgradeMaterialCost();
                    newMat.setId(mat.getId());
                    newMat.setItem(mat);
                    newMat.setAmount(0);
                    inventoryToSave.getInventory().add(newMat);
                }
            }

            for (UpgradeMaterialCost mat : inventoryToSave.getInventory()) {
                mat.setItem(materials.stream()
                        .filter(material -> material.getId() == mat.getId())
                        .findFirst().get());
            }
        }
        inventoryToSave.setLabel("Inventory");
        return inventoryToSave;
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
        return inventory;
    }
}
