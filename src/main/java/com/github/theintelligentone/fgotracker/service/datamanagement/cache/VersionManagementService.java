package com.github.theintelligentone.fgotracker.service.datamanagement.cache;

import com.github.theintelligentone.fgotracker.domain.other.VersionDTO;
import com.github.theintelligentone.fgotracker.service.datamanagement.DataRequestService;
import com.github.theintelligentone.fgotracker.service.filemanagement.FileManagementServiceFacade;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class VersionManagementService {
    private final FileManagementServiceFacade fileServiceFacade;
    private final DataRequestService requestService;

    @Getter
    private String gameRegion;
    private Map<String, VersionDTO> currentVersion;

    @Autowired
    public VersionManagementService(FileManagementServiceFacade fileServiceFacade, DataRequestService requestService) {
        this.fileServiceFacade = fileServiceFacade;
        this.requestService = requestService;
        gameRegion = fileServiceFacade.loadGameRegion();
    }

    public void loadGameRegion(String selectedRegion) {
        if (gameRegion.isEmpty()) {
            gameRegion = selectedRegion;
            fileServiceFacade.saveGameRegion(gameRegion);
        }
    }

    public boolean newVersionAvailable() {
        currentVersion = fileServiceFacade.loadCurrentVersion();
        Map<String, VersionDTO> onlineVersion = requestService.getOnlineVersion();
        boolean needUpdate = false;
        if (!onlineVersion.isEmpty() && checkForNewerVersions(onlineVersion)) {
            needUpdate = true;
            currentVersion = onlineVersion;
        } else if (currentVersion.get(gameRegion).getTimestamp() == 0) {
            currentVersion = fileServiceFacade.loadCurrentVersion();
        }
        return needUpdate;
    }

    private boolean checkForNewerVersions(Map<String, VersionDTO> onlineVersion) {
        return onlineVersion.entrySet()
                .stream()
                .anyMatch(version -> version.getValue().getTimestamp() > currentVersion.get(version.getKey())
                        .getTimestamp());
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
