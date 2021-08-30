package com.github.theintelligentone.fgotracker.service.datamanagement.cache;

import com.github.theintelligentone.fgotracker.domain.event.BasicEvent;
import com.github.theintelligentone.fgotracker.domain.item.UpgradeMaterial;
import com.github.theintelligentone.fgotracker.domain.servant.Servant;
import com.github.theintelligentone.fgotracker.service.datamanagement.DataRequestService;
import com.github.theintelligentone.fgotracker.service.filemanagement.FileManagementServiceFacade;
import javafx.collections.ObservableList;
import lombok.Getter;

import java.util.List;

public class CacheManagementServiceFacade {
    private final VersionManagementService versionManagementService;
    private final ServantManagementService servantManagementService;
    private final MaterialManagementService materialManagementService;
    private final EventManagementService eventManagementService;

    @Getter
    private boolean dataLoaded;

    public CacheManagementServiceFacade(FileManagementServiceFacade fileServiceFacade,
                                        DataRequestService requestService) {
        dataLoaded = false;
        versionManagementService = new VersionManagementService(fileServiceFacade, requestService);
        servantManagementService = new ServantManagementService(fileServiceFacade, requestService);
        materialManagementService = new MaterialManagementService(fileServiceFacade, requestService);
        eventManagementService = new EventManagementService(fileServiceFacade, requestService);
    }

    public void initApp(String selectedRegion) {
        versionManagementService.loadGameRegion(selectedRegion);
        if (versionManagementService.newVersionAvailable()) {
            refreshCache();
        } else {
            loadFromCache();
        }
        servantManagementService.createServantNameList();
        dataLoaded = true;
    }

    public List<Servant> getServantList() {
        return servantManagementService.getServantDataList();
    }

    public String getGameRegion() {
        return versionManagementService.getGameRegion();
    }

    public ObservableList<String> getServantNameList() {
        return servantManagementService.getServantNameList();
    }

    public boolean isIconsResized() {
        return materialManagementService.isIconsResized();
    }

    public List<UpgradeMaterial> getMaterials() {
        return materialManagementService.getMaterials();
    }

    public List<BasicEvent> getBasicEvents() {
        return eventManagementService.getBasicEvents();
    }

    private void loadFromCache() {
        String gameRegion = versionManagementService.getGameRegion();
        servantManagementService.loadServantDataFromCache(gameRegion);
        materialManagementService.loadMaterialDataFromCache(gameRegion);
        eventManagementService.loadBasicEventDataFromCache(gameRegion);
    }

    private void refreshCache() {
        downloadNewData();
        saveNewDataToCache();
    }

    private void downloadNewData() {
        String gameRegion = versionManagementService.getGameRegion();
        servantManagementService.downloadNewServantData(gameRegion);
        materialManagementService.downloadNewMaterialData(gameRegion);
        eventManagementService.downloadNewBasicEventData(gameRegion);
    }

    private void saveNewDataToCache() {
        servantManagementService.saveServantDataToCache(versionManagementService.getGameRegion());
        servantManagementService.saveBasicServantDataToCache(versionManagementService.getGameRegion());
        eventManagementService.saveBasicEventData(versionManagementService.getGameRegion());
        versionManagementService.saveVersion();
    }

    public void saveMaterialData() {
        materialManagementService.saveMaterialData(versionManagementService.getGameRegion());
    }

    public void invalidateCache() {
        versionManagementService.invalidateCache();
    }

    public Servant findServantByFormattedName(String name) {
        return servantManagementService.findServantByFormattedName(name, versionManagementService.getGameRegion());
    }
}
