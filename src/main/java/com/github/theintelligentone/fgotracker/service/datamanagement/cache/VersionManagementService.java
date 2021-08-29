package com.github.theintelligentone.fgotracker.service.datamanagement.cache;

import com.github.theintelligentone.fgotracker.domain.other.VersionDTO;
import com.github.theintelligentone.fgotracker.service.datamanagement.DataRequestService;
import com.github.theintelligentone.fgotracker.service.filemanagement.FileManagementServiceFacade;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

public class VersionManagementService {
    private final FileManagementServiceFacade fileServiceFacade;
    private final DataRequestService requestService;

    @Getter
    private String gameRegion;
    private Map<String, VersionDTO> currentVersion;

    public VersionManagementService(FileManagementServiceFacade fileServiceFacade, DataRequestService requestService) {
        this.fileServiceFacade = fileServiceFacade;
        this.requestService = requestService;
        gameRegion = fileServiceFacade.loadGameRegion();
    }

    public void loadGameRegion(String selectedRegion){
        if (gameRegion.isEmpty()) {
            gameRegion = selectedRegion;
            fileServiceFacade.saveGameRegion(gameRegion);
        }
    }

    public boolean newVersionAvailable() {
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

    public void saveVersion() {
        fileServiceFacade.saveNewVersion(currentVersion);
    }
}
