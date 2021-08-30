package com.github.theintelligentone.fgotracker.service.filemanagement.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.theintelligentone.fgotracker.domain.servant.BasicServant;
import com.github.theintelligentone.fgotracker.domain.servant.Servant;
import com.github.theintelligentone.fgotracker.service.filemanagement.FileService;

import java.util.List;

public class ServantFileService {
    private static final String SERVANT_DATA_FILE = "servants.json";
    private static final String BASIC_SERVANT_DATA_FILE = "basic_servants.json";
    private final FileService fileService;

    public ServantFileService(FileService fileService) {this.fileService = fileService;}

    public void prepareOfflineServantData() {
        fileService.copyOfflineBackupToCache("NA_" + SERVANT_DATA_FILE);
        fileService.copyOfflineBackupToCache("JP_" + SERVANT_DATA_FILE);
    }

    public void saveFullServantData(List<Servant> servants, String gameRegion) {
        fileService.saveDataToCache(servants, gameRegion + "_" + SERVANT_DATA_FILE);
    }

    public void saveBasicServantData(List<BasicServant> basicServantDataList, String gameRegion) {
        fileService.saveDataToCache(basicServantDataList, gameRegion + "_" + BASIC_SERVANT_DATA_FILE);
    }

    public List<Servant> loadFullServantData(String gameRegion) {
        return fileService.loadDataListFromCache(gameRegion + "_" + SERVANT_DATA_FILE, new TypeReference<>() {});
    }

    public List<BasicServant> loadBasicServantData(String gameRegion) {
        return fileService.loadDataListFromCache(gameRegion + "_" + BASIC_SERVANT_DATA_FILE, new TypeReference<>() {});
    }
}
