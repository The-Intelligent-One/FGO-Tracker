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
        loadedServants.forEach(this::makePlannerValuesValid);
        loadedServants.replaceAll(userServant -> userServant == null ? new UserServant() : userServant);
        return loadedServants;
    }

    private void makePlannerValuesValid(UserServant userServant) {
        if (userServant != null) {
            userServant.setDesLevel(ServantUtils.getDefaultValueIfInvalid(userServant.getDesLevel(), 1, 120, 1));
            userServant.setDesSkill1(ServantUtils.getDefaultValueIfInvalid(userServant.getDesSkill1(), 1, 10, 1));
            userServant.setDesSkill2(ServantUtils.getDefaultValueIfInvalid(userServant.getDesSkill2(), 1, 10, 1));
            userServant.setDesSkill3(ServantUtils.getDefaultValueIfInvalid(userServant.getDesSkill3(), 1, 10, 1));
        }
    }
}
