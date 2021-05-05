package com.github.theintelligentone.fgotracker.app;

import com.github.theintelligentone.fgotracker.domain.ServantOfUser;
import com.github.theintelligentone.fgotracker.service.DataManagementService;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    private DataManagementService dataManagementService;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        dataManagementService = new DataManagementService();
        Group group = new Group();
        Scene scene = new Scene(group);
        ServantOfUser tempSvt = new ServantOfUser();
        tempSvt.setLevel(20);
        dataManagementService.saveUserServant(tempSvt, 0);
        primaryStage.setScene(scene);
        primaryStage.setTitle("FGO Tracker");
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        dataManagementService.tearDown();
    }
}
