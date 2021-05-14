package com.github.theintelligentone.fgotracker.ui.controller;

import com.github.theintelligentone.fgotracker.app.MainApp;
import com.github.theintelligentone.fgotracker.domain.servant.ServantForPlanner;
import com.github.theintelligentone.fgotracker.service.DataManagementService;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.List;

public class LTPlannerController {
    @FXML
    private TableView<ServantForPlanner> sumTable;
    private DataManagementService dataManagementService;
    private List<ServantForPlanner> servantList;

    public void initialize() {
        dataManagementService = MainApp.getDataManagementService();
    }

    private List<TableColumn<ServantForPlanner, Integer>> createColumnsForAllMats() {
        List<TableColumn<ServantForPlanner, Integer>> columns = new ArrayList<>();
        dataManagementService.getAllMaterials().forEach(mat -> {
            TableColumn<ServantForPlanner, Integer> newCol = new TableColumn<>();
            ImageView imageView = new ImageView(mat.getIcon());
            imageView.fitHeightProperty().bind(newCol.widthProperty());
            imageView.fitWidthProperty().bind(newCol.widthProperty());
            newCol.setGraphic(imageView);
            newCol.setPrefWidth(MainController.SHORT_CELL_WIDTH);
            columns.add(newCol);
        });
        return columns;
    }

    public void setup() {
        sumTable.getColumns().addAll(createColumnsForAllMats());
        sumTable.getItems().add(null);
//        sumTable.getItems().addAll(createPlannerServantList());
    }

    private List<ServantForPlanner> createPlannerServantList() {
        return null;
    }
}
