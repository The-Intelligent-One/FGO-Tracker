package com.github.theintelligentone.fgotracker.app;

import com.github.theintelligentone.fgotracker.service.DataManagementService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    private static final String MAIN_WINDOW_FXML = "/mainWindow.fxml";
    private DataManagementService dataManagementService;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(MAIN_WINDOW_FXML));
        Parent root = loader.load();
        dataManagementService = new DataManagementService(loader.getController());
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("FGO Tracker");
        primaryStage.show();
    }

    @Override
    public void stop() {
        dataManagementService.tearDown();
    }
}
