package com.github.theintelligentone.fgotracker.service.datamanagement.user;

import com.github.theintelligentone.fgotracker.domain.item.Inventory;
import com.github.theintelligentone.fgotracker.domain.item.UpgradeMaterial;
import com.github.theintelligentone.fgotracker.domain.other.PlannerType;
import com.github.theintelligentone.fgotracker.domain.servant.PlannerServant;
import com.github.theintelligentone.fgotracker.domain.servant.Servant;
import com.github.theintelligentone.fgotracker.domain.servant.UserServant;
import com.github.theintelligentone.fgotracker.domain.view.InventoryView;
import com.github.theintelligentone.fgotracker.domain.view.PlannerServantView;
import com.github.theintelligentone.fgotracker.domain.view.UserServantView;
import com.github.theintelligentone.fgotracker.service.transformer.UserServantToViewTransformer;
import javafx.collections.ObservableList;

import java.util.List;

public class UserDataManagementServiceFacade {
    private final UserServantManagementService userServantManagementService;
    private final InventoryManagementService inventoryManagementService;
    private final PlannerManagementService plannerManagementService;

    public UserDataManagementServiceFacade(UserServantToViewTransformer userServantToViewTransformer) {
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

    public void initDataLists() {
        plannerManagementService.initDataLists();
        userServantManagementService.initDataLists(plannerManagementService.getPaddedPlannerServantList(PlannerType.REGULAR),
                plannerManagementService.getPaddedPlannerServantList(PlannerType.PRIORITY));
    }

    public void refreshUserServants(List<UserServant> loadedRoster, List<Servant> servantList) {
        userServantManagementService.refreshUserServants(loadedRoster, servantList);
    }

    public void refreshInventory(Inventory loadedInventory, List<UpgradeMaterial> materials) {
        inventoryManagementService.refreshInventory(loadedInventory, materials);
    }

    public void refreshPlannerServants(PlannerType plannerType, List<PlannerServant> loadedPlannedServantData) {
        plannerManagementService.refreshPlannerServants(plannerType, loadedPlannedServantData,
                userServantManagementService.getPaddedUserServantList());
    }

    public Inventory createEmptyInventory(List<UpgradeMaterial> materials) {
        return inventoryManagementService.createEmptyInventory(materials);
    }

    public void saveImportedUserServants(List<UserServantView> importedServants) {
        userServantManagementService.saveImportedUserServants(importedServants);
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

    public void replaceBaseServantInRow(int index, UserServantView servant, Servant newBaseServant) {
        userServantManagementService.replaceBaseServantInRow(index, servant, newBaseServant);
    }

    public void replaceBaseServantInPlannerRow(int index, PlannerServantView servant, String newServantName,
                                               PlannerType plannerType) {
        UserServantView newBaseServant = userServantManagementService.findUserServantByFormattedName(newServantName);
        plannerManagementService.replaceBaseServantInPlannerRow(index, servant, newBaseServant, plannerType);
    }

    public List<UserServant> getClearedUserServantList() {
        return userServantManagementService.getClearedUserServantList();
    }

    public Inventory getExportInventory() {
        return inventoryManagementService.getExportInventory();
    }

    public List<PlannerServant> getClearedPlannerServantList(PlannerType plannerType) {
        return plannerManagementService.getClearedPlannerServantList(plannerType);
    }

    public InventoryView getInventory() {
        return inventoryManagementService.getInventory();
    }

    public void saveImportedPlannerServants(PlannerType plannerType, List<PlannerServantView> importedServants) {
        plannerManagementService.saveImportedPlannerServants(plannerType, importedServants);
    }

    public ObservableList<String> getUserServantNameList() {
        return userServantManagementService.getUserServantNameList();
    }
}