package com.github.theintelligentone.fgotracker.app;

import com.github.theintelligentone.fgotracker.service.datamanagement.DataManagementServiceFacade;
import com.github.theintelligentone.fgotracker.ui.controller.MainController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class MainApp extends Application {
    private static final String MAIN_WINDOW_FXML = "/fxml/mainWindow.fxml";
    @Getter
    private static DataManagementServiceFacade dataManagementServiceFacade;
    private FXMLLoader loader;
    private MainController mainController;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void init() {
        dataManagementServiceFacade = new DataManagementServiceFacade();
        loader = new FXMLLoader(getClass().getResource(MAIN_WINDOW_FXML));
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = createMainScene();
        Alert loadingAlert = createServantLoadingAlert();
        mainController.initTables();
        setupAndShowPrimaryStage(primaryStage, scene);
        loadingAlert.show();
    }

    private Scene createMainScene() throws java.io.IOException {
        Parent root = loader.load();
        mainController = loader.getController();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("styles/tableStyle.css");
        scene.getStylesheets().add("styles/dark-mode.css");
        dataManagementServiceFacade.darkModeProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                scene.getStylesheets().add("styles/dark-mode.css");
            } else {
                scene.getStylesheets().remove("styles/dark-mode.css");
            }
        });
        return scene;
    }

    private void setupAndShowPrimaryStage(Stage primaryStage, Scene scene) {
        double screenHeight = Screen.getPrimary().getBounds().getMaxY() * 3 / 4;
        double screenWidth = Screen.getPrimary().getBounds().getMaxX() * 3 / 4;
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("FGO Tracker");
        primaryStage.setHeight(screenHeight);
        primaryStage.setWidth(screenWidth);
        primaryStage.setOnCloseRequest(event -> mainController.tearDown());
        primaryStage.show();
    }

    private Alert createServantLoadingAlert() {
        String selectedRegion = dataManagementServiceFacade.getGameRegion();
        if (selectedRegion.isEmpty()) {
            selectedRegion = showRegionChooser();
        }
        Alert loadingAlert = setupLoadingAlert();
        if (selectedRegion == null) {
            Platform.exit();
        } else {
            new Thread(createLoadingTaskWithAlert(selectedRegion, loadingAlert)).start();
        }
        return loadingAlert;
    }

    private Alert setupLoadingAlert() {
        Alert loadingAlert = new Alert(Alert.AlertType.NONE);
        loadingAlert.setTitle("FGO Tracker");
        loadingAlert.initStyle(StageStyle.UNIFIED);
        loadingAlert.setContentText("Servant data loading");
        return loadingAlert;
    }

    private String showRegionChooser() {
        ChoiceDialog<String> regionDialog = new ChoiceDialog<>();
        regionDialog.setTitle("FGO Region");
        regionDialog.setContentText("Which region are you playing on?");
        regionDialog.setHeaderText(null);
        regionDialog.setGraphic(null);
        regionDialog.getItems().addAll("JP", "NA");
        regionDialog.setSelectedItem("NA");
        Optional<String> dialogResult = regionDialog.showAndWait();
        return dialogResult.orElse(null);
    }

    private Task<Object> createLoadingTaskWithAlert(String selectedRegion, Alert loadingAlert) {
        Task<Object> loadingTask = new Task<>() {
            @Override
            protected Object call() {
                dataManagementServiceFacade.initApp(selectedRegion);
                this.succeeded();
                return null;
            }
        };
        loadingTask.setOnSucceeded(event -> {
            mainController.setup();
            loadingAlert.setResult(ButtonType.CANCEL);
            loadingAlert.close();
        });
        loadingTask.setOnFailed(event -> {
            log.error(loadingTask.getException().getLocalizedMessage());
            Platform.exit();
        });
        return loadingTask;
    }
}
