package com.github.theintelligentone.fgotracker.service.datamanagement.user;

import com.github.theintelligentone.fgotracker.domain.item.Inventory;
import com.github.theintelligentone.fgotracker.domain.item.UpgradeMaterial;
import com.github.theintelligentone.fgotracker.domain.other.PlannerType;
import com.github.theintelligentone.fgotracker.domain.servant.PlannerServant;
import com.github.theintelligentone.fgotracker.domain.servant.Servant;
import com.github.theintelligentone.fgotracker.domain.servant.UserServant;
import com.github.theintelligentone.fgotracker.service.filemanagement.FileManagementServiceFacade;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserDataManagementServiceFacade {
    @Getter
    private final BooleanProperty darkMode;

    @Autowired
    private FileManagementServiceFacade fileServiceFacade;
    @Autowired
    private UserServantManagementService userServantManagementService;
    @Autowired
    private InventoryManagementService inventoryManagementService;

    public UserDataManagementServiceFacade() {
        darkMode = new SimpleBooleanProperty(true);
    }

    public ObservableList<PlannerServant> getPaddedPlannerServantList(PlannerType plannerType) {
        return userServantManagementService.getPaddedPlannerServantList(plannerType);
    }

    public ObservableList<UserServant> getPaddedUserServantList() {
        return userServantManagementService.getPaddedUserServantList();
    }

    public Inventory getInventory() {
        return inventoryManagementService.getInventory();
    }

    public void initDataLists() {
        userServantManagementService.initDataLists();
    }

    public void refreshAllData(List<Servant> servantList, List<UpgradeMaterial> materials) {
        darkMode.set(fileServiceFacade.loadDarkMode());
        userServantManagementService.refreshUserServants(fileServiceFacade.loadRoster(), servantList);
        inventoryManagementService.refreshInventory(fileServiceFacade.loadInventory(), materials);
        userServantManagementService.refreshPlannerServants(PlannerType.REGULAR, fileServiceFacade.loadPlannedServantData(), servantList);
        userServantManagementService.refreshPlannerServants(PlannerType.PRIORITY, fileServiceFacade.loadPriorityServantData(), servantList);
        userServantManagementService.refreshPlannerServants(PlannerType.LT, fileServiceFacade.loadLongTermPlannedServantData(), servantList);
    }

    public void saveUserState(String gameRegion) {
        fileServiceFacade.saveRoster(userServantManagementService.getClearedUserServantList());
        fileServiceFacade.saveInventory(inventoryManagementService.getExportInventory());
        fileServiceFacade.savePlannerServants(userServantManagementService.getClearedPlannerServantList(PlannerType.REGULAR));
        fileServiceFacade.savePriorityServants(userServantManagementService.getClearedPlannerServantList(PlannerType.PRIORITY));
        fileServiceFacade.saveLongTermServants(userServantManagementService.getClearedPlannerServantList(PlannerType.LT));
        fileServiceFacade.saveDarkMode(darkMode.getValue());
        fileServiceFacade.saveGameRegion(gameRegion);
    }

    public Inventory createEmptyInventory(List<UpgradeMaterial> materials) {
        return inventoryManagementService.createEmptyInventory(materials);
    }

    public void saveImportedUserServants(List<UserServant> importedServants) {
        userServantManagementService.saveImportedUserServants(importedServants);
    }

    public void saveImportedPlannerServants(PlannerType plannerType, List<PlannerServant> importedServants) {
        userServantManagementService.saveImportedPlannerServants(plannerType, importedServants);
    }

    public void savePlannerServant(PlannerServant servant, PlannerType plannerType) {
        userServantManagementService.savePlannerServant(servant, plannerType);
    }

    public void savePlannerServant(int index, PlannerServant servant, PlannerType plannerType) {
        userServantManagementService.savePlannerServant(index, servant, plannerType);
    }

    public void saveUserServant(UserServant servant) {
        userServantManagementService.saveUserServant(servant);
    }

    public void saveUserServant(int index, UserServant servant) {
        userServantManagementService.saveUserServant(index, servant);
    }

    public void eraseUserServant(int index) {
        userServantManagementService.eraseUserServant(index);
    }

    public void erasePlannerServant(PlannerServant servant, PlannerType plannerType) {
        userServantManagementService.erasePlannerServant(servant, plannerType);
    }

    public void removeUserServant(int index) {
        userServantManagementService.removeUserServant(index);
    }

    public void removePlannerServant(PlannerServant servant, PlannerType plannerType) {
        userServantManagementService.removePlannerServant(servant, plannerType);
    }

    public void replaceBaseServantInRow(int index, UserServant servant, Servant newBaseServant) {
        userServantManagementService.replaceBaseServantInRosterRow(index, servant, newBaseServant);
    }

    public void replaceBaseServantInPlannerRow(int index, PlannerServant servant, Servant newBaseServant,
                                               PlannerType plannerType) {
        userServantManagementService.replaceBaseServantInPlannerRow(index, servant, newBaseServant, plannerType);
    }
}
