package com.github.theintelligentone.fgotracker.ui.controller;

import com.github.theintelligentone.fgotracker.app.MainApp;
import com.github.theintelligentone.fgotracker.domain.item.UpgradeMaterial;
import com.github.theintelligentone.fgotracker.domain.servant.PlannerServant;
import com.github.theintelligentone.fgotracker.domain.servant.factory.PlannerServantFactory;
import com.github.theintelligentone.fgotracker.service.DataManagementService;
import com.github.theintelligentone.fgotracker.ui.valuefactory.planner.PlannerServantGrailValueFactory;
import com.github.theintelligentone.fgotracker.ui.valuefactory.planner.PlannerServantMaterialValueFactory;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class PlannerController {
    private static final int HOLY_GRAIL_ID = 7999;

    @FXML
    private Tab plannerTab;

    @FXML
    private TableView<PlannerServant> sumTable;

    @FXML
    private TableColumn<PlannerServant, String> label;

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
    private boolean isLongTerm;

    public void setLongTerm(boolean longTerm) {
        isLongTerm = longTerm;
    }

    public void initialize() {
        dataManagementService = MainApp.getDataManagementService();
        tableInit();
    }

    private List<TableColumn<PlannerServant, Number>> createColumnsForAllMats() {
        List<TableColumn<PlannerServant, Number>> columns = new ArrayList<>();
        dataManagementService.getAllMaterials().forEach(mat -> addColumnForMaterial(columns, mat));
        return columns;
    }

    private void addColumnForMaterial(List<TableColumn<PlannerServant, Number>> columns, UpgradeMaterial mat) {
        TableColumn<PlannerServant, Number> newCol = new TableColumn<>();
        ImageView imageView = new ImageView(mat.getIconImage());
        newCol.setPrefWidth(MainController.SHORT_CELL_WIDTH);
        resizeIconIfNeeded(mat, newCol, imageView);
        newCol.setGraphic(imageView);
        newCol.setCellValueFactory(new PlannerServantMaterialValueFactory(mat.getId()));
        if (mat.getId() == HOLY_GRAIL_ID) {
            newCol.setCellValueFactory(new PlannerServantGrailValueFactory());
        }
        columns.add(newCol);
    }

    private void resizeIconIfNeeded(UpgradeMaterial mat, TableColumn<PlannerServant, Number> newCol, ImageView imageView) {
        if (!dataManagementService.isIconsResized()) {
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(newCol.getWidth());
            SnapshotParameters parameters = new SnapshotParameters();
            parameters.setFill(Color.TRANSPARENT);
            mat.setIconImage(imageView.snapshot(parameters, null));
        }
    }

    public void setup() {
        tabSetup();
        setupPlannerTable();
        setupSumTable();
        if (!dataManagementService.isIconsResized()) {
            dataManagementService.saveMaterialData();
        }
    }

    private void setupPlannerTable() {
        plannerTable.getColumns().addAll(createColumnsForAllMats());
        plannerTable.getStyleClass().add("planner-table");
    }

    private void setupSumTable() {
        sumTable.getColumns().addAll(createColumnsForAllMats());
        sumTable.setMaxHeight(MainController.CELL_HEIGHT * 3);
        disableSumTableHeader();
        bindColumnWidths();
        sumTable.getStyleClass().add("sum-table");
        sumTable.getItems().add(new PlannerServant());
    }

    private void disableSumTableHeader() {
        ((Pane) sumTable.getChildrenUnmodifiable().get(0)).setMaxHeight(0);
        ((Pane) sumTable.getChildrenUnmodifiable().get(0)).setMinHeight(0);
        ((Pane) sumTable.getChildrenUnmodifiable().get(0)).setPrefHeight(0);
    }

    private void bindColumnWidths() {
        label.prefWidthProperty().bind(plannerTable.getColumns().get(0).widthProperty());
        sumCurrent.prefWidthProperty().bind(getTotalWidthOfParentColumn(current));
        sumDesired.prefWidthProperty().bind(getTotalWidthOfParentColumn(desired));
        sumTable.getColumns().stream().skip(3).forEach(col -> {
            int index = sumTable.getColumns().indexOf(col);
            col.prefWidthProperty().bind(plannerTable.getColumns().get(index).widthProperty());
        });
    }

    private ObservableValue<? extends Number> getTotalWidthOfParentColumn(TableColumn<PlannerServant, ?> column) {
        DoubleBinding result = Bindings.createDoubleBinding(() -> (double) 0);
        for (TableColumn<PlannerServant, ?> col : column.getColumns()) {
            result = result.add(col.widthProperty());
        }
        return result;
    }

    public void init() {
        if (isLongTerm) {
            plannerTab.setText("LT Planner");
        } else {
            plannerTab.setText("Planner");
        }
    }

    private void tabSetup() {
        refreshTableData();
        plannerTab.setOnSelectionChanged(event -> refreshOnTabSelected());
    }

    private void refreshTableData() {
        if (isLongTerm) {
            loadLtTableData();
        } else {
            loadTableData();
        }
    }

    private void refreshOnTabSelected() {
        if (plannerTab.isSelected() && dataManagementService.isDataLoaded()) {
            refreshTableData();
            syncScrollbars();
        }
    }

    private void syncScrollbars() {
        ScrollBar sumTableBar = sumTable.lookupAll(".scroll-bar").stream().map(node -> (ScrollBar) node).filter(scrollBar -> scrollBar.getOrientation() == Orientation.HORIZONTAL).findFirst().get();
        ScrollBar plannerTableBar = plannerTable.lookupAll(".scroll-bar").stream().map(node -> (ScrollBar) node).filter(scrollBar -> scrollBar.getOrientation() == Orientation.HORIZONTAL).findFirst().get();
        sumTableBar.valueProperty().bindBidirectional(plannerTableBar.valueProperty());
    }

    private void tableInit() {
        initPlannerTable();
    }

    private void initPlannerTable() {
        plannerTable.getColumns().get(0).setPrefWidth(MainController.NAME_CELL_WIDTH);
        initCurrentInfoColumns();
        initInfoColumn(desired);
    }

    public void loadTableData() {
        plannerTable.setItems(FXCollections.observableArrayList());
    }

    public void loadLtTableData() {
        plannerTable.setItems(FXCollections.observableArrayList(createPlannerServantList()));
    }

    private void initCurrentInfoColumns() {
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
        initInfoColumn(current);
    }

    private void initInfoColumn(TableColumn<PlannerServant, ?> column) {
        column.getColumns().get(0).setPrefWidth(MainController.SHORT_CELL_WIDTH);
        column.getColumns().stream().skip(1).forEach(col -> col.setPrefWidth(MainController.CHAR_CELL_WIDTH));
    }

    private List<PlannerServant> createPlannerServantList() {
        return new PlannerServantFactory().createForLTPlanner(dataManagementService.getUserServantList());
    }
}
