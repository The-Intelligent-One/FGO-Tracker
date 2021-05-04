package com.github.theintelligentone.fgotracker.app;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.theintelligentone.fgotracker.domain.ServantBasicData;
import com.github.theintelligentone.fgotracker.service.DataRequestService;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;

public class MainApp extends Application {

    private DataRequestService requestService = new DataRequestService();

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        List<ServantBasicData> temp = requestService.getAllServantData();
        Group group = new Group();
        Scene scene = new Scene(group);
        primaryStage.setScene(scene);
        primaryStage.setTitle("FGO Tracker");
        primaryStage.show();
    }
}
