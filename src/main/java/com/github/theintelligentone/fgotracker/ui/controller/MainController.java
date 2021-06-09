package com.github.theintelligentone.fgotracker.ui.controller;

import com.github.theintelligentone.fgotracker.app.MainApp;
import com.github.theintelligentone.fgotracker.domain.other.PlannerType;
import com.github.theintelligentone.fgotracker.service.DataManagementService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
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
    private PlannerController priorityPlannerController;
    @FXML
    private PlannerController ltPlannerController;

    private DataManagementService dataManagementService;

    public void initialize() {
        dataManagementService = MainApp.getDataManagementService();
    }


    public void tableSetup() {
        rosterTabController.setup();
        plannerController.setup();
        priorityPlannerController.setup();
        ltPlannerController.setup();
    }

    public void tearDown() {
        saveUserData();
    }

    public void saveUserData() {
        dataManagementService.saveUserState();
    }

    public void initTables() {
        plannerController.setPlannerType(PlannerType.REGULAR);
        plannerController.init();
        priorityPlannerController.setPlannerType(PlannerType.PRIORITY);
        priorityPlannerController.init();
        ltPlannerController.setPlannerType(PlannerType.LT);
        ltPlannerController.init();
    }

    public void showAboutInfo() {
        Alert aboutAlert = new Alert(Alert.AlertType.INFORMATION);
        aboutAlert.setTitle("FGO Tracker " + DataManagementService.VERSION);
        aboutAlert.setHeaderText("");
        aboutAlert.setContentText("Tracker app for Fate/Grand Order. Heavily inspired by FGO Manager by zuth. Based on Atlas Academy DB.");
        aboutAlert.showAndWait();
    }

    public void checkForUpdates() {
        try {
            GitHub gitHub = GitHub.connectAnonymously();
            GHRepository repo = gitHub.getRepository("The-Intelligent-One/FGO-Tracker");
            GHRelease latest = repo.getLatestRelease();
            GHRelease current = repo.getReleaseByTagName(DataManagementService.VERSION);
            if (current == null || latest.getPublished_at().after(current.getPublished_at())) {
                showNewUpdateAlert(latest);
            }
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
            return;
        }
    }

    private void showNewUpdateAlert(GHRelease latest) {
        Alert newUpdateAlert = new Alert(Alert.AlertType.CONFIRMATION);
        newUpdateAlert.setHeaderText("New version available: " + latest.getName());
        newUpdateAlert.setTitle("New Update Available!");
        newUpdateAlert.setContentText(
                "Would you like to download it (this will open the releases page in your browser)?");
        newUpdateAlert.showAndWait().ifPresent(buttonType -> {
            if (buttonType.getButtonData().isDefaultButton()) {
                try {
                    Desktop.getDesktop().browse(
                            new URI("https://github.com/The-Intelligent-One/FGO-Tracker/releases"));
                } catch (IOException | URISyntaxException e) {
                    log.error(e.getLocalizedMessage());
                }
            }
        });
    }
}
