package com.github.theintelligentone.fgotracker.service.datamanagement;

import com.github.theintelligentone.fgotracker.domain.event.BasicEvent;
import com.github.theintelligentone.fgotracker.domain.item.Inventory;
import com.github.theintelligentone.fgotracker.domain.item.UpgradeMaterial;
import com.github.theintelligentone.fgotracker.domain.other.PlannerType;
import com.github.theintelligentone.fgotracker.domain.servant.Servant;
import com.github.theintelligentone.fgotracker.domain.servant.UserServant;
import com.github.theintelligentone.fgotracker.domain.servant.factory.UserServantFactory;
import com.github.theintelligentone.fgotracker.domain.view.InventoryView;
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
    public static final String VERSION = "v0.3.3-hotfix";
    public static final int[] MAX_LEVELS = {65, 60, 65, 70, 80, 90};

    public static final int MIN_TABLE_SIZE = 25;
    public static final String NAME_FORMAT = "%s [%d* %s]";

        @Autowired
    private ImportManagementService importManagementService;
    @Autowired
    private CacheManagementServiceFacade cacheManagementService;
    @Autowired
    private UserDataManagementServiceFacade userDataManagementServiceFacade;

    public void initApp(String selectedRegion) {
        cacheManagementService.initApp(selectedRegion);
        userDataManagementServiceFacade.initDataLists();
        userDataManagementServiceFacade.refreshAllData(cacheManagementService.getServantList(), cacheManagementService.getMaterials());
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

    public ObservableList<UserServant> getPaddedPlannerServantList(PlannerType plannerType) {
        return userDataManagementServiceFacade.getPaddedPlannerServantList(plannerType);
    }

    public ObservableList<UserServant> getUserServantList() {
        return userDataManagementServiceFacade.getPaddedUserServantList();
    }

    public boolean isDataLoaded() {
        return cacheManagementService.isDataLoaded();
    }

    public List<String> importInventoryFromCsv(File sourceFile) {
        return importManagementService.createInventoryFromCsvLines(sourceFile,
                cacheManagementService.getMaterials(),
                userDataManagementServiceFacade.getInventory());
    }

    public List<String> importPlannerServantsFromCsv(File sourceFile, PlannerType plannerType) {
        List<UserServant> importedServants = new ArrayList<>();
        List<String> notFoundNames = importManagementService.createPlannerServantListFromCsvLines(
                userDataManagementServiceFacade.getPaddedUserServantList(),
                importedServants, cacheManagementService.getBasicServantList(),
                sourceFile);
        userDataManagementServiceFacade.saveImportedPlannerServants(plannerType, importedServants);
        return notFoundNames;
    }


    public void saveMaterialData() {
        cacheManagementService.saveMaterialData();
    }

    public Inventory createEmptyInventory() {
        return userDataManagementServiceFacade.createEmptyInventory(cacheManagementService.getMaterials());
    }

    public List<String> importUserServantsFromCsv(File sourceFile) {
        List<UserServant> importedServants = new ArrayList<>();
        List<String> notFoundNames = importManagementService.importUserServantsFromCsv(sourceFile, importedServants,
                cacheManagementService.getBasicServantList());
        importedServants.forEach(userServant -> {
            if (userServant.getSvtId() != 0){
                UserServantFactory.updateBaseServant(userServant, cacheManagementService.getServantById(userServant.getSvtId()));
            }
        });
        userDataManagementServiceFacade.saveImportedUserServants(importedServants);
        return notFoundNames;
    }

    public void saveUserState() {
        cacheManagementService.saveCachedFullServantData();
        userDataManagementServiceFacade.saveUserState(cacheManagementService.getGameRegion());
    }

    public void invalidateCache() {
        cacheManagementService.invalidateCache();
    }

    public void eraseUserServant(int index) {
        userDataManagementServiceFacade.eraseUserServant(index);
    }

    public void erasePlannerServant(UserServant servant, PlannerType plannerType) {
        userDataManagementServiceFacade.erasePlannerServant(servant, plannerType);
    }

    public void removeUserServant(int index) {
        userDataManagementServiceFacade.removeUserServant(index);
    }

    public void removePlannerServant(UserServant servant, PlannerType plannerType) {
        userDataManagementServiceFacade.removePlannerServant(servant, plannerType);
    }

    public void savePlannerServant(UserServant servant, PlannerType plannerType) {
        userDataManagementServiceFacade.savePlannerServant(servant, plannerType);
    }

    public void savePlannerServant(int index, UserServant servant, PlannerType plannerType) {
        userDataManagementServiceFacade.savePlannerServant(index, servant, plannerType);
    }

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

    public void replaceBaseServantInPlannerRow(int index, UserServant servant, String newServantName, PlannerType plannerType) {
        Servant newBaseServant = cacheManagementService.findServantByFormattedName(newServantName);
        userDataManagementServiceFacade.replaceBaseServantInPlannerRow(index, servant, newBaseServant, plannerType);
    }
}
