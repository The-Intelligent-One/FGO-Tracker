package com.github.theintelligentone.fgotracker.service.datamanagement;

import com.github.theintelligentone.fgotracker.domain.item.UpgradeMaterial;
import com.github.theintelligentone.fgotracker.domain.other.CardPlacementData;
import com.github.theintelligentone.fgotracker.domain.other.VersionDTO;
import com.github.theintelligentone.fgotracker.domain.servant.Servant;
import com.github.theintelligentone.fgotracker.service.filemanagement.FileManagementServiceFacade;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.theintelligentone.fgotracker.service.datamanagement.DataManagementServiceFacade.NAME_FORMAT;

public class CacheManagementService {
    public static Map<String, Integer> CLASS_ATTACK_MULTIPLIER;
    public static Map<String, Map<Integer, CardPlacementData>> CARD_DATA;

    private final FileManagementServiceFacade fileServiceFacade;
    private final DataRequestService requestService;

    @Getter
    private ObservableList<String> servantNameList;
    @Getter
    @Setter
    private String gameRegion;
    @Getter
    private List<UpgradeMaterial> materials;
    @Getter
    private boolean iconsResized;

    private List<Servant> servantDataList;
    private Map<String, VersionDTO> currentVersion;

    public CacheManagementService(FileManagementServiceFacade fileServiceFacade,
                                  DataRequestService requestService) {
        this.fileServiceFacade = fileServiceFacade;
        this.requestService = requestService;
        gameRegion = fileServiceFacade.loadGameRegion();
    }

    public boolean isDataLoaded() {
        return servantDataList != null && !servantDataList.isEmpty();
    }

    public void initApp(String selectedRegion) {
        if (gameRegion.isEmpty()) {
            gameRegion = selectedRegion;
            fileServiceFacade.saveGameRegion(gameRegion);
        }
        servantNameList = FXCollections.observableArrayList();
        refreshAllData();
    }

    private void refreshAllData() {
        if (newVersionAvailable()) {
            refreshCache();
        } else {
            loadFromCache();
        }
        servantNameList.addAll(
                servantDataList.stream().map(
                        svt -> String.format(NAME_FORMAT, svt.getName(), svt.getRarity(), svt.getClassName())).collect(
                        Collectors.toList()));
    }

    private void loadFromCache() {
        servantDataList = fileServiceFacade.loadFullServantData(gameRegion);
        materials = fileServiceFacade.loadMaterialData(gameRegion);
        iconsResized = true;
        CLASS_ATTACK_MULTIPLIER = fileServiceFacade.loadClassAttackRate();
        CARD_DATA = fileServiceFacade.loadCardData();
    }

    private void refreshCache() {
        iconsResized = false;
        downloadNewData();
        saveNewDataToCache();
    }

    private void saveNewDataToCache() {
        fileServiceFacade.saveFullServantData(servantDataList, gameRegion);
        fileServiceFacade.saveClassAttackRate(CLASS_ATTACK_MULTIPLIER);
        fileServiceFacade.saveCardData(CARD_DATA);
        fileServiceFacade.saveNewVersion(currentVersion);
    }

    public void saveMaterialData() {
        fileServiceFacade.saveMaterialData(materials, gameRegion);
    }

    private void downloadNewData() {
        servantDataList = requestService.getAllServantData(gameRegion);
        materials = requestService.getAllMaterialData(gameRegion);
        materials.forEach(material -> material.setIconImage(requestService.getImageForMaterial(material)));
        CLASS_ATTACK_MULTIPLIER = requestService.getClassAttackRate();
        CARD_DATA = requestService.getCardDetails();
    }

    private boolean newVersionAvailable() {
        currentVersion = fileServiceFacade.loadCurrentVersion();
        Map<String, VersionDTO> onlineVersion = requestService.getOnlineVersion();
        boolean needUpdate = false;
        if (!onlineVersion.isEmpty() && (onlineVersion.get(gameRegion).getTimestamp() > currentVersion.get(
                gameRegion).getTimestamp())) {
            needUpdate = true;
            currentVersion.put(gameRegion, onlineVersion.get(gameRegion));
        } else if (currentVersion.get(gameRegion).getTimestamp() == 0) {
            fileServiceFacade.loadOfflineData();
            currentVersion = fileServiceFacade.loadCurrentVersion();
        }
        return needUpdate;
    }

    public void invalidateCache() {
        for (Map.Entry<String, VersionDTO> entry : currentVersion.entrySet()) {
            entry.getValue().setTimestamp(0);
        }
        fileServiceFacade.saveNewVersion(currentVersion);
    }

    public Servant findServantByFormattedName(String name) {
        return servantDataList.stream().filter(
                svt -> name.equalsIgnoreCase(
                        String.format(NAME_FORMAT, svt.getName(), svt.getRarity(), svt.getClassName()))).findFirst().orElse(
                null);
    }

    public List<Servant> getServantList() {
        return servantDataList;
    }
}
