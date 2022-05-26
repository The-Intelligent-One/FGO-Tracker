package com.github.theintelligentone.fgotracker.service.filemanagement.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.theintelligentone.fgotracker.domain.servant.PlannerServant;
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

    public void savePlannerServants(List<PlannerServant> servants) {
        save(servants, fileService, PLANNER_SERVANT_FILE);
    }

    public void savePriorityPlannerServants(List<PlannerServant> servants) {
        save(servants, fileService, PRIORITY_SERVANT_FILE);
    }

    public void saveLongTermPlannerServants(List<PlannerServant> servants) {
        save(servants, fileService, LT_SERVANT_FILE);
    }

    private void save(List<PlannerServant> servants, FileService fileService, String file) {
        List<PlannerServant> servantsToSave = new ArrayList<>(servants);
        servantsToSave.replaceAll(userServant -> userServant.getSvtId() == 0 ? null : userServant);
        fileService.saveUserData(servantsToSave, file);
    }

    public List<PlannerServant> loadPlanner() {
        return load(PLANNER_SERVANT_FILE);
    }

    public List<PlannerServant> loadPriorityPlanner() {
        return load(PRIORITY_SERVANT_FILE);
    }

    public List<PlannerServant> loadLongTermPlanner() {
        return load(LT_SERVANT_FILE);
    }

    private List<PlannerServant> load(String file) {
        List<PlannerServant> loadedServants = fileService.loadUserDataList(file, new TypeReference<>() {});
        loadedServants.forEach(this::makePlannerValuesValid);
        loadedServants.replaceAll(userServant -> userServant == null ? new PlannerServant() : userServant);
        return loadedServants;
    }

    private void makePlannerValuesValid(PlannerServant userServant) {
        if (userServant != null) {
            userServant.setDesLevel(ServantUtils.getDefaultValueIfInvalid(userServant.getDesLevel(), 1, 120, 1));
            userServant.setDesSkill1(ServantUtils.getDefaultValueIfInvalid(userServant.getDesSkill1(), 1, 10, 1));
            userServant.setDesSkill2(ServantUtils.getDefaultValueIfInvalid(userServant.getDesSkill2(), 1, 10, 1));
            userServant.setDesSkill3(ServantUtils.getDefaultValueIfInvalid(userServant.getDesSkill3(), 1, 10, 1));
        }
    }
}
