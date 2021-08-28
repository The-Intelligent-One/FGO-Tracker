package com.github.theintelligentone.fgotracker.service.filemanagement.user;

import com.github.theintelligentone.fgotracker.domain.item.Inventory;
import com.github.theintelligentone.fgotracker.domain.servant.PlannerServant;
import com.github.theintelligentone.fgotracker.domain.servant.UserServant;
import com.github.theintelligentone.fgotracker.service.filemanagement.FileService;

import java.util.List;

public class UserFileServiceFacade {
    private final RosterFileService rosterFileService;
    private final PlannerFileService plannerFileService;
    private final InventoryFileService inventoryFileService;
    private final OptionsFileService optionsFileService;

    public UserFileServiceFacade(FileService fileService) {
        rosterFileService = new RosterFileService(fileService);
        plannerFileService = new PlannerFileService(fileService);
        inventoryFileService = new InventoryFileService(fileService);
        optionsFileService = new OptionsFileService(fileService);
    }

    public void saveRoster(List<UserServant> servants) {
        rosterFileService.saveRoster(servants);
    }

    public List<UserServant> loadRoster() {
        return rosterFileService.loadRoster();
    }

    public void savePlannerServants(List<PlannerServant> servants) {
        plannerFileService.savePlannerServants(servants);
    }

    public void savePriorityPlannerServants(List<PlannerServant> servants) {
        plannerFileService.savePriorityPlannerServants(servants);
    }

    public void saveInventory(Inventory inventory) {
        inventoryFileService.saveInventory(inventory);
    }

    public void saveDarkMode(boolean darkMode) {
        optionsFileService.saveDarkMode(darkMode);
    }

    public void saveGameRegion(String gameRegion) {
        optionsFileService.saveGameRegion(gameRegion);
    }

    public List<PlannerServant> loadPlanner() {
        return plannerFileService.loadPlanner();
    }

    public List<PlannerServant> loadPriorityPlanner() {
        return plannerFileService.loadPriorityPlanner();
    }

    public Inventory loadInventory() {
        return inventoryFileService.loadInventory();
    }

    public boolean loadDarkMode() {
        return optionsFileService.loadDarkMode();
    }

    public String loadGameRegion() {
        return optionsFileService.loadGameRegion();
    }
}
