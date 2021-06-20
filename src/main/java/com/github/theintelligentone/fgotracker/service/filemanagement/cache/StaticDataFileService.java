package com.github.theintelligentone.fgotracker.service.filemanagement.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.theintelligentone.fgotracker.domain.other.CardPlacementData;
import com.github.theintelligentone.fgotracker.domain.other.VersionDTO;
import com.github.theintelligentone.fgotracker.service.filemanagement.FileService;

import java.util.Map;

public class StaticDataFileService {
    private static final String VERSION_FILE = "dbVersion.json";
    private static final String CLASS_ATTACK_FILE = "classAttack.json";
    private static final String CARD_DATA_FILE = "cardData.json";
    private final FileService fileService;


    public StaticDataFileService(FileService fileService) {this.fileService = fileService;}

    public void saveClassAttackRateData(Map<String, Integer> classAttackRate) {
        fileService.saveDataToCache(classAttackRate, CLASS_ATTACK_FILE);
    }

    public void saveCardData(Map<String, Map<Integer, CardPlacementData>> cardDataMap) {
        fileService.saveDataToCache(cardDataMap, CARD_DATA_FILE);
    }

    public void saveCurrentVersion(Map<String, VersionDTO> versionMap) {
        fileService.saveDataToCache(versionMap, VERSION_FILE);
    }

    public Map<String, Integer> loadClassAttackRate() {
        return fileService.loadDataMapFromCache(CLASS_ATTACK_FILE, new TypeReference<>() {});
    }

    public Map<String, Map<Integer, CardPlacementData>> loadCardData() {
        return fileService.loadDataMapFromCache(CARD_DATA_FILE, new TypeReference<>() {});
    }

    public Map<String, VersionDTO> loadCurrentVersion() {
        Map<String, VersionDTO> versionDTOMap = fileService.loadDataMapFromCache(VERSION_FILE, new TypeReference<>() {});
        if (!versionDTOMap.containsKey("NA")) {
            versionDTOMap.put("NA", new VersionDTO());
        }
        if (!versionDTOMap.containsKey("JP")) {
            versionDTOMap.put("JP", new VersionDTO());
        }
        return versionDTOMap;
    }

    public void prepareOfflineStaticData() {
        fileService.copyOfflineBackupToCache(VERSION_FILE);
        fileService.copyOfflineBackupToCache(CARD_DATA_FILE);
        fileService.copyOfflineBackupToCache(CLASS_ATTACK_FILE);
    }
}
