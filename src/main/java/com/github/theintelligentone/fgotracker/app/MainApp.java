package com.github.theintelligentone.fgotracker.app;

import com.github.theintelligentone.fgotracker.service.DataManagementService;
import com.github.theintelligentone.fgotracker.ui.MainWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;

public class MainApp extends Application {
    private static final String MAIN_WINDOW_FXML = "/mainWindow.fxml";
    private static final double AUTO_WIDTH = 1016;
    private static final double AUTO_HEIGHT = 468;
    private FXMLLoader loader;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        loader = new FXMLLoader(getClass().getResource(MAIN_WINDOW_FXML));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("tableStyle.css");
        primaryStage.setScene(scene);
        primaryStage.setTitle("FGO Tracker");
        primaryStage.setMinHeight(AUTO_HEIGHT);
        primaryStage.setMinWidth(AUTO_WIDTH);
        primaryStage.show();
    }

    @Override
    public void stop() {
        MainWindow controller = loader.getController();
        controller.tearDown();
    }
}
