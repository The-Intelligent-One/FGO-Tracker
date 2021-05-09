package com.github.theintelligentone.fgotracker.ui;

import com.github.theintelligentone.fgotracker.app.MainApp;
import com.github.theintelligentone.fgotracker.service.DataManagementService;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.util.List;

public class MainController {
    private DataManagementService dataManagementService;

    public void initialize() {
        dataManagementService = MainApp.getDataManagementService();
    }

    public void addNewRow() {
        dataManagementService.saveUserServant(null);
    }

    public void importFromCsv() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("CSV to import");
        List<String> notFoundNames = dataManagementService.importFromCsv(fileChooser.showOpenDialog(Stage.getWindows().get(0)));
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

    public void tearDown() {
        dataManagementService.saveUserState();
    }
}
