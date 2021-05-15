package com.github.theintelligentone.fgotracker.ui.controller;

import com.github.theintelligentone.fgotracker.app.MainApp;
import com.github.theintelligentone.fgotracker.domain.servant.PlannerServant;
import com.github.theintelligentone.fgotracker.domain.servant.factory.PlannerServantFactory;
import com.github.theintelligentone.fgotracker.service.DataManagementService;
import com.github.theintelligentone.fgotracker.ui.valuefactory.planner.PlannerServantGrailValueFactory;
import com.github.theintelligentone.fgotracker.ui.valuefactory.planner.PlannerServantMaterialValueFactory;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class LTPlannerController {
    private static final int HOLY_GRAIL_ID = 7999;

    @FXML
    private Tab ltPlannerTab;

    @FXML
    private TableView<PlannerServant> sumTable;

    @FXML
    private TableColumn<PlannerServant, String> sumCurrent;

    @FXML
    private TableColumn<PlannerServant, String> sumDesired;

    @FXML
    private TableView<PlannerServant> plannerTable;

    @FXML
    private TableColumn<PlannerServant, ?> current;

    @FXML
    private TableColumn<PlannerServant, ?> desired;

    @FXML
    private TableColumn<PlannerServant, Number> level;

    @FXML
    private TableColumn<PlannerServant, Number> skill1;

    @FXML
    private TableColumn<PlannerServant, Number> skill2;

    @FXML
    private TableColumn<PlannerServant, Number> skill3;

    private DataManagementService dataManagementService;

    public void initialize() {
        dataManagementService = MainApp.getDataManagementService();
        tableSetup();
    }

    private List<TableColumn<PlannerServant, Number>> createColumnsForAllMats() {
        List<TableColumn<PlannerServant, Number>> columns = new ArrayList<>();
        dataManagementService.getAllMaterials().forEach(mat -> {
            TableColumn<PlannerServant, Number> newCol = new TableColumn<>();
            ImageView imageView = new ImageView(mat.getIconImage());
            imageView.setPreserveRatio(true);
            imageView.fitWidthProperty().bind(newCol.widthProperty());
            if (!dataManagementService.isIconsResized()) {
                SnapshotParameters parameters = new SnapshotParameters();
                parameters.setFill(Color.TRANSPARENT);
                mat.setIconImage(imageView.snapshot(parameters, null));
            }
            newCol.setGraphic(imageView);
            newCol.setPrefWidth(MainController.SHORT_CELL_WIDTH);
            newCol.setCellValueFactory(new PlannerServantMaterialValueFactory(mat.getId()));
            if (mat.getId() == HOLY_GRAIL_ID) {
                newCol.setCellValueFactory(new PlannerServantGrailValueFactory());
            }
            columns.add(newCol);
        });
        return columns;
    }

    public void setup() {
        loadTableData();
        ltPlannerTab.setOnSelectionChanged(event -> {
            if (ltPlannerTab.isSelected() && dataManagementService.isDataLoaded()) {
                this.loadTableData();
            }
        });
        plannerTable.getColumns().addAll(createColumnsForAllMats());
        sumTable.getColumns().addAll(createColumnsForAllMats());
        if (!dataManagementService.isIconsResized()) {
            dataManagementService.saveMaterialData();
        }
        ((Pane) sumTable.getChildrenUnmodifiable().get(0)).setMaxHeight(0);
        ((Pane) sumTable.getChildrenUnmodifiable().get(0)).setMinHeight(0);
        ((Pane) sumTable.getChildrenUnmodifiable().get(0)).setPrefHeight(0);
    }

    private void tableSetup() {
        setupPlannerTable();
        setupSumTable();
    }

    private void setupPlannerTable() {
        plannerTable.getColumns().get(0).setPrefWidth(MainController.NAME_CELL_WIDTH);
        setupCurrentInfoColumns();
        setupInfoColumn(desired);
    }

    private void loadTableData() {
        plannerTable.setItems(FXCollections.observableArrayList(createPlannerServantList()));
    }

    private void setupCurrentInfoColumns() {
        level.setCellValueFactory(param -> {
            SimpleIntegerProperty level = null;
            if (param.getValue().getBaseServant() != null && param.getValue().getBaseServant().getBaseServant() != null) {
                level = new SimpleIntegerProperty(param.getValue().getBaseServant().getLevel());
            }
            return level;
        });
        skill1.setCellValueFactory(param -> {
            SimpleIntegerProperty skill1 = null;
            if (param.getValue().getBaseServant() != null && param.getValue().getBaseServant().getBaseServant() != null) {
                skill1 = new SimpleIntegerProperty(param.getValue().getBaseServant().getSkillLevel1());
            }
            return skill1;
        });
        skill2.setCellValueFactory(param -> {
            SimpleIntegerProperty skill2 = null;
            if (param.getValue().getBaseServant() != null && param.getValue().getBaseServant().getBaseServant() != null) {
                skill2 = new SimpleIntegerProperty(param.getValue().getBaseServant().getSkillLevel1());
            }
            return skill2;
        });
        skill3.setCellValueFactory(param -> {
            SimpleIntegerProperty skill3 = null;
            if (param.getValue().getBaseServant() != null && param.getValue().getBaseServant().getBaseServant() != null) {
                skill3 = new SimpleIntegerProperty(param.getValue().getBaseServant().getSkillLevel1());
            }
            return skill3;
        });
        setupInfoColumn(current);
    }

    private void setupSumTable() {
        sumCurrent.prefWidthProperty().bind(current.widthProperty());
        sumDesired.prefWidthProperty().bind(desired.widthProperty());
    }

    private void setupInfoColumn(TableColumn<PlannerServant, ?> column) {
        column.getColumns().get(0).setPrefWidth(MainController.SHORT_CELL_WIDTH);
        column.getColumns().stream().skip(1).forEach(col -> col.setPrefWidth(MainController.CHAR_CELL_WIDTH));
    }

    private List<PlannerServant> createPlannerServantList() {
        return new PlannerServantFactory().createForLTPlanner(dataManagementService.getUserServantList());
    }
}
