package com.github.theintelligentone.fgotracker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.theintelligentone.fgotracker.domain.Servant;
import com.github.theintelligentone.fgotracker.domain.ServantBasicData;
import com.github.theintelligentone.fgotracker.domain.ServantOfUser;

import java.util.List;

public class DataManagementService {
    private final DataRequestService requestService;
    private final FileManagementService fileService;

    private List<ServantBasicData> basicDataList;
    private List<ServantOfUser> userServantList;
    private List<Servant> servantDataList;
    private long currentVersion;

    public DataManagementService() {
        ObjectMapper objectMapper = new ObjectMapper();
        this.requestService = new DataRequestService(objectMapper);
        this.fileService = new FileManagementService(objectMapper);
        initApp();
    }

    private void initApp() {
        if (newVersionAvailable()) {
            servantDataList = requestService.getAllServantData();
            fileService.saveFullServantData(servantDataList);
            basicDataList = requestService.getAllBasicServantData();
            fileService.saveBasicServantData(basicDataList);
            fileService.saveNewVersion(currentVersion);
        } else {
            servantDataList = fileService.loadFullServantData();
            basicDataList = fileService.loadBasicServantData();
        }
        userServantList = fileService.loadUserData();
    }

    private boolean newVersionAvailable() {
        currentVersion = fileService.getCurrentVersion();
        long onlineVersion = requestService.getOnlineVersion();
        boolean needUpdate = false;
        if (onlineVersion > currentVersion) {
            needUpdate = true;
            currentVersion = onlineVersion;
        }
        return needUpdate;
    }
}
