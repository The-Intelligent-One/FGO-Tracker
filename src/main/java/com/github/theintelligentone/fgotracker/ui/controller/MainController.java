package com.github.theintelligentone.fgotracker.ui.controller;

import com.github.theintelligentone.fgotracker.app.MainApp;
import com.github.theintelligentone.fgotracker.service.DataManagementService;
import javafx.fxml.FXML;

public class MainController {
    public static final double NAME_CELL_WIDTH = 200;
    public static final double LONG_CELL_WIDTH = 100;
    public static final double MID_CELL_WIDTH = 60;
    public static final double SHORT_CELL_WIDTH = 40;
    public static final double CHAR_CELL_WIDTH = 20;
    public static final int CELL_HEIGHT = 30;

    @FXML
    private RosterController rosterTabController;

    @FXML
    private PlannerController plannerController;

    @FXML
    private PlannerController ltPlannerController;

    private DataManagementService dataManagementService;

    public void initialize() {
        dataManagementService = MainApp.getDataManagementService();
    }


    public void tableSetup() {
        rosterTabController.setup();
        plannerController.setup();
        ltPlannerController.setup();
    }

    public void tearDown() {
        dataManagementService.saveUserState();
    }

    public void saveUserData() {
        dataManagementService.saveUserState();
    }

    public void initTables() {
        plannerController.setLongTerm(false);
        plannerController.init();
        ltPlannerController.setLongTerm(true);
        ltPlannerController.init();
    }
}
