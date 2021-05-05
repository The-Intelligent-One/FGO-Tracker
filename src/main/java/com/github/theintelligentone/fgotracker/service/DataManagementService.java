package com.github.theintelligentone.fgotracker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.theintelligentone.fgotracker.domain.Servant;
import com.github.theintelligentone.fgotracker.domain.ServantBasicData;

import java.util.List;

public class DataManagementService {
    private final DataRequestService requestService;
    private final FileManagementService fileService;

    private List<ServantBasicData> basicDataList;
    private List<ServantBasicData> userServantList;
    private List<Servant> servantDataList;

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
        } else {
            servantDataList = fileService.loadFullServantData();
            basicDataList = fileService.loadBasicServantData();
        }
        userServantList = fileService.loadUserData();
    }

    private boolean newVersionAvailable() {
        return requestService.getOnlineVersion() > fileService.getCurrentVersion();
    }
}
