package com.github.theintelligentone.fgotracker.service.datamanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.theintelligentone.fgotracker.domain.item.Inventory;
import com.github.theintelligentone.fgotracker.domain.item.UpgradeMaterial;
import com.github.theintelligentone.fgotracker.domain.other.PlannerType;
import com.github.theintelligentone.fgotracker.domain.servant.Servant;
import com.github.theintelligentone.fgotracker.domain.view.InventoryView;
import com.github.theintelligentone.fgotracker.domain.view.PlannerServantView;
import com.github.theintelligentone.fgotracker.domain.view.UserServantView;
import com.github.theintelligentone.fgotracker.service.datamanagement.user.UserDataManagementServiceFacade;
import com.github.theintelligentone.fgotracker.service.filemanagement.FileManagementServiceFacade;
import com.github.theintelligentone.fgotracker.service.transformer.UserServantToViewTransformer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataManagementServiceFacade {
    public static final String VERSION = "v0.2.4";
    public static final int[] MAX_LEVELS = {65, 60, 65, 70, 80, 90};

    public static final int MIN_TABLE_SIZE = 25;
    public static final String NAME_FORMAT = "%s [%d* %s]";

    private final BooleanProperty darkMode;
    private final FileManagementServiceFacade fileServiceFacade;

    private final ImportManagementService importManagementService;
    private final CacheManagementService cacheManagementService;
    private final UserDataManagementServiceFacade userDataManagementServiceFacade;

    public DataManagementServiceFacade() {
        darkMode = new SimpleBooleanProperty(true);
        ObjectMapper objectMapper = new ObjectMapper();
        DataRequestService requestService = new DataRequestService(objectMapper);
        fileServiceFacade = new FileManagementServiceFacade(objectMapper);
        cacheManagementService = new CacheManagementService(fileServiceFacade, requestService);
        UserServantToViewTransformer userServantToViewTransformer = new UserServantToViewTransformer();
        importManagementService = new ImportManagementService(fileServiceFacade, userServantToViewTransformer);
        userDataManagementServiceFacade = new UserDataManagementServiceFacade(userServantToViewTransformer);
    }

    public BooleanProperty darkModeProperty() {
        return darkMode;
    }

    public ObservableList<PlannerServantView> getPaddedPlannerServantList(PlannerType plannerType) {
        return userDataManagementServiceFacade.getPaddedPlannerServantList(plannerType);
    }

    public ObservableList<UserServantView> getUserServantList() {
        return userDataManagementServiceFacade.getPaddedUserServantList();
    }

    public boolean isDataLoaded() {
        return cacheManagementService.isDataLoaded();
    }

    public void initApp(String selectedRegion) {
        cacheManagementService.initApp(selectedRegion);
        userDataManagementServiceFacade.initDataLists();
        refreshAllData();
    }

    private void refreshAllData() {
        darkMode.set(fileServiceFacade.loadDarkMode());
        userDataManagementServiceFacade.refreshUserServants(fileServiceFacade.loadRoster(),
                cacheManagementService.getServantList());
        userDataManagementServiceFacade.refreshInventory(fileServiceFacade.loadInventory(),
                cacheManagementService.getMaterials());
        userDataManagementServiceFacade.refreshPlannerServants(PlannerType.REGULAR, fileServiceFacade.loadPlannedServantData());
        userDataManagementServiceFacade.refreshPlannerServants(PlannerType.PRIORITY, fileServiceFacade.loadPriorityServantData());
    }

    public void saveMaterialData() {
        cacheManagementService.saveMaterialData();
    }

    public Inventory createEmptyInventory() {
        return userDataManagementServiceFacade.createEmptyInventory(cacheManagementService.getMaterials());
    }

    public List<String> importUserServantsFromCsv(File sourceFile) {
        List<UserServantView> importedServants = new ArrayList<>();
        List<String> notFoundNames = importManagementService.importUserServantsFromCsv(sourceFile, importedServants,
                cacheManagementService.getServantList());
        userDataManagementServiceFacade.saveImportedUserServants(importedServants);
        return notFoundNames;
    }

    public void eraseUserServant(UserServantView servant) {
        userDataManagementServiceFacade.eraseUserServant(servant);
    }

    public void erasePlannerServant(PlannerServantView servant, PlannerType plannerType) {
        userDataManagementServiceFacade.erasePlannerServant(servant, plannerType);
    }

    public void removeUserServant(UserServantView servant) {
        userDataManagementServiceFacade.removeUserServant(servant);
    }

    public void removePlannerServant(PlannerServantView servant, PlannerType plannerType) {
        userDataManagementServiceFacade.removePlannerServant(servant, plannerType);
    }

    public void savePlannerServant(PlannerServantView servant, PlannerType plannerType) {
        userDataManagementServiceFacade.savePlannerServant(servant, plannerType);
    }

    public void savePlannerServant(int index, PlannerServantView servant, PlannerType plannerType) {
        userDataManagementServiceFacade.savePlannerServant(index, servant, plannerType);
    }

    public void saveUserServant(UserServantView servant) {
        userDataManagementServiceFacade.saveUserServant(servant);
    }

    public void saveUserServant(int index, UserServantView servant) {
        userDataManagementServiceFacade.saveUserServant(index, servant);
    }

    public void replaceBaseServantInRow(int index, UserServantView servant, String newServantName) {
        Servant newBaseServant = cacheManagementService.findServantByFormattedName(newServantName);
        userDataManagementServiceFacade.replaceBaseServantInRow(index, servant, newBaseServant);
    }

    public void replaceBaseServantInPlannerRow(int index, PlannerServantView servant, String newServantName,
                                               PlannerType plannerType) {
        userDataManagementServiceFacade.replaceBaseServantInPlannerRow(index, servant, newServantName, plannerType);
    }

    public void saveUserState() {
        fileServiceFacade.saveRoster(userDataManagementServiceFacade.getClearedUserServantList());
        fileServiceFacade.saveInventory(userDataManagementServiceFacade.getExportInventory());
        fileServiceFacade.savePlannerServants(userDataManagementServiceFacade.getClearedPlannerServantList(PlannerType.REGULAR));
        fileServiceFacade.savePriorityServants(
                userDataManagementServiceFacade.getClearedPlannerServantList(PlannerType.PRIORITY));
        fileServiceFacade.saveDarkMode(darkMode.getValue());
        fileServiceFacade.saveGameRegion(cacheManagementService.getGameRegion());
    }

    public List<String> importInventoryFromCsv(File sourceFile) {
        return importManagementService.createInventoryFromCsvLines(fileServiceFacade.importInventoryCsv(sourceFile),
                cacheManagementService.getMaterials(),
                userDataManagementServiceFacade.getInventory());
    }

    public List<String> importPlannerServantsFromCsv(File sourceFile, PlannerType plannerType) {
        List<PlannerServantView> importedServants = new ArrayList<>();
        List<String> notFoundNames = importManagementService.createPlannerServantListFromCsvLines(
                userDataManagementServiceFacade.getPaddedUserServantList(), cacheManagementService.getServantList(),
                importedServants,
                sourceFile);
        userDataManagementServiceFacade.saveImportedPlannerServants(plannerType, importedServants);
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
        return userDataManagementServiceFacade.getUserServantNameList();
    }

    public String getGameRegion() {
        return cacheManagementService.getGameRegion();
    }

    public InventoryView getInventory() {
        return userDataManagementServiceFacade.getInventory();
    }
}
