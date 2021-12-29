package com.github.theintelligentone.fgotracker.service.datamanagement.cache;

import com.github.theintelligentone.fgotracker.domain.event.BasicEvent;
import com.github.theintelligentone.fgotracker.domain.item.UpgradeMaterial;
import com.github.theintelligentone.fgotracker.domain.servant.BasicServant;
import com.github.theintelligentone.fgotracker.domain.servant.Servant;
import javafx.collections.ObservableList;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CacheManagementServiceFacade {
    @Autowired
    private VersionManagementService versionManagementService;
    @Autowired
    private ServantManagementService servantManagementService;
    @Autowired
    private MaterialManagementService materialManagementService;
    @Autowired
    private EventManagementService eventManagementService;

    @Getter
    private boolean dataLoaded;

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
        eventManagementService.saveBasicEventData(versionManagementService.getGameRegion());
        versionManagementService.saveVersion();
    }

    public void saveCachedFullServantData() {
        servantManagementService.saveCachedFullServantData(versionManagementService.getGameRegion());
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

    public Servant getServantById(long id) {
        return servantManagementService.getServantById(id, versionManagementService.getGameRegion());
    }

    public List<BasicServant> getBasicServantList() {
        return servantManagementService.getBasicServantDataList();
    }
}
