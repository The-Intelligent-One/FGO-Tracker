package com.github.theintelligentone.fgotracker.app;

import com.github.theintelligentone.fgotracker.domain.servant.ServantOfUser;
import com.github.theintelligentone.fgotracker.service.DataManagementService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;

public class MainApp extends Application {
    private static final String MAIN_WINDOW_FXML = "/mainWindow.fxml";
    private DataManagementService dataManagementService;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        dataManagementService = new DataManagementService();
        URL fxmlSource = getClass().getResource(MAIN_WINDOW_FXML);
        Scene scene = new Scene(FXMLLoader.load(fxmlSource));
        TableColumn idColumn = new TableColumn("Id");
        idColumn.setCellValueFactory(new PropertyValueFactory<ServantOfUser, Integer>("id"));
        primaryStage.setScene(scene);
        primaryStage.setTitle("FGO Tracker");
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        dataManagementService.tearDown();
    }
}
