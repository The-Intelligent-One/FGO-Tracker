package com.github.theintelligentone.fgotracker.service.filemanagement.user;

import com.github.theintelligentone.fgotracker.domain.item.Inventory;
import com.github.theintelligentone.fgotracker.domain.servant.UserServant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserFileServiceFacade {
    @Autowired
    private RosterFileService rosterFileService;
    @Autowired
    private PlannerFileService plannerFileService;
    @Autowired
    private InventoryFileService inventoryFileService;
    @Autowired
    private OptionsFileService optionsFileService;

    public void saveRoster(List<UserServant> servants) {
        rosterFileService.saveRoster(servants);
    }

    public List<UserServant> loadRoster() {
        return rosterFileService.loadRoster();
    }

    public void savePlannerServants(List<UserServant> servants) {
        plannerFileService.savePlannerServants(servants);
    }

    public void savePriorityPlannerServants(List<UserServant> servants) {
        plannerFileService.savePriorityPlannerServants(servants);
    }

    public void saveLongTermPlannerServants(List<UserServant> servants) {
        plannerFileService.saveLongTermPlannerServants(servants);
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

    public List<UserServant> loadPlanner() {
        return plannerFileService.loadPlanner();
    }

    public List<UserServant> loadPriorityPlanner() {
        return plannerFileService.loadPriorityPlanner();
    }

    public List<UserServant> loadLongTermPlanner() {
        return plannerFileService.loadLongTermPlanner();
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
