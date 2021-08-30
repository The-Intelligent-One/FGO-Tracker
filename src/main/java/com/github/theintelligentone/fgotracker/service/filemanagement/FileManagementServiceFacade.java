package com.github.theintelligentone.fgotracker.service.filemanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.theintelligentone.fgotracker.domain.event.BasicEvent;
import com.github.theintelligentone.fgotracker.domain.item.Inventory;
import com.github.theintelligentone.fgotracker.domain.item.UpgradeMaterial;
import com.github.theintelligentone.fgotracker.domain.other.CardPlacementData;
import com.github.theintelligentone.fgotracker.domain.other.VersionDTO;
import com.github.theintelligentone.fgotracker.domain.servant.ManagerServant;
import com.github.theintelligentone.fgotracker.domain.servant.PlannerServant;
import com.github.theintelligentone.fgotracker.domain.servant.Servant;
import com.github.theintelligentone.fgotracker.domain.servant.UserServant;
import com.github.theintelligentone.fgotracker.service.filemanagement.cache.CacheFileServiceFacade;
import com.github.theintelligentone.fgotracker.service.filemanagement.user.UserFileServiceFacade;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;
import java.util.Map;

@Slf4j
public class FileManagementServiceFacade {
    private final CacheFileServiceFacade cacheFileServiceFacade;
    private final UserFileServiceFacade userFileServiceFacade;
    private final ImportFileService importFileService;

    public FileManagementServiceFacade(ObjectMapper objectMapper) {
        FileService fileService = new FileService(objectMapper);
        cacheFileServiceFacade = new CacheFileServiceFacade(fileService);
        userFileServiceFacade = new UserFileServiceFacade(fileService);
        importFileService = new ImportFileService();
    }

    public void loadOfflineData() {
        cacheFileServiceFacade.loadOfflineData();
    }

    public List<String[]> importRosterCsv(File sourceFile) {
        return importFileService.importRosterCsv(sourceFile);
    }

    public List<String[]> importPlannerCsv(File sourceFile) {
        return importFileService.importPlannerCsv(sourceFile);
    }

    public Map<String, Integer> importInventoryCsv(File sourceFile) {
        return importFileService.importInventoryCsv(sourceFile);
    }

    public List<ManagerServant> loadManagerLookupTable() {
        return importFileService.loadManagerLookupTable();
    }

    public void saveMaterialData(List<UpgradeMaterial> materials, String gameRegion) {
        cacheFileServiceFacade.saveMaterialData(materials, gameRegion);
    }

    public void saveFullServantData(List<Servant> servants, String gameRegion) {
        cacheFileServiceFacade.saveFullServantData(servants, gameRegion);
    }

    public void saveClassAttackRate(Map<String, Integer> classAttackRate) {
        cacheFileServiceFacade.saveClassAttackRateData(classAttackRate);
    }

    public void saveCardData(Map<String, Map<Integer, CardPlacementData>> cardDataMap) {
        cacheFileServiceFacade.saveCardData(cardDataMap);
    }

    public void saveNewVersion(Map<String, VersionDTO> versionMap) {
        cacheFileServiceFacade.saveCurrentVersion(versionMap);
    }

    public void saveRoster(List<UserServant> servants) {
        userFileServiceFacade.saveRoster(servants);
    }

    public void savePlannerServants(List<PlannerServant> servants) {
        userFileServiceFacade.savePlannerServants(servants);
    }

    public void savePriorityServants(List<PlannerServant> servants) {
        userFileServiceFacade.savePriorityPlannerServants(servants);
    }

    public void saveInventory(Inventory inventory) {
        userFileServiceFacade.saveInventory(inventory);
    }

    public void saveDarkMode(boolean darkMode) {
        userFileServiceFacade.saveDarkMode(darkMode);
    }

    public void saveGameRegion(String gameRegion) {
        userFileServiceFacade.saveGameRegion(gameRegion);
    }

    public void saveBasicEventData(List<BasicEvent> basicEvents, String gameRegion) {
        cacheFileServiceFacade.saveBasicEventData(basicEvents, gameRegion);
    }

    public List<UpgradeMaterial> loadMaterialData(String gameRegion) {
        return cacheFileServiceFacade.loadMaterialData(gameRegion);
    }

    public List<Servant> loadFullServantData(String gameRegion) {
        return cacheFileServiceFacade.loadFullServantData(gameRegion);
    }

    public Map<String, Integer> loadClassAttackRate() {
        return cacheFileServiceFacade.loadClassAttackRate();
    }

    public Map<String, Map<Integer, CardPlacementData>> loadCardData() {
        return cacheFileServiceFacade.loadCardData();
    }

    public Map<String, VersionDTO> loadCurrentVersion() {
        return cacheFileServiceFacade.loadCurrentVersion();
    }

    public List<UserServant> loadRoster() {
        return userFileServiceFacade.loadRoster();
    }

    public List<PlannerServant> loadPlannedServantData() {
        return userFileServiceFacade.loadPlanner();
    }

    public List<PlannerServant> loadPriorityServantData() {
        return userFileServiceFacade.loadPriorityPlanner();
    }

    public Inventory loadInventory() {
        return userFileServiceFacade.loadInventory();
    }

    public boolean loadDarkMode() {
        return userFileServiceFacade.loadDarkMode();
    }

    public String loadGameRegion() {
        return userFileServiceFacade.loadGameRegion();
    }

    public List<BasicEvent> loadBasicEventData(String gameRegion) {
        return cacheFileServiceFacade.loadBasicEventData(gameRegion);
    }

    public void loadImageForMaterial(UpgradeMaterial material) {
        cacheFileServiceFacade.loadImageForMaterial(material);
    }
}
