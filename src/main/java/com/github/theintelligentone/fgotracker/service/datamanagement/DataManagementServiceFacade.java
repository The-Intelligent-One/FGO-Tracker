package com.github.theintelligentone.fgotracker.service.datamanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataManagementServiceFacade {
    public static final String VERSION = "v0.2.3";
    public static final int[] MAX_LEVELS = {65, 60, 65, 70, 80, 90};

    public static final int MIN_TABLE_SIZE = 25;
    public static final String NAME_FORMAT = "%s [%d* %s]";

    private final BooleanProperty darkMode;
    private final FileManagementServiceFacade fileServiceFacade;

    private final ImportManagementService importManagementService;
    private final CacheManagementService cacheManagementService;
    private final UserServantManagementService userServantManagementService;
    private final InventoryManagementService inventoryManagementService;
    private final PlannerManagementService plannerManagementService;

    public DataManagementServiceFacade() {
        darkMode = new SimpleBooleanProperty(true);
        ObjectMapper objectMapper = new ObjectMapper();
        DataRequestService requestService = new DataRequestService(objectMapper);
        fileServiceFacade = new FileManagementServiceFacade(objectMapper);
        UserServantToViewTransformer userServantToViewTransformer = new UserServantToViewTransformer();
        importManagementService = new ImportManagementService(fileServiceFacade, userServantToViewTransformer);
        userServantManagementService = new UserServantManagementService(userServantToViewTransformer);
        cacheManagementService = new CacheManagementService(fileServiceFacade, requestService);
        inventoryManagementService = new InventoryManagementService();
        plannerManagementService = new PlannerManagementService();
    }

    public BooleanProperty darkModeProperty() {
        return darkMode;
    }

    public ObservableList<PlannerServantView> getPaddedPlannerServantList(PlannerType plannerType) {
        return plannerManagementService.getPaddedPlannerServantList(plannerType);
    }

    public ObservableList<UserServantView> getUserServantList() {
        return userServantManagementService.getPaddedUserServantList();
    }

    public boolean isDataLoaded() {
        return cacheManagementService.isDataLoaded();
    }

    public void initApp(String selectedRegion) {
        cacheManagementService.initApp(selectedRegion);
        initDataLists();
        refreshAllData();
    }

    private void initDataLists() {
        plannerManagementService.initDataLists();
        userServantManagementService.initDataLists(plannerManagementService.getPaddedPlannerServantList(PlannerType.REGULAR),
                plannerManagementService.getPaddedPlannerServantList(PlannerType.PRIORITY));
    }

    private void refreshAllData() {
        darkMode.set(fileServiceFacade.loadDarkMode());
        userServantManagementService.refreshUserServants(fileServiceFacade.loadRoster(), cacheManagementService.getServantList());
        inventoryManagementService.refreshInventory(fileServiceFacade.loadInventory(), cacheManagementService.getMaterials());
        plannerManagementService.refreshPlannerServants(PlannerType.REGULAR, fileServiceFacade.loadPlannedServantData(),
                userServantManagementService.getPaddedUserServantList());
        plannerManagementService.refreshPlannerServants(PlannerType.PRIORITY, fileServiceFacade.loadPriorityServantData(),
                userServantManagementService.getPaddedUserServantList());
    }

    public void saveMaterialData() {
        cacheManagementService.saveMaterialData();
    }

    public Inventory createEmptyInventory() {
        return inventoryManagementService.createEmptyInventory(cacheManagementService.getMaterials());
    }

    public List<String> importUserServantsFromCsv(File sourceFile) {
        List<UserServantView> importedServants = new ArrayList<>();
        List<String> notFoundNames = importManagementService.importUserServantsFromCsv(sourceFile, importedServants,
                cacheManagementService.getServantList());
        userServantManagementService.saveImportedUserServants(importedServants);
        return notFoundNames;
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

    public void savePlannerServant(PlannerServantView plannerServantView, PlannerType plannerType) {
        plannerManagementService.savePlannerServant(plannerServantView, plannerType);
    }

    public void savePlannerServant(int index, PlannerServantView plannerServantView, PlannerType plannerType) {
        plannerManagementService.savePlannerServant(index, plannerServantView, plannerType);
    }

    public void saveUserServant(UserServantView servant) {
        userServantManagementService.saveUserServant(servant);
    }

    public void saveUserServant(int index, UserServantView servant) {
        userServantManagementService.saveUserServant(index, servant);
    }

    public void replaceBaseServantInRow(int index, UserServantView servant, String newServantName) {
        Servant newBaseServant = cacheManagementService.findServantByFormattedName(newServantName);
        userServantManagementService.replaceBaseServantInRow(index, servant, newBaseServant);
    }

    public void replaceBaseServantInPlannerRow(int index, PlannerServantView servant, String newServantName,
                                               PlannerType plannerType) {
        UserServantView newBaseServant = userServantManagementService.findUserServantByFormattedName(newServantName);
        plannerManagementService.replaceBaseServantInPlannerRow(index, servant, newBaseServant, plannerType);
    }

    public void saveUserState() {
        fileServiceFacade.saveRoster(userServantManagementService.getClearedUserServantList());
        fileServiceFacade.saveInventory(inventoryManagementService.getExportInventory());
        fileServiceFacade.savePlannerServants(plannerManagementService.getClearedPlannerServantList(PlannerType.REGULAR));
        fileServiceFacade.savePriorityServants(plannerManagementService.getClearedPlannerServantList(PlannerType.PRIORITY));
        fileServiceFacade.saveDarkMode(darkMode.getValue());
        fileServiceFacade.saveGameRegion(cacheManagementService.getGameRegion());
    }

    public List<String> importInventoryFromCsv(File sourceFile) {
        return importManagementService.createInventoryFromCsvLines(fileServiceFacade.importInventoryCsv(sourceFile),
                cacheManagementService.getMaterials(),
                inventoryManagementService.getInventory());
    }

    public List<String> importPlannerServantsFromCsv(File sourceFile, PlannerType plannerType) {
        List<PlannerServantView> importedServants = new ArrayList<>();
        List<String> notFoundNames = importManagementService.createPlannerServantListFromCsvLines(
                userServantManagementService.getPaddedUserServantList(), cacheManagementService.getServantList(),
                importedServants,
                sourceFile);
        plannerManagementService.saveImportedPlannerServants(plannerType, importedServants);
        return notFoundNames;
    }

    public void invalidateCache() {
        cacheManagementService.invalidateCache();
    }

    public ObservableList<String> getServantNameList() {
        return cacheManagementService.getServantNameList();
    }

    public boolean isIconsNotResized() {
        return !cacheManagementService.isIconsResized();
    }

    public List<UpgradeMaterial> getMaterials() {
        return cacheManagementService.getMaterials();
    }

    public ObservableList<String> getUserServantNameList() {
        return userServantManagementService.getUserServantNameList();
    }

    public String getGameRegion() {
        return cacheManagementService.getGameRegion();
    }

    public InventoryView getInventory() {
        return inventoryManagementService.getInventory();
    }
}
