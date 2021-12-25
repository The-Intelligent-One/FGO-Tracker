package com.github.theintelligentone.fgotracker.service.datamanagement;

import com.github.theintelligentone.fgotracker.domain.event.BasicEvent;
import com.github.theintelligentone.fgotracker.domain.item.Inventory;
import com.github.theintelligentone.fgotracker.domain.item.UpgradeMaterial;
import com.github.theintelligentone.fgotracker.domain.other.PlannerType;
import com.github.theintelligentone.fgotracker.domain.servant.Servant;
import com.github.theintelligentone.fgotracker.domain.servant.UserServant;
import com.github.theintelligentone.fgotracker.domain.view.InventoryView;
//import com.github.theintelligentone.fgotracker.domain.view.PlannerServantView;
import com.github.theintelligentone.fgotracker.domain.view.UserServantView;
import com.github.theintelligentone.fgotracker.service.datamanagement.cache.CacheManagementServiceFacade;
import com.github.theintelligentone.fgotracker.service.datamanagement.user.UserDataManagementServiceFacade;
import javafx.beans.property.BooleanProperty;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataManagementServiceFacade {
    public static final String VERSION = "v0.3.2-hotfix";
    public static final int[] MAX_LEVELS = {65, 60, 65, 70, 80, 90};

    public static final int MIN_TABLE_SIZE = 25;
    public static final String NAME_FORMAT = "%s [%d* %s]";

//    @Autowired
//    private ImportManagementService importManagementService;
    @Autowired
    private CacheManagementServiceFacade cacheManagementService;
    @Autowired
    private UserDataManagementServiceFacade userDataManagementServiceFacade;

    public void initApp(String selectedRegion) {
        cacheManagementService.initApp(selectedRegion);
        userDataManagementServiceFacade.initDataLists();
        userDataManagementServiceFacade.refreshAllData(cacheManagementService.getServantList(),
                cacheManagementService.getMaterials());
    }

    public BooleanProperty darkModeProperty() {
        return userDataManagementServiceFacade.getDarkMode();
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

    public String getGameRegion() {
        return cacheManagementService.getGameRegion();
    }

    public List<BasicEvent> getBasicEvents() {
        return cacheManagementService.getBasicEvents();
    }

    public InventoryView getInventory() {
        return userDataManagementServiceFacade.getInventory();
    }

//    public ObservableList<UserServantView> getPaddedPlannerServantList(PlannerType plannerType) {
//        return userDataManagementServiceFacade.getPaddedPlannerServantList(plannerType);
//    }

    public ObservableList<UserServant> getUserServantList() {
        return userDataManagementServiceFacade.getPaddedUserServantList();
    }

    public boolean isDataLoaded() {
        return cacheManagementService.isDataLoaded();
    }

    public List<String> importInventoryFromCsv(File sourceFile) {
//        return importManagementService.createInventoryFromCsvLines(sourceFile,
//                cacheManagementService.getMaterials(),
//                userDataManagementServiceFacade.getInventory());
        return null;
    }

    public List<String> importPlannerServantsFromCsv(File sourceFile, PlannerType plannerType) {
//        List<UserServantView> importedServants = new ArrayList<>();
//        List<String> notFoundNames = importManagementService.createPlannerServantListFromCsvLines(
//                userDataManagementServiceFacade.getPaddedUserServantList(), cacheManagementService.getServantList(),
//                importedServants,
//                sourceFile);
//        userDataManagementServiceFacade.saveImportedPlannerServants(plannerType, importedServants);
//        return notFoundNames;
        return null;
    }


    public void saveMaterialData() {
        cacheManagementService.saveMaterialData();
    }

    public Inventory createEmptyInventory() {
        return userDataManagementServiceFacade.createEmptyInventory(cacheManagementService.getMaterials());
    }

    public List<String> importUserServantsFromCsv(File sourceFile) {
//        List<UserServantView> importedServants = new ArrayList<>();
//        List<String> notFoundNames = importManagementService.importUserServantsFromCsv(sourceFile, importedServants,
//                cacheManagementService.getServantList());
//        userDataManagementServiceFacade.saveImportedUserServants(importedServants);
//        return notFoundNames;
        return null;
    }

    public void saveUserState() {
        cacheManagementService.saveCachedFullServantData();
        userDataManagementServiceFacade.saveUserState(cacheManagementService.getGameRegion());
    }

    public void invalidateCache() {
        cacheManagementService.invalidateCache();
    }

    public void eraseUserServant(UserServant servant) {
        userDataManagementServiceFacade.eraseUserServant(servant);
    }

//    public void erasePlannerServant(UserServantView servant, PlannerType plannerType) {
//        userDataManagementServiceFacade.erasePlannerServant(servant, plannerType);
//    }

    public void removeUserServant(UserServant servant) {
        userDataManagementServiceFacade.removeUserServant(servant);
    }

//    public void removePlannerServant(UserServantView servant, PlannerType plannerType) {
//        userDataManagementServiceFacade.removePlannerServant(servant, plannerType);
//    }
//
//    public void savePlannerServant(UserServantView servant, PlannerType plannerType) {
//        userDataManagementServiceFacade.savePlannerServant(servant, plannerType);
//    }
//
//    public void savePlannerServant(int index, UserServantView servant, PlannerType plannerType) {
//        userDataManagementServiceFacade.savePlannerServant(index, servant, plannerType);
//    }

    public void saveUserServant(UserServant servant) {
        userDataManagementServiceFacade.saveUserServant(servant);
    }

    public void saveUserServant(int index, UserServant servant) {
        userDataManagementServiceFacade.saveUserServant(index, servant);
    }

    public void replaceBaseServantInRow(int index, UserServant servant, String newServantName) {
        Servant newBaseServant = cacheManagementService.findServantByFormattedName(newServantName);
        userDataManagementServiceFacade.replaceBaseServantInRow(index, servant, newBaseServant);
    }

//    public void replaceBaseServantInPlannerRow(int index, UserServantView servant, String newServantName,
//                                               PlannerType plannerType) {
//        userDataManagementServiceFacade.replaceBaseServantInPlannerRow(index, servant, newServantName, plannerType);
//    }
}
