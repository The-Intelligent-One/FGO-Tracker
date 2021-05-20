package com.github.theintelligentone.fgotracker.ui.controller;

import com.github.theintelligentone.fgotracker.app.MainApp;
import com.github.theintelligentone.fgotracker.domain.item.Inventory;
import com.github.theintelligentone.fgotracker.domain.item.UpgradeMaterial;
import com.github.theintelligentone.fgotracker.domain.item.UpgradeMaterialCost;
import com.github.theintelligentone.fgotracker.domain.servant.factory.PlannerServantViewFactory;
import com.github.theintelligentone.fgotracker.service.DataManagementService;
import com.github.theintelligentone.fgotracker.service.transformer.InventoryToViewTransformer;
import com.github.theintelligentone.fgotracker.ui.valuefactory.planner.InventoryValueFactory;
import com.github.theintelligentone.fgotracker.ui.valuefactory.planner.PlannerServantGrailValueFactory;
import com.github.theintelligentone.fgotracker.ui.valuefactory.planner.PlannerServantMaterialValueFactory;
import com.github.theintelligentone.fgotracker.ui.view.InventoryView;
import com.github.theintelligentone.fgotracker.ui.view.PlannerServantView;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableNumberValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
        Inventory inventory = dataManagementService.getInventory();
        sumTable.getItems().add(transformer.transform(inventory));
        Inventory planned = dataManagementService.createEmptyInventory();
        planned.setLabel("Plan");
        planned.setInventory(getSumOfNeededMats());
        sumTable.getItems().add(transformer.transform(planned));
        Inventory sum = dataManagementService.createEmptyInventory();
        sum.setLabel("Sum");
        sum.setInventory(createListOfRemainingMats(inventory, planned));
        sumTable.getItems().add(transformer.transform(sum));
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

    private List<UpgradeMaterialCost> createListOfRemainingMats(Inventory inventory, Inventory planned) {
        ObservableList<UpgradeMaterialCost> result = FXCollections.observableArrayList();
        for (int index = 0; index < inventory.getInventory().size(); index++) {
            final int currIndex = index;
            UpgradeMaterialCost mat = new UpgradeMaterialCost();
            SimpleIntegerProperty invAmount = new SimpleIntegerProperty(inventory.getInventory().get(index).getAmount());
            invAmount.addListener((observable, oldValue, newValue) -> inventory.getInventory().get(0).setAmount(newValue.intValue()));
            SimpleIntegerProperty planAmount = new SimpleIntegerProperty(planned.getInventory().get(currIndex).getAmount());
            planAmount.addListener((observable, oldValue, newValue) -> planned.getInventory().get(0).setAmount(newValue.intValue()));
            IntegerBinding sumBinding = Bindings.createIntegerBinding(() -> inventory.getInventory().get(currIndex).getAmount() - planned.getInventory().get(currIndex).getAmount(), invAmount, planAmount);
            mat.setAmount(invAmount.intValue() - planAmount.intValue());
            sumBinding.addListener((observable, oldValue, newValue) -> mat.setAmount(newValue.intValue()));
            mat.setId(inventory.getInventory().get(index).getId());
            mat.setItem(inventory.getInventory().get(index).getItem());
            result.add(mat);
        }
        return result;
    }

    private List<UpgradeMaterialCost> getSumOfNeededMats() {
        List<UpgradeMaterialCost> result = new ArrayList<>();
        for (UpgradeMaterialCost mat : dataManagementService.getInventory().getInventory()) {
            UpgradeMaterialCost matCost = new UpgradeMaterialCost();
            matCost.setId(mat.getId());
            matCost.setItem(mat.getItem());
            NumberBinding sum = Bindings.createIntegerBinding(() -> 0);
            for (PlannerServantView servant : plannerTable.getItems()) {
                TableColumn<PlannerServantView, Number> actualCol = (TableColumn<PlannerServantView, Number>) plannerTable.getColumns().stream()
                        .filter(col -> String.valueOf(mat.getItem().getId()).equalsIgnoreCase(col.getId()))
                        .findFirst().get();
                ObservableNumberValue value = (ObservableNumberValue) actualCol.getCellObservableValue(servant);
                if (value != null) {
                    sum = sum.add(value);
                }
            }
            sum.addListener((observable, oldValue, newValue) -> matCost.setAmount(newValue.intValue()));
            matCost.setAmount(sum.intValue());
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
        initPlannerTable();
    }

    private void initPlannerTable() {
        plannerTable.getColumns().get(0).setPrefWidth(MainController.NAME_CELL_WIDTH);
        initCurrentInfoColumns();
        initInfoColumn(desired);
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
        initInfoColumn(current);
    }

    private boolean validServant(TableColumn.CellDataFeatures<PlannerServantView, Number> param) {
        return param.getValue().getBaseServant() != null && param.getValue().getBaseServant().getValue() != null && param.getValue().getBaseServant().getValue().getBaseServant() != null;
    }

    private void initInfoColumn(TableColumn<PlannerServantView, ?> column) {
        column.getColumns().get(0).setPrefWidth(MainController.SHORT_CELL_WIDTH);
        column.getColumns().stream().skip(1).forEach(col -> col.setPrefWidth(MainController.CHAR_CELL_WIDTH));
    }

    private List<PlannerServantView> createPlannerServantList() {
        return new PlannerServantViewFactory().createForLTPlanner(dataManagementService.getUserServantList());
    }
}
