package com.github.theintelligentone.fgotracker.ui.controller;

import com.github.theintelligentone.fgotracker.domain.other.PlannerType;
import com.github.theintelligentone.fgotracker.service.datamanagement.DataManagementServiceFacade;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@FxmlView("/fxml/plannerTab.fxml")
public class LTPlannerController {
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
        plannerComponentController.setupController(elements);
        elements.setPlannerType(PlannerType.LT);
        plannerHandler = new PlannerHandler(elements, dataManagementServiceFacade);
        plannerHandler.tableInit();
    }
}
