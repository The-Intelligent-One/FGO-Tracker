package com.github.theintelligentone.fgotracker.service.filemanagement.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.theintelligentone.fgotracker.service.filemanagement.FileService;

public class OptionsFileService {
    private static final String GAME_REGION_FILE = "region.json";
    private static final String DARKMODE_FILE = "darkmode.json";

    private final FileService fileService;

    public OptionsFileService(FileService fileService) {this.fileService = fileService;}

    public void saveDarkMode(boolean darkMode) {
        fileService.saveUserData(darkMode, DARKMODE_FILE);
    }

    public void saveGameRegion(String gameRegion) {
        fileService.saveUserData(gameRegion, GAME_REGION_FILE);
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
