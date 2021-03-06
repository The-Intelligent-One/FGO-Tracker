package com.github.theintelligentone.fgotracker.service.datamanagement.user;

import com.github.theintelligentone.fgotracker.domain.item.Inventory;
import com.github.theintelligentone.fgotracker.domain.item.UpgradeMaterial;
import com.github.theintelligentone.fgotracker.domain.other.PlannerType;
import com.github.theintelligentone.fgotracker.domain.servant.Servant;
import com.github.theintelligentone.fgotracker.domain.view.InventoryView;
import com.github.theintelligentone.fgotracker.domain.view.PlannerServantView;
import com.github.theintelligentone.fgotracker.domain.view.UserServantView;
import com.github.theintelligentone.fgotracker.service.filemanagement.FileManagementServiceFacade;
import com.github.theintelligentone.fgotracker.service.transformer.UserServantToViewTransformer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import lombok.Getter;

import java.util.List;

public class UserDataManagementServiceFacade {
    @Getter
    private final BooleanProperty darkMode;

    private final FileManagementServiceFacade fileServiceFacade;
    private final UserServantManagementService userServantManagementService;
    private final InventoryManagementService inventoryManagementService;
    private final PlannerManagementService plannerManagementService;

    public UserDataManagementServiceFacade(
            FileManagementServiceFacade fileServiceFacade,
            UserServantToViewTransformer userServantToViewTransformer) {
        this.fileServiceFacade = fileServiceFacade;
        darkMode = new SimpleBooleanProperty(true);
        userServantManagementService = new UserServantManagementService(userServantToViewTransformer);
        inventoryManagementService = new InventoryManagementService();
        plannerManagementService = new PlannerManagementService();
    }

    public ObservableList<PlannerServantView> getPaddedPlannerServantList(PlannerType plannerType) {
        return plannerManagementService.getPaddedPlannerServantList(plannerType);
    }

    public ObservableList<UserServantView> getPaddedUserServantList() {
        return userServantManagementService.getPaddedUserServantList();
    }

    public InventoryView getInventory() {
        return inventoryManagementService.getInventory();
    }

    public ObservableList<String> getUserServantNameList() {
        return userServantManagementService.getUserServantNameList();
    }

    public void initDataLists() {
        plannerManagementService.initDataLists();
        userServantManagementService.initDataLists(plannerManagementService.getPaddedPlannerServantList(PlannerType.REGULAR),
                plannerManagementService.getPaddedPlannerServantList(PlannerType.PRIORITY));
    }

    public void refreshAllData(List<Servant> servantList, List<UpgradeMaterial> materials) {
        darkMode.set(fileServiceFacade.loadDarkMode());
        userServantManagementService.refreshUserServants(fileServiceFacade.loadRoster(), servantList);
        inventoryManagementService.refreshInventory(fileServiceFacade.loadInventory(), materials);
        plannerManagementService.refreshPlannerServants(PlannerType.REGULAR, fileServiceFacade.loadPlannedServantData(),
                userServantManagementService.getPaddedUserServantList());
        plannerManagementService.refreshPlannerServants(PlannerType.PRIORITY, fileServiceFacade.loadPriorityServantData(),
                userServantManagementService.getPaddedUserServantList());
    }

    public void saveUserState(String gameRegion) {
        fileServiceFacade.saveRoster(userServantManagementService.getClearedUserServantList());
        fileServiceFacade.saveInventory(inventoryManagementService.getExportInventory());
        fileServiceFacade.savePlannerServants(plannerManagementService.getClearedPlannerServantList(PlannerType.REGULAR));
        fileServiceFacade.savePriorityServants(plannerManagementService.getClearedPlannerServantList(PlannerType.PRIORITY));
        fileServiceFacade.saveDarkMode(darkMode.getValue());
        fileServiceFacade.saveGameRegion(gameRegion);
    }

    public Inventory createEmptyInventory(List<UpgradeMaterial> materials) {
        return inventoryManagementService.createEmptyInventory(materials);
    }

    public void saveImportedUserServants(List<UserServantView> importedServants) {
        userServantManagementService.saveImportedUserServants(importedServants);
    }

    public void saveImportedPlannerServants(PlannerType plannerType, List<PlannerServantView> importedServants) {
        plannerManagementService.saveImportedPlannerServants(plannerType, importedServants);
    }

    public void savePlannerServant(PlannerServantView servant, PlannerType plannerType) {
        plannerManagementService.savePlannerServant(servant, plannerType);
    }

    public void savePlannerServant(int index, PlannerServantView servant, PlannerType plannerType) {
        plannerManagementService.savePlannerServant(index, servant, plannerType);
    }

    public void saveUserServant(UserServantView servant) {
        userServantManagementService.saveUserServant(servant);
    }

    public void saveUserServant(int index, UserServantView servant) {
        userServantManagementService.saveUserServant(index, servant);
    }

    public void eraseUserServant(UserServantView servant) {
        userServantManagementService.eraseUserServant(servant);
    }

    public void erasePlannerServant(PlannerServantView servant, PlannerType plannerType) {
        plannerManagementService.erasePlannerServant(servant, plannerType);
    }

    public void removeUserServant(UserServantView servant) {
        userServantManagementService.removeUserServant(servant);
    }

    public void removePlannerServant(PlannerServantView servant, PlannerType plannerType) {
        plannerManagementService.removePlannerServant(servant, plannerType);
    }

    public void replaceBaseServantInRow(int index, UserServantView servant, Servant newBaseServant) {
        userServantManagementService.replaceBaseServantInRow(index, servant, newBaseServant);
    }

    public void replaceBaseServantInPlannerRow(int index, PlannerServantView servant, String newServantName,
                                               PlannerType plannerType) {
        UserServantView newBaseServant = userServantManagementService.findUserServantByFormattedName(newServantName);
        plannerManagementService.replaceBaseServantInPlannerRow(index, servant, newBaseServant, plannerType);
    }
}
