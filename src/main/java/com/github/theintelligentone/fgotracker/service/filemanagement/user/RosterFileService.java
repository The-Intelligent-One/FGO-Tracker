package com.github.theintelligentone.fgotracker.service.filemanagement.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.theintelligentone.fgotracker.domain.servant.UserServant;
import com.github.theintelligentone.fgotracker.service.filemanagement.FileService;

import java.util.List;

public class RosterFileService {
    private static final String USER_SERVANT_FILE = "servants.json";

    private final FileService fileService;

    public RosterFileService(FileService fileService) {
        this.fileService = fileService;
    }

    public void saveRoster(List<UserServant> servants) {
        fileService.saveUserData(servants, USER_SERVANT_FILE);
    }

    public List<UserServant> loadRoster() {
        return fileService.loadUserDataList(USER_SERVANT_FILE, new TypeReference<>() {});
    }
}
