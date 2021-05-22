package com.github.theintelligentone.fgotracker.ui.controller;

import com.github.theintelligentone.fgotracker.app.MainApp;
import com.github.theintelligentone.fgotracker.domain.item.Inventory;
import com.github.theintelligentone.fgotracker.domain.item.UpgradeMaterial;
import com.github.theintelligentone.fgotracker.domain.servant.factory.PlannerServantViewFactory;
import com.github.theintelligentone.fgotracker.service.DataManagementService;
import com.github.theintelligentone.fgotracker.service.transformer.InventoryToViewTransformer;
import com.github.theintelligentone.fgotracker.ui.valuefactory.planner.InventoryValueFactory;
import com.github.theintelligentone.fgotracker.ui.valuefactory.planner.PlannerServantGrailValueFactory;
import com.github.theintelligentone.fgotracker.ui.valuefactory.planner.PlannerServantMaterialValueFactory;
import com.github.theintelligentone.fgotracker.domain.view.InventoryView;
import com.github.theintelligentone.fgotracker.domain.view.PlannerServantView;
import com.github.theintelligentone.fgotracker.domain.view.UpgradeMaterialCostView;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.value.ObservableNumberValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.converter.IntegerStringConverter;

import java.util.ArrayList;
import java.util.List;

public class PlannerController {
    private static final int HOLY_GRAIL_ID = 7999;

    @FXML
    private Tab plannerTab;

    @FXML
    private TableView<InventoryView> sumTable;

    @FXML
    private TableColumn<PlannerServantView, String> label;

    @FXML
    private TableColumn<PlannerServantView, String> sumCurrent;

    @FXML
    private TableColumn<PlannerServantView, String> sumDesired;

    @FXML
    private TableView<PlannerServantView> plannerTable;

    @FXML
    private TableColumn<PlannerServantView, ?> current;

    @FXML
    private TableColumn<PlannerServantView, ?> desired;

    @FXML
    private TableColumn<PlannerServantView, Number> level;

    @FXML
    private TableColumn<PlannerServantView, Number> skill1;

    @FXML
    private TableColumn<PlannerServantView, Number> skill2;

    @FXML
    private TableColumn<PlannerServantView, Number> skill3;

    private DataManagementService dataManagementService;
    private boolean isLongTerm;

    public void setLongTerm(boolean longTerm) {
        isLongTerm = longTerm;
    }

    public void initialize() {
        dataManagementService = MainApp.getDataManagementService();
        tableInit();
    }

    private List<TableColumn<PlannerServantView, Number>> createColumnsForAllMats() {
        List<TableColumn<PlannerServantView, Number>> columns = new ArrayList<>();
        dataManagementService.getAllMaterials().forEach(mat -> addColumnForMaterial(columns, mat));
        return columns;
    }

    private void addColumnForMaterial(List<TableColumn<PlannerServantView, Number>> columns, UpgradeMaterial mat) {
        TableColumn<PlannerServantView, Number> newCol = new TableColumn<>();
        ImageView imageView = new ImageView(mat.getIconImage());
        newCol.setId(String.valueOf(mat.getId()));
        newCol.setPrefWidth(MainController.SHORT_CELL_WIDTH);
        resizeIconIfNeeded(mat, newCol, imageView);
        newCol.setGraphic(imageView);
        newCol.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item.intValue() <= 0) {
                    setText(null);
                } else {
                    setText(item.toString());
                }
            }
        });
        newCol.setCellValueFactory(new PlannerServantMaterialValueFactory(mat.getId()));
        if (mat.getId() == HOLY_GRAIL_ID) {
            newCol.setCellValueFactory(new PlannerServantGrailValueFactory());
        }
        columns.add(newCol);
    }

    private void resizeIconIfNeeded(UpgradeMaterial mat, TableColumn<PlannerServantView, Number> newCol, ImageView imageView) {
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
        InventoryToViewTransformer transformer = new InventoryToViewTransformer();
        sumTable.getColumns().addAll(createColumnsForAllMatsForSum());
        sumTable.setMaxHeight(MainController.CELL_HEIGHT * 3);
        disableSumTableHeader();
        bindColumnWidths();
        sumTable.getStyleClass().add("sum-table");
        InventoryView inventory = dataManagementService.getInventory();
        sumTable.getItems().add(inventory);
        Inventory plannedBase = dataManagementService.createEmptyInventory();
        plannedBase.setLabel("Plan");
        InventoryView planned = transformer.transform(plannedBase);
        planned.setInventory(getSumOfNeededMats());
        sumTable.getItems().add(planned);
        Inventory sumBase = dataManagementService.createEmptyInventory();
        sumBase.setLabel("Sum");
        InventoryView sum = transformer.transform(sumBase);
        sum.setInventory(createListOfRemainingMats(inventory, planned));
        sumTable.getItems().add(sum);
    }

    private List<TableColumn<InventoryView, Number>> createColumnsForAllMatsForSum() {
        List<TableColumn<InventoryView, Number>> columns = new ArrayList<>();
        dataManagementService.getAllMaterials().forEach(mat -> addSumColumnForMaterial(columns, mat));
        return columns;
    }

    private void addSumColumnForMaterial(List<TableColumn<InventoryView, Number>> columns, UpgradeMaterial mat) {
        TableColumn<InventoryView, Number> newCol = new TableColumn<>();
        newCol.setCellValueFactory(new InventoryValueFactory(mat.getId()));
        columns.add(newCol);
    }

    private ObservableList<UpgradeMaterialCostView> createListOfRemainingMats(InventoryView inventory, InventoryView planned) {
        ObservableList<UpgradeMaterialCostView> result = FXCollections.observableArrayList();
        for (int index = 0; index < inventory.getInventory().size(); index++) {
            final int currIndex = index;
            UpgradeMaterialCostView mat = new UpgradeMaterialCostView();
            IntegerBinding sumBinding = Bindings.createIntegerBinding(() -> 0);
            sumBinding.add(inventory.getInventory().get(currIndex).getAmount());
            sumBinding.add(planned.getInventory().get(currIndex).getAmount());
            mat.getAmount().bind(sumBinding);
            mat.setId(inventory.getInventory().get(index).getId());
            mat.setItem(inventory.getInventory().get(index).getItem());
            result.add(mat);
        }
        return result;
    }

    private ObservableList<UpgradeMaterialCostView> getSumOfNeededMats() {
        ObservableList<UpgradeMaterialCostView> result = FXCollections.observableArrayList();
        for (UpgradeMaterialCostView mat : dataManagementService.getInventory().getInventory()) {
            UpgradeMaterialCostView matCost = new UpgradeMaterialCostView();
            matCost.setId(mat.getId());
            matCost.setItem(mat.getItem());
            ObservableList<ObservableNumberValue> neededValues = FXCollections.observableArrayList();
            NumberBinding sum = Bindings.createIntegerBinding(() -> 0, neededValues);
            for (PlannerServantView servant : plannerTable.getItems()) {
                TableColumn<PlannerServantView, Number> actualCol = (TableColumn<PlannerServantView, Number>) plannerTable.getColumns().stream()
                        .filter(col -> String.valueOf(mat.getItem().getValue().getId()).equalsIgnoreCase(col.getId()))
                        .findFirst().get();
                ObservableNumberValue matAmount = (ObservableNumberValue) actualCol.getCellObservableValue(servant);
                if (matAmount != null) {
                    neededValues.add(matAmount);
                    sum.add(matAmount);
                }
            }
            matCost.getAmount().bind(sum);
            result.add(matCost);
        }
        return result;
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

    private ObservableValue<? extends Number> getTotalWidthOfParentColumn(TableColumn<PlannerServantView, ?> column) {
        DoubleBinding result = Bindings.createDoubleBinding(() -> (double) 0);
        for (TableColumn<PlannerServantView, ?> col : column.getColumns()) {
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
        sumTable.setFixedCellSize(MainController.CELL_HEIGHT);
        initPlannerTable();
    }

    private void initPlannerTable() {
        plannerTable.setFixedCellSize(MainController.CELL_HEIGHT);
        plannerTable.getColumns().get(0).setPrefWidth(MainController.NAME_CELL_WIDTH);
        initCurrentInfoColumns();
        desired.getColumns().stream().forEach(col1 -> col1.setPrefWidth(MainController.SHORT_CELL_WIDTH));
        desired.getColumns().forEach(col -> {
            TableColumn<PlannerServantView, Integer> actualCol = (TableColumn<PlannerServantView, Integer>) col;
            actualCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        });
    }

    public void loadTableData() {
        plannerTable.setItems(FXCollections.observableArrayList());
    }

    public void loadLtTableData() {
        plannerTable.setItems(FXCollections.observableArrayList(createPlannerServantList()));
    }

    private void initCurrentInfoColumns() {
        level.setCellValueFactory(param -> {
            IntegerProperty level = null;
            if (validServant(param)) {
                level = param.getValue().getBaseServant().getValue().getLevel();
            }
            return level;
        });
        skill1.setCellValueFactory(param -> {
            IntegerProperty skill1 = null;
            if (validServant(param)) {
                skill1 = param.getValue().getBaseServant().getValue().getSkillLevel1();
            }
            return skill1;
        });
        skill2.setCellValueFactory(param -> {
            IntegerProperty skill2 = null;
            if (validServant(param)) {
                skill2 = param.getValue().getBaseServant().getValue().getSkillLevel1();
            }
            return skill2;
        });
        skill3.setCellValueFactory(param -> {
            IntegerProperty skill3 = null;
            if (validServant(param)) {
                skill3 = param.getValue().getBaseServant().getValue().getSkillLevel1();
            }
            return skill3;
        });
        current.getColumns().stream().forEach(col -> col.setPrefWidth(MainController.SHORT_CELL_WIDTH));
    }

    private boolean validServant(TableColumn.CellDataFeatures<PlannerServantView, Number> param) {
        return param.getValue().getBaseServant() != null && param.getValue().getBaseServant().getValue() != null && param.getValue().getBaseServant().getValue().getBaseServant() != null;
    }

    private List<PlannerServantView> createPlannerServantList() {
        return new PlannerServantViewFactory().createForLTPlanner(dataManagementService.getUserServantList());
    }
}
