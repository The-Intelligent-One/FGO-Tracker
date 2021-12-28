package com.github.theintelligentone.fgotracker.service.datamanagement.user;

import com.github.theintelligentone.fgotracker.domain.item.Inventory;
import com.github.theintelligentone.fgotracker.domain.item.UpgradeMaterial;
import com.github.theintelligentone.fgotracker.domain.other.PlannerType;
import com.github.theintelligentone.fgotracker.domain.servant.Servant;
import com.github.theintelligentone.fgotracker.domain.servant.UserServant;
import com.github.theintelligentone.fgotracker.domain.view.InventoryView;
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

    public ObservableList<UserServant> getPaddedPlannerServantList(PlannerType plannerType) {
        return userServantManagementService.getPaddedPlannerServantList(plannerType);
    }

    public ObservableList<UserServant> getPaddedUserServantList() {
        return userServantManagementService.getPaddedUserServantList();
    }

    public InventoryView getInventory() {
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
    }

    public void saveUserState(String gameRegion) {
        fileServiceFacade.saveRoster(userServantManagementService.getClearedUserServantList());
        fileServiceFacade.saveInventory(inventoryManagementService.getExportInventory());
        fileServiceFacade.savePlannerServants(userServantManagementService.getClearedPlannerServantList(PlannerType.REGULAR));
        fileServiceFacade.savePriorityServants(userServantManagementService.getClearedPlannerServantList(PlannerType.PRIORITY));
        fileServiceFacade.saveDarkMode(darkMode.getValue());
        fileServiceFacade.saveGameRegion(gameRegion);
    }

    public Inventory createEmptyInventory(List<UpgradeMaterial> materials) {
        return inventoryManagementService.createEmptyInventory(materials);
    }

    public void saveImportedUserServants(List<UserServant> importedServants) {
        userServantManagementService.saveImportedUserServants(importedServants);
    }

//    public void saveImportedPlannerServants(PlannerType plannerType, List<PlannerServantView> importedServants) {
//        plannerManagementService.saveImportedPlannerServants(plannerType, importedServants);
//    }

    public void savePlannerServant(UserServant servant, PlannerType plannerType) {
        userServantManagementService.savePlannerServant(servant, plannerType);
    }

    public void savePlannerServant(int index, UserServant servant, PlannerType plannerType) {
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

    public void erasePlannerServant(UserServant servant, PlannerType plannerType) {
        userServantManagementService.erasePlannerServant(servant, plannerType);
    }

    public void removeUserServant(int index) {
        userServantManagementService.removeUserServant(index);
    }

    public void removePlannerServant(UserServant servant, PlannerType plannerType) {
        userServantManagementService.removePlannerServant(servant, plannerType);
    }

    public void replaceBaseServantInRow(int index, UserServant servant, Servant newBaseServant) {
        userServantManagementService.replaceBaseServantInRosterRow(index, servant, newBaseServant);
    }

    public void replaceBaseServantInPlannerRow(int index, UserServant servant, Servant newBaseServant,
                                               PlannerType plannerType) {
        userServantManagementService.replaceBaseServantInPlannerRow(index, servant, newBaseServant, plannerType);
    }
}
