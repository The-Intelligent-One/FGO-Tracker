package com.github.theintelligentone.fgotracker.service.filemanagement.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.theintelligentone.fgotracker.domain.servant.PlannerServant;
import com.github.theintelligentone.fgotracker.domain.view.JsonViews;
import com.github.theintelligentone.fgotracker.service.filemanagement.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PlannerFileService {
    private static final String PLANNER_SERVANT_FILE = "planned.json";
    private static final String PRIORITY_SERVANT_FILE = "priority.json";

    @Autowired
    private FileService fileService;

    public void savePlannerServants(List<PlannerServant> servants) {
        fileService.saveUserData(servants, PLANNER_SERVANT_FILE, JsonViews.Planner.class);
    }

    public void savePriorityPlannerServants(List<PlannerServant> servants) {
        fileService.saveUserData(servants, PRIORITY_SERVANT_FILE, JsonViews.Planner.class);
    }

    public List<PlannerServant> loadPlanner() {
        return fileService.loadUserDataList(PLANNER_SERVANT_FILE, new TypeReference<>() {});
    }

    public List<PlannerServant> loadPriorityPlanner() {
        return fileService.loadUserDataList(PRIORITY_SERVANT_FILE, new TypeReference<>() {});
    }
}
