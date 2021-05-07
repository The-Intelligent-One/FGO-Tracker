package com.github.theintelligentone.fgotracker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.theintelligentone.fgotracker.domain.servant.Servant;
import com.github.theintelligentone.fgotracker.domain.servant.ServantOfUser;
import com.github.theintelligentone.fgotracker.ui.MainWindow;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DataManagementService {
    private final DataRequestService requestService;
    private final FileManagementService fileService;
    private final MainWindow mainWindowController;

    private List<String> servantNameList;
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
            if (!servantDataList.isEmpty()) {
                fileService.saveFullServantData(servantDataList);
                fileService.saveNewVersion(currentVersion);
            }
        } else {
            servantDataList = fileService.loadFullServantData();
        }
        servantNameList = servantDataList.stream().map(Servant::getName).collect(Collectors.toList());
        mainWindowController.getUserServants().addAll(fileService.loadUserData());
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
        return servantNameList;
    }
}
