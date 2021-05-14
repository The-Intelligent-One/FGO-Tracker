package com.github.theintelligentone.fgotracker.ui.controller;

import com.github.theintelligentone.fgotracker.app.MainApp;
import com.github.theintelligentone.fgotracker.domain.servant.PlannerServant;
import com.github.theintelligentone.fgotracker.service.DataManagementService;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.List;

public class LTPlannerController {
    @FXML
    private TableView<PlannerServant> sumTable;
    private DataManagementService dataManagementService;
    private List<PlannerServant> servantList;

    public void initialize() {
        dataManagementService = MainApp.getDataManagementService();
    }

    private List<TableColumn<PlannerServant, Integer>> createColumnsForAllMats() {
        List<TableColumn<PlannerServant, Integer>> columns = new ArrayList<>();
        dataManagementService.getAllMaterials().forEach(mat -> {
            TableColumn<PlannerServant, Integer> newCol = new TableColumn<>();
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

    private List<PlannerServant> createPlannerServantList() {
        return null;
    }
}
