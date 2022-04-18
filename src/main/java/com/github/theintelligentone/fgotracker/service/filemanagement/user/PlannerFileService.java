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
public class PlannerFileService {
    private static final String PLANNER_SERVANT_FILE = "planned.json";
    private static final String PRIORITY_SERVANT_FILE = "priority.json";
    private static final String LT_SERVANT_FILE = "longterm.json";

    @Autowired
    private FileService fileService;

    public void savePlannerServants(List<UserServant> servants) {
        save(servants, fileService, PLANNER_SERVANT_FILE);
    }

    public void savePriorityPlannerServants(List<UserServant> servants) {
        save(servants, fileService, PRIORITY_SERVANT_FILE);
    }

    public void saveLongTermPlannerServants(List<UserServant> servants) {
        save(servants, fileService, LT_SERVANT_FILE);
    }

    private void save(List<UserServant> servants, FileService fileService, String file) {
        List<UserServant> servantsToSave = new ArrayList<>(servants);
        servantsToSave.replaceAll(userServant -> userServant.getSvtId() == 0 ? null : userServant);
        fileService.saveUserData(servantsToSave, file);
    }

    public List<UserServant> loadPlanner() {
        return load(PLANNER_SERVANT_FILE);
    }

    public List<UserServant> loadPriorityPlanner() {
        return load(PRIORITY_SERVANT_FILE);
    }

    public List<UserServant> loadLongTermPlanner() {
        return load(LT_SERVANT_FILE);
    }

    private List<UserServant> load(String file) {
        List<UserServant> loadedServants = fileService.loadUserDataList(file, new TypeReference<>() {});
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
            userServant.setFouAtk(ServantUtils.getDefaultValueIfInvalid(userServant.getFouAtk(), 0, 2000, 0));
            userServant.setFouHp(ServantUtils.getDefaultValueIfInvalid(userServant.getFouHp(), 0, 2000, 0));
            userServant.setNpLevel(ServantUtils.getDefaultValueIfInvalid(userServant.getNpLevel(), 1, 5, 1));
            userServant.setBondLevel(ServantUtils.getDefaultValueIfInvalid(userServant.getBondLevel(), 0, 15, 0));
        }
    }
}
