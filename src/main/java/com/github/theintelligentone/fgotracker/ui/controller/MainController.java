package com.github.theintelligentone.fgotracker.ui.controller;

import com.github.theintelligentone.fgotracker.app.MainApp;
import com.github.theintelligentone.fgotracker.service.DataManagementService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;
import java.util.stream.IntStream;

public class MainController {
    public static final double NAME_CELL_WIDTH = 200;
    public static final double LONG_CELL_WIDTH = 100;
    public static final double MID_CELL_WIDTH = 60;
    public static final double SHORT_CELL_WIDTH = 40;
    public static final double CHAR_CELL_WIDTH = 20;

    @FXML
    private RosterController rosterTabController;

    private DataManagementService dataManagementService;

    public void initialize() {
        dataManagementService = MainApp.getDataManagementService();
    }

    public void addNewRow() {
        dataManagementService.saveUserServant(null);
    }

    public void rosterTableSetup() {
        rosterTabController.loadData();
    }

    public void importFromCsv() {
        if (dataManagementService.isDataLoaded()) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("CSV to import");
            File csvFile = fileChooser.showOpenDialog(Stage.getWindows().get(0));
            if (csvFile != null) {
                List<String> notFoundNames = dataManagementService.importFromCsv(csvFile);
                if (notFoundNames != null && !notFoundNames.isEmpty()) {
                    StringBuilder stringBuilder = new StringBuilder();
                    Alert notFoundAlert = new Alert(Alert.AlertType.WARNING);
                    notFoundNames.forEach(str -> {
                        stringBuilder.append(str);
                        stringBuilder.append("\n");
                    });
                    notFoundAlert.setContentText(stringBuilder.toString());
                    notFoundAlert.show();
                }
            }
        } else {
            Alert loadingAlert = new Alert(Alert.AlertType.WARNING);
            loadingAlert.setContentText("Servant data still loading.");
            loadingAlert.show();
        }
    }

    public void tearDown() {
        dataManagementService.saveUserState();
    }

    public void add10NewRow() {
        IntStream.range(0, 10).forEach(number -> this.addNewRow());
    }

    public void saveUserData() {
        dataManagementService.saveUserState();
    }
}