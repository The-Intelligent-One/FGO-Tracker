package com.github.theintelligentone.fgotracker.service.filemanagement.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.theintelligentone.fgotracker.domain.servant.UserServant;
import com.github.theintelligentone.fgotracker.domain.view.JsonViews;
import com.github.theintelligentone.fgotracker.service.filemanagement.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RosterFileService {
    private static final String USER_SERVANT_FILE = "servants.json";

    @Autowired
    private FileService fileService;

    public void saveRoster(List<UserServant> servants) {
        fileService.saveUserData(servants, USER_SERVANT_FILE, JsonViews.Roster.class);
    }

    public List<UserServant> loadRoster() {
        return fileService.loadUserDataList(USER_SERVANT_FILE, new TypeReference<>() {});
    }
}
