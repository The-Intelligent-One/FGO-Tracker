package com.github.theintelligentone.fgotracker.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.theintelligentone.fgotracker.domain.Servant;
import com.github.theintelligentone.fgotracker.service.DataRequestService;
import com.github.theintelligentone.fgotracker.service.FileManagementService;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;

public class MainApp extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        DataRequestService requestService = new DataRequestService(objectMapper);
        FileManagementService fileService = new FileManagementService(objectMapper);
        List<Servant> temp = requestService.getAllServantData();
        fileService.saveFullServantData(temp);
        Group group = new Group();
        Scene scene = new Scene(group);
        primaryStage.setScene(scene);
        primaryStage.setTitle("FGO Tracker");
        primaryStage.show();
    }
}
