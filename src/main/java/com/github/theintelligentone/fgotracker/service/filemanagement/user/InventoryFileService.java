package com.github.theintelligentone.fgotracker.service.filemanagement.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.theintelligentone.fgotracker.domain.item.Inventory;
import com.github.theintelligentone.fgotracker.domain.item.UpgradeMaterialCost;
import com.github.theintelligentone.fgotracker.domain.view.JsonViews;
import com.github.theintelligentone.fgotracker.service.filemanagement.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InventoryFileService {
    private static final String INVENTORY_FILE = "inventory.json";

    @Autowired
    private FileService fileService;

    public void saveInventory(Inventory inventory) {
        fileService.saveUserData(inventory.getInventory(), INVENTORY_FILE, null);
    }

    public Inventory loadInventory() {
        List<UpgradeMaterialCost> matList = fileService.loadUserDataList(INVENTORY_FILE, new TypeReference<>() {});
        Inventory inventory = new Inventory();
        inventory.setLabel("Inventory");
        inventory.setInventory(matList);
        return inventory;
    }
}
