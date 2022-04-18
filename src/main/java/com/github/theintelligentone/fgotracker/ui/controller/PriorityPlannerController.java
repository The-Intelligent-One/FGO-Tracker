package com.github.theintelligentone.fgotracker.ui.controller;

import com.github.theintelligentone.fgotracker.domain.other.PlannerType;
import com.github.theintelligentone.fgotracker.service.datamanagement.DataManagementServiceFacade;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@FxmlView("/fxml/priorityPlannerTab.fxml")
public class PriorityPlannerController {

    private PlannerHandler plannerHandler;

    @Autowired
    private PlannerComponentController plannerComponentController;

    @Autowired
    private DataManagementServiceFacade dataManagementServiceFacade;

    public void setup() {
        plannerHandler.setup();
    }

    public void initialize() {
        PlannerElements elements = new PlannerElements();
        elements.setSumTable(plannerComponentController.getSumTable());
        elements.setSumLabel(plannerComponentController.getLabel());
        elements.setSumCurrent(plannerComponentController.getSumCurrent());
        elements.setSumDesired(plannerComponentController.getSumDesired());
        elements.setPlannerTable(plannerComponentController.getPlannerTable());
        elements.setNameColumn(plannerComponentController.getNameColumn());
        elements.setCurrent(plannerComponentController.getCurrent());
        elements.setDesired(plannerComponentController.getDesired());
        elements.setLevel(plannerComponentController.getLevel());
        elements.setSkill1(plannerComponentController.getSkill1());
        elements.setSkill2(plannerComponentController.getSkill2());
        elements.setSkill3(plannerComponentController.getSkill3());
        elements.setPlannerType(PlannerType.PRIORITY);
        plannerHandler = new PlannerHandler(elements, dataManagementServiceFacade);
        plannerHandler.tableInit();
    }
}
