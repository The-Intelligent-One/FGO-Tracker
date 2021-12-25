package com.github.theintelligentone.fgotracker.service.filemanagement.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.theintelligentone.fgotracker.domain.view.JsonViews;
import com.github.theintelligentone.fgotracker.service.filemanagement.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OptionsFileService {
    private static final String GAME_REGION_FILE = "region.json";
    private static final String DARKMODE_FILE = "darkmode.json";

    @Autowired
    private FileService fileService;

    public void saveDarkMode(boolean darkMode) {
        fileService.saveUserData(darkMode, DARKMODE_FILE, null);
    }

    public void saveGameRegion(String gameRegion) {
        fileService.saveUserData(gameRegion, GAME_REGION_FILE, null);
    }

    public boolean loadDarkMode() {
        Boolean darkMode = fileService.loadUserData(DARKMODE_FILE, new TypeReference<>() {});
        return darkMode == null || darkMode;
    }

    public String loadGameRegion() {
        String region = fileService.loadUserData(GAME_REGION_FILE, new TypeReference<>() {});
        return region == null ? "" : region;
    }
}
