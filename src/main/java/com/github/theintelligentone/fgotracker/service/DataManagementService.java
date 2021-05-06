package com.github.theintelligentone.fgotracker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.theintelligentone.fgotracker.domain.servant.Servant;
import com.github.theintelligentone.fgotracker.domain.servant.ServantBasicData;
import com.github.theintelligentone.fgotracker.domain.servant.ServantOfUser;
import com.github.theintelligentone.fgotracker.ui.MainWindow;

import java.util.List;
import java.util.stream.Collectors;

public class DataManagementService {
    private final DataRequestService requestService;
    private final FileManagementService fileService;
    private final MainWindow mainWindowController;

    private List<ServantBasicData> basicDataList;
    private List<Servant> servantDataList;
    private long currentVersion;

    public DataManagementService(MainWindow mainWindowController) {
        ObjectMapper objectMapper = new ObjectMapper();
        this.requestService = new DataRequestService(objectMapper);
        this.fileService = new FileManagementService(objectMapper);
        this.mainWindowController = mainWindowController;
        initApp();
    }

    public void tearDown() {
        fileService.saveUserServants(mainWindowController.getUserServants());
    }

    public void saveUserServant(ServantOfUser servant, int index) {
        while (mainWindowController.getUserServants().size() <= index) {
            mainWindowController.getUserServants().add(null);
        }
        mainWindowController.getUserServants().set(index, servant);
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
        mainWindowController.getUserServants().addAll(createUserServantList());
    }

    private List<ServantOfUser> createUserServantList() {
        List<ServantOfUser> servantsOfUser = fileService.loadUserData();
        servantsOfUser.forEach(svt -> {
            svt.setServant(basicDataList.stream()
                    .filter(basicData -> basicData.getId() == svt.getSvtId())
                    .findFirst().get());
        });
        return servantsOfUser;
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

    public List<String> getAllServantNames() {
        return basicDataList.stream().map(ServantBasicData::getName).collect(Collectors.toList());
    }
}
