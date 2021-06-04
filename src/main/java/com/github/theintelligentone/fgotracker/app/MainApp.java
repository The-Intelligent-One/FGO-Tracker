package com.github.theintelligentone.fgotracker.app;

import com.github.theintelligentone.fgotracker.service.DataManagementService;
import com.github.theintelligentone.fgotracker.ui.controller.MainController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Getter;

public class MainApp extends Application {
    private static final String MAIN_WINDOW_FXML = "/fxml/mainWindow.fxml";
    @Getter
    private static DataManagementService dataManagementService;
    private FXMLLoader loader;
    private MainController mainController;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void init() {
        dataManagementService = new DataManagementService();
        loader = new FXMLLoader(getClass().getResource(MAIN_WINDOW_FXML));
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        double screenHeight = Screen.getPrimary().getBounds().getMaxY() * 3 / 4;
        double screenWidth = Screen.getPrimary().getBounds().getMaxX() * 3 / 4;
        Parent root = loader.load();
        mainController = loader.getController();
        Alert loadingAlert = initServantData();
        mainController.initTables();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("tableStyle.css");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("FGO Tracker");
        primaryStage.setHeight(screenHeight);
        primaryStage.setWidth(screenWidth);
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> mainController.tearDown());
        loadingAlert.show();
    }

    private Alert initServantData() {
        Alert loadingAlert = new Alert(Alert.AlertType.NONE);
        loadingAlert.setTitle("FGO Tracker");
        loadingAlert.initStyle(StageStyle.UNIFIED);
        loadingAlert.setContentText("Servant data loading");
        Task loadingTask = new Task() {
            @Override
            protected Object call() {
                dataManagementService.initApp();
                this.succeeded();
                return null;
            }
        };
        loadingTask.setOnSucceeded(event -> {
            mainController.tableSetup();
            loadingAlert.setResult(ButtonType.CANCEL);
            loadingAlert.close();
        });
        loadingTask.setOnFailed(event -> {
            loadingTask.getException().printStackTrace();
            Platform.exit();
        });
        new Thread(loadingTask).start();
        return loadingAlert;
    }
}
