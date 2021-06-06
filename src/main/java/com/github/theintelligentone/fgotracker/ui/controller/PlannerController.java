package com.github.theintelligentone.fgotracker.ui.controller;

import com.github.theintelligentone.fgotracker.app.MainApp;
import com.github.theintelligentone.fgotracker.domain.item.Inventory;
import com.github.theintelligentone.fgotracker.domain.item.UpgradeMaterial;
import com.github.theintelligentone.fgotracker.domain.servant.factory.PlannerServantViewFactory;
import com.github.theintelligentone.fgotracker.domain.view.InventoryView;
import com.github.theintelligentone.fgotracker.domain.view.PlannerServantView;
import com.github.theintelligentone.fgotracker.domain.view.UpgradeMaterialCostView;
import com.github.theintelligentone.fgotracker.service.DataManagementService;
import com.github.theintelligentone.fgotracker.service.ServantUtils;
import com.github.theintelligentone.fgotracker.service.transformer.InventoryToViewTransformer;
import com.github.theintelligentone.fgotracker.ui.cellfactory.AutoCompleteTextFieldTableCell;
import com.github.theintelligentone.fgotracker.ui.valuefactory.planner.InventoryValueFactory;
import com.github.theintelligentone.fgotracker.ui.valuefactory.planner.PlannerServantGrailValueFactory;
import com.github.theintelligentone.fgotracker.ui.valuefactory.planner.PlannerServantMaterialValueFactory;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableNumberValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class PlannerController {
    private static final int HOLY_GRAIL_ID = 7999;

    @FXML
    private Tab plannerTab;

    @FXML
    private TableView<InventoryView> sumTable;

    @FXML
    private TableColumn<InventoryView, String> label;

    @FXML
    private TableColumn<InventoryView, String> sumCurrent;

    @FXML
    private TableColumn<InventoryView, String> sumDesired;

    @FXML
    private TableView<PlannerServantView> plannerTable;

    @FXML
    private TableColumn<PlannerServantView, String> nameColumn;

    @FXML
    private TableColumn<PlannerServantView, ?> current;

    @FXML
    private TableColumn<PlannerServantView, ?> desired;

    @FXML
    private TableColumn<PlannerServantView, Integer> level;

    @FXML
    private TableColumn<PlannerServantView, Integer> skill1;

    @FXML
    private TableColumn<PlannerServantView, Integer> skill2;

    @FXML
    private TableColumn<PlannerServantView, Integer> skill3;

    private DataManagementService dataManagementService;
    private ServantUtils servantUtils;
    private boolean isLongTerm;

    public void setLongTerm(boolean longTerm) {
        isLongTerm = longTerm;
    }

    public void initialize() {
        dataManagementService = MainApp.getDataManagementService();
        servantUtils = new ServantUtils();
        tableInit();
    }

    private List<TableColumn<PlannerServantView, Number>> createColumnsForAllMats() {
        List<TableColumn<PlannerServantView, Number>> columns = new ArrayList<>();
        dataManagementService.getMaterials().forEach(mat -> addColumnForMaterial(columns, mat));
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

    private List<TableColumn<InventoryView, Integer>> createColumnsForAllMatsForSum() {
        List<TableColumn<InventoryView, Integer>> columns = new ArrayList<>();
        dataManagementService.getMaterials().forEach(mat -> addSumColumnForMaterial(columns, mat));
        return columns;
    }

    private void addSumColumnForMaterial(List<TableColumn<InventoryView, Integer>> columns, UpgradeMaterial mat) {
        TableColumn<InventoryView, Integer> newCol = new TableColumn<>();
        newCol.setEditable(true);
        newCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        newCol.setCellValueFactory(new InventoryValueFactory(mat.getId()));
        newCol.setOnEditCommit(event -> {
            if (event.getRowValue().getLabel().equalsIgnoreCase("Inventory")) {
                long matId = ((InventoryValueFactory) event.getTableColumn().getCellValueFactory()).getMatId();
                event.getRowValue().getInventory().stream()
                        .filter(material -> matId == material.getId().longValue())
                        .findFirst().get()
                        .getAmount().set(servantUtils.getNewValueIfValid(event, 0, 99999));
                event.getTableView().refresh();
            }
        });
        columns.add(newCol);
    }

    private ObservableList<UpgradeMaterialCostView> createListOfRemainingMats(InventoryView inventory, InventoryView planned) {
        ObservableList<UpgradeMaterialCostView> result = FXCollections.observableArrayList(
                param -> new Observable[]{param.getAmount()});
        for (int index = 0; index < inventory.getInventory().size(); index++) {
            UpgradeMaterialCostView matAmount = inventory.getInventory().get(index);
            UpgradeMaterialCostView matPlan = planned.getInventory().get(index);
            UpgradeMaterialCostView mat = new UpgradeMaterialCostView();
            mat.setId(matAmount.getId());
            mat.setItem(matAmount.getItem());
            NumberBinding sumBinding = Bindings.createIntegerBinding(() -> 0, matAmount.getAmount(), matPlan.getAmount());
            sumBinding = sumBinding.add(matAmount.getAmount());
            sumBinding = sumBinding.subtract(matPlan.getAmount());
            mat.getAmount().bind(sumBinding);
            result.add(mat);
        }
        return result;
    }

    private ObservableList<UpgradeMaterialCostView> getSumOfNeededMats() {
        ObservableList<UpgradeMaterialCostView> result = FXCollections.observableArrayList(
                param -> new Observable[]{param.getAmount()});
        for (UpgradeMaterialCostView mat : dataManagementService.getInventory().getInventory()) {
            UpgradeMaterialCostView matCost = new UpgradeMaterialCostView();
            matCost.setId(mat.getId());
            matCost.setItem(mat.getItem());
            int sumValue = getPlannedMatUseSum(plannerTable.getItems(), mat);
            IntegerProperty sum = new SimpleIntegerProperty(sumValue);
            plannerTable.getItems().addListener((ListChangeListener<? super PlannerServantView>) c -> sum.set(
                    getPlannedMatUseSum((List<PlannerServantView>) c.getList(), mat)));
            matCost.getAmount().bind(sum);
            result.add(matCost);
        }
        return result;
    }

    private int getPlannedMatUseSum(List<PlannerServantView> servants, UpgradeMaterialCostView mat) {
        ServantUtils servantUtils = new ServantUtils();
        return servants.stream()
                .filter(servant -> servant.getBaseServant().getValue() != null && servant.getBaseServant().getValue().getBaseServant().getValue() != null)
                .map(servant -> servantUtils.getPlannedMatUse(servant, mat.getId().longValue()))
                .mapToInt(ObservableNumberValue::intValue)
                .reduce(Integer::sum).orElse(0);
    }

    private void disableSumTableHeader() {
        ((Pane) sumTable.getChildrenUnmodifiable().get(0)).setMaxHeight(0);
        ((Pane) sumTable.getChildrenUnmodifiable().get(0)).setMinHeight(0);
        ((Pane) sumTable.getChildrenUnmodifiable().get(0)).setPrefHeight(0);
    }

    private void bindColumnWidths() {
        label.prefWidthProperty().bind(nameColumn.widthProperty());
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
        setupTableData();
        plannerTab.setOnSelectionChanged(event -> refreshOnTabSelected());
    }

    private void setupTableData() {
        if (isLongTerm) {
            loadLtTableData();
            plannerTable.setRowFactory(param -> {
                PseudoClass lastRow = PseudoClass.getPseudoClass("last-row");
                TableRow<PlannerServantView> row = new TableRow<>() {
                    @Override
                    public void updateIndex(int index) {
                        super.updateIndex(index);
                        pseudoClassStateChanged(lastRow,
                                index >= 0 && index == plannerTable.getItems().size() - 1);
                    }
                };
                createContextMenuForTableRow(row);
                return row;
            });
        } else {
            loadTableData();
            plannerTable.setEditable(true);
            plannerTable.setRowFactory(param -> {
                PseudoClass lastRow = PseudoClass.getPseudoClass("last-row");
                TableRow<PlannerServantView> row = new TableRow<>() {
                    @Override
                    public void updateIndex(int index) {
                        super.updateIndex(index);
                        pseudoClassStateChanged(lastRow,
                                index >= 0 && index == plannerTable.getItems().size() - 1);
                    }
                };
                createContextMenuForTableRow(row);
                return row;
            });
            nameColumn.setCellFactory(
                    AutoCompleteTextFieldTableCell.forTableColumn(dataManagementService.getUserServantNameList()));
        }
    }

    private void createContextMenuForTableRow(TableRow<PlannerServantView> row) {
        MenuItem importInventoryButton = new MenuItem("Import inventory from csv");
        importInventoryButton.setOnAction(event -> importInventoryFromCsv());
        MenuItem importPlannerServantsButton = new MenuItem("Import servants for planner from CSV");
        importPlannerServantsButton.setOnAction(event -> importPlannerServantsFromCsv());
        MenuItem removeRowButton = new MenuItem("Delete row");
        removeRowButton.setOnAction(event -> dataManagementService.removePlannerServant(row.getItem()));
        MenuItem clearRowButton = new MenuItem("Clear row");
        clearRowButton.setOnAction(event -> dataManagementService.erasePlannerServant(row.getItem()));
        MenuItem addRowAboveButton = new MenuItem("Insert row above");
        addRowAboveButton.setOnAction(
                event -> dataManagementService.savePlannerServant(row.getTableView().getItems().indexOf(row.getItem()),
                        new PlannerServantView()));
        MenuItem addRowBelowButton = new MenuItem("Insert row below");
        addRowBelowButton.setOnAction(
                event -> dataManagementService.savePlannerServant(row.getTableView().getItems().indexOf(row.getItem()) + 1,
                        new PlannerServantView()));
        MenuItem addMultipleRowsButton = new MenuItem("Add X new rows");
        addMultipleRowsButton.setOnAction(event -> {
            TextInputDialog prompt = new TextInputDialog("10");
            prompt.setContentText("How many new rows to add?");
            prompt.setTitle("Add X new rows");
            prompt.setHeaderText("");
            prompt.showAndWait().ifPresent(s -> IntStream.range(0, Integer.parseInt(s)).forEach(
                    i -> dataManagementService.savePlannerServant(new PlannerServantView())));
        });
        ContextMenu menu = new ContextMenu(importInventoryButton);
        if (!isLongTerm) {
            menu.getItems().addAll(importPlannerServantsButton, addRowAboveButton, addRowBelowButton, addMultipleRowsButton,
                    clearRowButton, removeRowButton);
        }
        row.contextMenuProperty().bind(Bindings.when(row.emptyProperty()).then((ContextMenu) null).otherwise(menu));
    }

    private void refreshOnTabSelected() {
        if (plannerTab.isSelected() && dataManagementService.isDataLoaded()) {
            syncScrollbars();
        }
    }

    private void syncScrollbars() {
        ScrollBar sumTableBar = sumTable.lookupAll(".scroll-bar").stream().map(node -> (ScrollBar) node).filter(
                scrollBar -> scrollBar.getOrientation() == Orientation.HORIZONTAL).findFirst().get();
        ScrollBar plannerTableBar = plannerTable.lookupAll(".scroll-bar").stream().map(node -> (ScrollBar) node).filter(
                scrollBar -> scrollBar.getOrientation() == Orientation.HORIZONTAL).findFirst().get();
        sumTableBar.valueProperty().bindBidirectional(plannerTableBar.valueProperty());
    }

    private void tableInit() {
        sumTable.getSelectionModel().setCellSelectionEnabled(true);
        sumTable.setFixedCellSize(MainController.CELL_HEIGHT);
        initPlannerTable();
    }

    private void initPlannerTable() {
        plannerTable.getSelectionModel().setCellSelectionEnabled(true);
        plannerTable.setFixedCellSize(MainController.CELL_HEIGHT);
        nameColumn.setPrefWidth(MainController.NAME_CELL_WIDTH);
        nameColumn.setOnEditCommit(event -> {
            if (event.getNewValue().isEmpty()) {
                dataManagementService.erasePlannerServant(event.getRowValue());
            } else {
                dataManagementService.replaceBaseServantInPlannerRow(event.getTablePosition().getRow(), event.getRowValue(),
                        event.getNewValue());
                event.getTableView().refresh();
            }
        });
        desired.getColumns().get(0).setOnEditCommit(event -> {
            if (event.getRowValue().getSvtId().longValue() != 0) {
                event.getRowValue().getDesLevel().set(
                        servantUtils.getNewValueIfValid((TableColumn.CellEditEvent<?, Integer>) event, 1, 100));
                plannerTable.refresh();
            }
        });
        desired.getColumns().get(1).setOnEditCommit(event -> {
            if (event.getRowValue().getSvtId().longValue() != 0) {
                event.getRowValue().getDesSkill1().set(
                        servantUtils.getNewValueIfValid((TableColumn.CellEditEvent<?, Integer>) event, 1, 10));
                plannerTable.refresh();
            }
        });
        desired.getColumns().get(2).setOnEditCommit(event -> {
            if (event.getRowValue().getSvtId().longValue() != 0) {
                event.getRowValue().getDesSkill2().set(
                        servantUtils.getNewValueIfValid((TableColumn.CellEditEvent<?, Integer>) event, 1, 10));
                plannerTable.refresh();
            }
        });
        desired.getColumns().get(3).setOnEditCommit(event -> {
            if (event.getRowValue().getSvtId().longValue() != 0) {
                event.getRowValue().getDesSkill3().set(
                        servantUtils.getNewValueIfValid((TableColumn.CellEditEvent<?, Integer>) event, 1, 10));
                plannerTable.refresh();
            }
        });
        initCurrentInfoColumns();
        desired.getColumns().forEach(col1 -> col1.setPrefWidth(MainController.SHORT_CELL_WIDTH));
        desired.getColumns().forEach(col -> {
            TableColumn<PlannerServantView, Integer> actualCol = (TableColumn<PlannerServantView, Integer>) col;
            actualCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        });
    }

    public void loadTableData() {
        plannerTable.setItems(dataManagementService.getPlannerServantList());
    }

    public void loadLtTableData() {
        plannerTable.setItems(createLTPlannerServantList());
    }

    private void initCurrentInfoColumns() {
        level.setCellValueFactory(param -> {
            IntegerProperty level = null;
            if (validServant(param)) {
                level = new SimpleIntegerProperty(1);
                level.bind(param.getValue().getBaseServant().getValue().getLevel());
            }
            return level == null ? null : level.asObject();
        });
        skill1.setCellValueFactory(param -> {
            IntegerProperty skill1 = null;
            if (validServant(param)) {
                skill1 = param.getValue().getBaseServant().getValue().getSkillLevel1();
            }
            return skill1 == null ? null : skill1.asObject();
        });
        skill2.setCellValueFactory(param -> {
            IntegerProperty skill2 = null;
            if (validServant(param)) {
                skill2 = param.getValue().getBaseServant().getValue().getSkillLevel2();
            }
            return skill2 == null ? null : skill2.asObject();
        });
        skill3.setCellValueFactory(param -> {
            IntegerProperty skill3 = null;
            if (validServant(param)) {
                skill3 = param.getValue().getBaseServant().getValue().getSkillLevel3();
            }
            return skill3 == null ? null : skill3.asObject();
        });
        current.getColumns().forEach(col -> col.setPrefWidth(MainController.SHORT_CELL_WIDTH));
    }

    private void importInventoryFromCsv() {
        if (dataManagementService.isDataLoaded()) {
            File csvFile = importCsvFile();
            if (csvFile != null) {
                loadInventoryDataFromCsv(csvFile);
            }
        } else {
            showNotLoadedYetAlert();
        }
    }

    private void importPlannerServantsFromCsv() {
        if (dataManagementService.isDataLoaded()) {
            File csvFile = importCsvFile();
            if (csvFile != null) {
                loadPlannerDataFromCsv(csvFile);
            }
        } else {
            showNotLoadedYetAlert();
        }
    }

    private File importCsvFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setTitle("CSV to import");
        return fileChooser.showOpenDialog(Stage.getWindows().get(0));
    }

    private void loadInventoryDataFromCsv(File csvFile) {
        List<String> notFoundNames = dataManagementService.importInventoryFromCsv(csvFile);
        if (notFoundNames != null && !notFoundNames.isEmpty()) {
            displayNotFoundAlert(notFoundNames);
        }
    }

    private void loadPlannerDataFromCsv(File csvFile) {
        List<String> notFoundNames = dataManagementService.importPlannerServantsFromCsv(csvFile);
        if (notFoundNames != null && !notFoundNames.isEmpty()) {
            displayNotFoundAlert(notFoundNames);
        }
    }

    private void displayNotFoundAlert(List<String> notFoundNames) {
        StringBuilder stringBuilder = new StringBuilder();
        Alert notFoundAlert = new Alert(Alert.AlertType.WARNING);
        notFoundNames.forEach(str -> {
            stringBuilder.append(str);
            stringBuilder.append("\n");
        });
        notFoundAlert.setHeaderText("Servants/Materials that couldn't be imported:");
        notFoundAlert.setContentText(stringBuilder.toString());
        notFoundAlert.show();
    }

    private void showNotLoadedYetAlert() {
        Alert loadingAlert = new Alert(Alert.AlertType.WARNING);
        loadingAlert.setContentText("Servant data still loading.");
        loadingAlert.show();
    }

    private boolean validServant(TableColumn.CellDataFeatures<PlannerServantView, Integer> param) {
        return param.getValue().getBaseServant() != null && param.getValue().getBaseServant().getValue() != null && param.getValue().getBaseServant().getValue().getBaseServant() != null;
    }

    private ObservableList<PlannerServantView> createLTPlannerServantList() {
        return new PlannerServantViewFactory().createForLTPlanner(dataManagementService.getUserServantList());
    }
}
