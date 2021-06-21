package com.github.theintelligentone.fgotracker.service.filemanagement.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.theintelligentone.fgotracker.domain.item.Inventory;
import com.github.theintelligentone.fgotracker.domain.item.UpgradeMaterialCost;
import com.github.theintelligentone.fgotracker.service.filemanagement.FileService;

import java.util.List;

public class InventoryFileService {
    private static final String INVENTORY_FILE = "inventory.json";

    private final FileService fileService;

    public InventoryFileService(FileService fileService) {this.fileService = fileService;}

    public void saveInventory(Inventory inventory) {
        fileService.saveUserData(inventory.getInventory(), INVENTORY_FILE);
    }

    public Inventory loadInventory() {
        List<UpgradeMaterialCost> matList = fileService.loadUserDataList(INVENTORY_FILE, new TypeReference<>() {});
        Inventory inventory = new Inventory();
        inventory.setLabel("Inventory");
        inventory.setInventory(matList);
        return inventory;
    }
}
