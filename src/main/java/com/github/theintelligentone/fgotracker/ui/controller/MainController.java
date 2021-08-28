package com.github.theintelligentone.fgotracker.ui.controller;

import com.github.theintelligentone.fgotracker.app.MainApp;
import com.github.theintelligentone.fgotracker.domain.other.PlannerType;
import com.github.theintelligentone.fgotracker.service.datamanagement.DataManagementServiceFacade;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

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

    private DataManagementServiceFacade dataManagementServiceFacade;

    public void initialize() {
        dataManagementServiceFacade = MainApp.getDataManagementServiceFacade();
        checkForUpdates(null);
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
        dataManagementServiceFacade.saveUserState();
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
        aboutAlert.setTitle("FGO Tracker " + DataManagementServiceFacade.VERSION);
        aboutAlert.setHeaderText("");
        aboutAlert.setContentText(
                "Tracker app for Fate/Grand Order. Heavily inspired by FGO Manager by zuth. Based on Atlas Academy DB.");
        aboutAlert.showAndWait();
    }

    public void checkForUpdates(ActionEvent actionEvent) {
        try {
            GitHub gitHub = GitHub.connectAnonymously();
            GHRepository repo = gitHub.getRepository("The-Intelligent-One/FGO-Tracker");
            GHRelease latest;
            if (actionEvent == null) {
                latest = repo.getLatestRelease();
            } else {
                latest = repo.listReleases().toList().get(0);
            }
            GHRelease current = repo.getReleaseByTagName(DataManagementServiceFacade.VERSION);
            if (current != null && latest.getPublished_at().after(current.getPublished_at())) {
                showNewUpdateAlert(latest);
            } else if (actionEvent != null) {
                showNoNewUpdateAlert();
            }
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    private void showNoNewUpdateAlert() {
        Alert noUpdateAlert = new Alert(Alert.AlertType.INFORMATION);
        noUpdateAlert.setTitle("Update Checker");
        noUpdateAlert.setHeaderText("No new updates available");
        noUpdateAlert.show();
    }

    private void showNewUpdateAlert(GHRelease latest) {
        Alert newUpdateAlert = new Alert(Alert.AlertType.CONFIRMATION);
        newUpdateAlert.setTitle("New Update Available!");
        newUpdateAlert.setHeaderText("New version available: " + latest.getName());
        newUpdateAlert.setContentText(
                "Would you like to download it (this will open the releases page in your browser and close the app)?");
        newUpdateAlert.setResizable(true);
        newUpdateAlert.showAndWait().ifPresent(buttonType -> {
            if (buttonType.getButtonData().isDefaultButton()) {
                try {
                    Desktop.getDesktop().browse(
                            new URI("https://github.com/The-Intelligent-One/FGO-Tracker/releases"));
                    Platform.exit();
                } catch (IOException | URISyntaxException e) {
                    log.error(e.getLocalizedMessage(), e);
                }
            }
        });
    }

    public void showUserGuide() {
        WebView helpView = new WebView();
        helpView.getEngine().load(Objects.requireNonNull(getClass().getResource("/userguide.html")).toString());
        VBox.setVgrow(helpView, Priority.ALWAYS);
        VBox vBox = new VBox(helpView);
        vBox.setFillWidth(true);
        Scene scene = new Scene(vBox);
        if (dataManagementServiceFacade.darkModeProperty().getValue()) {
            helpView.getEngine().setUserStyleSheetLocation(
                    Objects.requireNonNull(getClass().getResource("/styles/userguide-dark.css")).toString());
            scene.getStylesheets().add("styles/dark-mode.css");
        } else {
            helpView.getEngine().setUserStyleSheetLocation(
                    Objects.requireNonNull(getClass().getResource("/styles/userguide.css")).toString());
        }
        Stage helpStage = new Stage();
        vBox.minHeightProperty().bind(helpStage.heightProperty());
        helpStage.setScene(scene);
        helpStage.setTitle("FGO Tracker User Guide");
        helpStage.show();
    }

    public void invalidateCache() {
        dataManagementServiceFacade.invalidateCache();
    }

    public void toggleDarkMode() {
        dataManagementServiceFacade.darkModeProperty().set(!dataManagementServiceFacade.darkModeProperty().get());
    }
}
