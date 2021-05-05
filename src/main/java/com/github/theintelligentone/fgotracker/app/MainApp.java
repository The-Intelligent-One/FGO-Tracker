package com.github.theintelligentone.fgotracker.app;

import com.github.theintelligentone.fgotracker.service.DataManagementService;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        DataManagementService dataManagementService = new DataManagementService();
        Group group = new Group();
        Scene scene = new Scene(group);
        primaryStage.setScene(scene);
        primaryStage.setTitle("FGO Tracker");
        primaryStage.show();
    }
}
