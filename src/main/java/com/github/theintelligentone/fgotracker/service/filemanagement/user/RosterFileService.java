package com.github.theintelligentone.fgotracker.service.filemanagement.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.theintelligentone.fgotracker.domain.servant.UserServant;
import com.github.theintelligentone.fgotracker.service.ServantUtils;
import com.github.theintelligentone.fgotracker.service.filemanagement.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RosterFileService {
    private static final String USER_SERVANT_FILE = "servants.json";

    @Autowired
    private FileService fileService;

    public void saveRoster(List<UserServant> servants) {
        List<UserServant> servantsToSave = new ArrayList<>(servants);
        servantsToSave.replaceAll(userServant -> userServant.getSvtId() == 0 ? null : userServant);
        fileService.saveUserData(servantsToSave, USER_SERVANT_FILE);
    }

    public List<UserServant> loadRoster() {
        List<UserServant> loadedServants = fileService.loadUserDataList(USER_SERVANT_FILE, new TypeReference<>() {});
        loadedServants.forEach(this::makeRosterValuesValid);
        loadedServants.replaceAll(userServant -> userServant == null ? new UserServant() : userServant);
        return loadedServants;
    }

    private void makeRosterValuesValid(UserServant userServant) {
        if (userServant != null) {
            userServant.setLevel(ServantUtils.getDefaultValueIfInvalid(userServant.getLevel(), 1, 120, 1));
            userServant.setSkillLevel1(ServantUtils.getDefaultValueIfInvalid(userServant.getSkillLevel1(), 1, 10, 1));
            userServant.setSkillLevel2(ServantUtils.getDefaultValueIfInvalid(userServant.getSkillLevel2(), 1, 10, 1));
            userServant.setSkillLevel3(ServantUtils.getDefaultValueIfInvalid(userServant.getSkillLevel3(), 1, 10, 1));
            userServant.setAppendSkillLevel1(ServantUtils.getDefaultValueIfInvalid(userServant.getAppendSkillLevel1(), 1, 10, 1));
            userServant.setAppendSkillLevel2(ServantUtils.getDefaultValueIfInvalid(userServant.getAppendSkillLevel2(), 1, 10, 1));
            userServant.setAppendSkillLevel3(ServantUtils.getDefaultValueIfInvalid(userServant.getAppendSkillLevel3(), 1, 10, 1));
        }
    }
}
