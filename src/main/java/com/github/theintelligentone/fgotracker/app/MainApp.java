package com.github.theintelligentone.fgotracker.app;

import com.github.theintelligentone.fgotracker.service.DataManagementService;
import com.github.theintelligentone.fgotracker.ui.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;

public class MainApp extends Application {
    private static final String MAIN_WINDOW_FXML = "/mainWindow.fxml";
    private static final double AUTO_WIDTH = 1030;
    private static final double AUTO_HEIGHT = 480;
    @Getter
    private static DataManagementService dataManagementService;
    private FXMLLoader loader;
    private MainController mainController;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        dataManagementService = new DataManagementService();
        loader = new FXMLLoader(getClass().getResource(MAIN_WINDOW_FXML));
        Parent root = loader.load();
        mainController = loader.getController();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("tableStyle.css");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("FGO Tracker");
        primaryStage.setMinHeight(AUTO_HEIGHT);
        primaryStage.setMinWidth(AUTO_WIDTH);
        primaryStage.show();
    }

    @Override
    public void stop() {
        mainController.tearDown();
    }
}
