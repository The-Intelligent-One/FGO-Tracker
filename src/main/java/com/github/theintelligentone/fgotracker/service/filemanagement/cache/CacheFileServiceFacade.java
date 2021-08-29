package com.github.theintelligentone.fgotracker.service.filemanagement.cache;

import com.github.theintelligentone.fgotracker.domain.event.BasicEvent;
import com.github.theintelligentone.fgotracker.domain.item.UpgradeMaterial;
import com.github.theintelligentone.fgotracker.domain.other.CardPlacementData;
import com.github.theintelligentone.fgotracker.domain.other.VersionDTO;
import com.github.theintelligentone.fgotracker.domain.servant.Servant;
import com.github.theintelligentone.fgotracker.service.filemanagement.FileService;

import java.util.List;
import java.util.Map;

public class CacheFileServiceFacade {
    private final MaterialFileService materialFileService;
    private final ServantFileService servantFileService;
    private final EventFileService eventFileService;
    private final StaticDataFileService staticDataFileService;

    public CacheFileServiceFacade(FileService fileService) {
        materialFileService = new MaterialFileService(fileService);
        servantFileService = new ServantFileService(fileService);
        staticDataFileService = new StaticDataFileService(fileService);
        eventFileService = new EventFileService(fileService);
    }

    public void loadOfflineData() {
        servantFileService.prepareOfflineServantData();
        materialFileService.prepareOfflineMaterialData();
        staticDataFileService.prepareOfflineStaticData();
    }

    public void saveMaterialData(List<UpgradeMaterial> materials, String gameRegion) {
        materialFileService.saveMaterialData(materials, gameRegion);
    }

    public void saveFullServantData(List<Servant> servants, String gameRegion) {
        servantFileService.saveFullServantData(servants, gameRegion);
    }

    public void saveClassAttackRateData(Map<String, Integer> classAttackRate) {
        staticDataFileService.saveClassAttackRateData(classAttackRate);
    }

    public void saveCardData(Map<String, Map<Integer, CardPlacementData>> cardDataMap) {
        staticDataFileService.saveCardData(cardDataMap);
    }

    public List<UpgradeMaterial> loadMaterialData(String gameRegion) {
        return materialFileService.loadMaterialData(gameRegion);
    }

    public List<Servant> loadFullServantData(String gameRegion) {
        return servantFileService.loadFullServantData(gameRegion);
    }

    public Map<String, Integer> loadClassAttackRate() {
        return staticDataFileService.loadClassAttackRate();
    }

    public Map<String, Map<Integer, CardPlacementData>> loadCardData() {
        return staticDataFileService.loadCardData();
    }

    public Map<String, VersionDTO> loadCurrentVersion() {
        return staticDataFileService.loadCurrentVersion();
    }

    public void saveCurrentVersion(Map<String, VersionDTO> versionMap) {
        staticDataFileService.saveCurrentVersion(versionMap);
    }

    public List<BasicEvent> loadBasicEventData(String gameRegion) {
        return eventFileService.loadBasicEventData(gameRegion);
    }

    public void saveBasicEventData(List<BasicEvent> basicEvents, String gameRegion) {
        eventFileService.saveBasicEventData(basicEvents, gameRegion);
    }
}
