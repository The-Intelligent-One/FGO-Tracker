package com.github.theintelligentone.fgotracker.ui.controller;

import com.github.theintelligentone.fgotracker.app.MainApp;
import com.github.theintelligentone.fgotracker.domain.item.Inventory;
import com.github.theintelligentone.fgotracker.domain.item.UpgradeMaterial;
import com.github.theintelligentone.fgotracker.domain.other.PlannerType;
import com.github.theintelligentone.fgotracker.domain.servant.factory.PlannerServantViewFactory;
import com.github.theintelligentone.fgotracker.domain.view.InventoryView;
import com.github.theintelligentone.fgotracker.domain.view.PlannerServantView;
import com.github.theintelligentone.fgotracker.domain.view.UpgradeMaterialCostView;
import com.github.theintelligentone.fgotracker.service.ServantUtils;
import com.github.theintelligentone.fgotracker.service.datamanagement.DataManagementServiceFacade;
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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

@Slf4j
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

    private DataManagementServiceFacade dataManagementServiceFacade;
    @Setter
    private PlannerType plannerType;

    public void initialize() {
        dataManagementServiceFacade = MainApp.getDataManagementServiceFacade();
        tableInit();
    }

    private List<TableColumn<PlannerServantView, Number>> createColumnsForAllMats() {
        List<TableColumn<PlannerServantView, Number>> columns = new ArrayList<>();
        dataManagementServiceFacade.getMaterials().forEach(mat -> addColumnForMaterial(columns, mat));
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
        if (dataManagementServiceFacade.isIconsNotResized()) {
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(newCol.getWidth());
            SnapshotParameters parameters = new SnapshotParameters();
            parameters.setFill(Color.TRANSPARENT);
            mat.setIconImage(imageView.snapshot(parameters, null));
        }
    }

    public void setup() {
        plannerTab.setOnSelectionChanged(event -> refreshOnTabSelected());
        setupTables();
        if (dataManagementServiceFacade.isIconsNotResized()) {
            dataManagementServiceFacade.saveMaterialData();
        }
    }

    private void setupTables() {
        setupTableData();
        setupPlannerTable();
        setupSumTable();
    }

    private void setupTableData() {
        if (PlannerType.LT == plannerType) {
            loadLTData();
        } else {
            plannerTable.setItems(dataManagementServiceFacade.getPaddedPlannerServantList(plannerType));
        }
    }

    private void setupPlannerTable() {
        if (PlannerType.LT != plannerType) {
            plannerTable.setEditable(true);
            nameColumn.setCellFactory(
                    AutoCompleteTextFieldTableCell.forTableColumn(dataManagementServiceFacade.getUserServantNameList()));
        }
        plannerTable.getColumns().addAll(createColumnsForAllMats());
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
            createContextMenuForPlannerTableRow(row);
            return row;
        });
    }

    private void setupSumTable() {
        disableSumTableHeader();
        sumTable.setMaxHeight(MainController.CELL_HEIGHT * 3);
        sumTable.setRowFactory(param -> {
            TableRow<InventoryView> row = new TableRow<>() {
                @Override
                public void updateIndex(int index) {
                    super.updateIndex(index);
                }
            };
            createContextMenuForInventoryTableRow(row);
            return row;
        });
        setupSumTableData();
        bindColumnWidths();
    }

    private void setupSumTableData() {
        InventoryToViewTransformer transformer = new InventoryToViewTransformer();
        sumTable.getColumns().addAll(createColumnsForAllMatsForSum());
        sumTable.getStyleClass().add("sum-table");
        InventoryView inventory = dataManagementServiceFacade.getInventory();
        sumTable.getItems().add(inventory);
        Inventory plannedBase = dataManagementServiceFacade.createEmptyInventory();
        plannedBase.setLabel("Plan");
        InventoryView planned = transformer.transform(plannedBase);
        planned.setInventory(getSumOfNeededMats());
        sumTable.getItems().add(planned);
        Inventory sumBase = dataManagementServiceFacade.createEmptyInventory();
        sumBase.setLabel("Sum");
        InventoryView sum = transformer.transform(sumBase);
        sum.setInventory(createListOfRemainingMats(inventory, planned));
        sumTable.getItems().add(sum);
    }

    private List<TableColumn<InventoryView, Integer>> createColumnsForAllMatsForSum() {
        List<TableColumn<InventoryView, Integer>> columns = new ArrayList<>();
        dataManagementServiceFacade.getMaterials().forEach(mat -> addSumColumnForMaterial(columns, mat));
        return columns;
    }

    private void addSumColumnForMaterial(List<TableColumn<InventoryView, Integer>> columns, UpgradeMaterial mat) {
        TableColumn<InventoryView, Integer> newCol = new TableColumn<>();
        newCol.setEditable(true);
        newCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        newCol.setCellValueFactory(new InventoryValueFactory(mat.getId()));
        newCol.setOnEditCommit(event -> {
            if ("Inventory".equalsIgnoreCase(event.getRowValue().getLabel())) {
                long matId = ((InventoryValueFactory) event.getTableColumn().getCellValueFactory()).getMatId();
                event.getRowValue().getInventory().stream()
                        .filter(material -> matId == material.idProperty().longValue())
                        .findFirst().get()
                        .amountProperty().set(getNewValueIfValid(event, 0, 99_999));
                event.getTableView().refresh();
            }
        });
        columns.add(newCol);
    }

    private ObservableList<UpgradeMaterialCostView> createListOfRemainingMats(InventoryView inventory, InventoryView planned) {
        ObservableList<UpgradeMaterialCostView> result = FXCollections.observableArrayList(
                param -> new Observable[]{param.amountProperty()});
        for (int index = 0; index < inventory.getInventory().size(); index++) {
            UpgradeMaterialCostView matAmount = inventory.getInventory().get(index);
            UpgradeMaterialCostView matPlan = planned.getInventory().get(index);
            UpgradeMaterialCostView mat = new UpgradeMaterialCostView();
            mat.setId(matAmount.idProperty());
            mat.setItem(matAmount.itemProperty());
            NumberBinding sumBinding = Bindings.createIntegerBinding(() -> 0, matAmount.amountProperty(),
                    matPlan.amountProperty());
            sumBinding = sumBinding.add(matAmount.amountProperty());
            sumBinding = sumBinding.subtract(matPlan.amountProperty());
            mat.amountProperty().bind(sumBinding);
            result.add(mat);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private ObservableList<UpgradeMaterialCostView> getSumOfNeededMats() {
        ObservableList<UpgradeMaterialCostView> result = FXCollections.observableArrayList(
                param -> new Observable[]{param.amountProperty()});
        for (UpgradeMaterialCostView mat : dataManagementServiceFacade.getInventory().getInventory()) {
            UpgradeMaterialCostView matCost = new UpgradeMaterialCostView();
            matCost.setId(mat.idProperty());
            matCost.setItem(mat.itemProperty());
            int sumValue = getPlannedMatUseSum(plannerTable.getItems(), mat);
            IntegerProperty sum = new SimpleIntegerProperty(sumValue);
            plannerTable.getItems().addListener((ListChangeListener<? super PlannerServantView>) c -> sum.set(
                    getPlannedMatUseSum((List<PlannerServantView>) c.getList(), mat)));
            matCost.amountProperty().bind(sum);
            result.add(matCost);
        }
        return result;
    }

    private int getPlannedMatUseSum(List<PlannerServantView> servants, UpgradeMaterialCostView mat) {
        return servants.stream()
                .filter(servant -> servant.baseServantProperty().getValue() != null && servant.baseServantProperty().getValue().baseServantProperty().getValue() != null)
                .map(servant -> ServantUtils.getPlannedMatUse(servant, mat.idProperty().longValue()))
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
        switch (plannerType) {
            case LT:
                plannerTab.setText("LT Planner");
                break;
            case REGULAR:
                plannerTab.setText("Planner");
                break;
            case PRIORITY:
                plannerTab.setText("Priority Planner");
                break;
            default:
                log.error("ERROR: Planner type not set correctly");
        }
    }

    private void createContextMenuForInventoryTableRow(TableRow<InventoryView> row) {
        ContextMenu menu = createBasicPlannerContextMenu();
        row.contextMenuProperty().bind(Bindings.when(row.emptyProperty()).then((ContextMenu) null).otherwise(menu));
    }

    private void createContextMenuForPlannerTableRow(TableRow<PlannerServantView> row) {
        ContextMenu menu = createBasicPlannerContextMenu();
        if (PlannerType.LT != plannerType) {
            List<MenuItem> editableMenuItems = createEditablePlannerTableMenuItems(row);
            menu.getItems().addAll(editableMenuItems);
        }
        row.contextMenuProperty().bind(Bindings.when(row.emptyProperty()).then((ContextMenu) null).otherwise(menu));
    }

    private ContextMenu createBasicPlannerContextMenu() {
        MenuItem importInventoryButton = new MenuItem("Import inventory from csv");
        importInventoryButton.setOnAction(event -> importInventoryFromCsv());
        return new ContextMenu(importInventoryButton);
    }

    private List<MenuItem> createEditablePlannerTableMenuItems(TableRow<PlannerServantView> row) {
        List<MenuItem> editableMenuItems = new ArrayList<>();
        addNewMenuItem(editableMenuItems, "Import servants for planner from CSV", event -> importPlannerServantsFromCsv());
        addNewMenuItem(editableMenuItems, "Delete row",
                event -> dataManagementServiceFacade.removePlannerServant(row.getItem(), plannerType));
        addNewMenuItem(editableMenuItems, "Clear row",
                event -> dataManagementServiceFacade.erasePlannerServant(row.getItem(), plannerType));
        addNewMenuItem(editableMenuItems, "Insert row above",
                event -> dataManagementServiceFacade.savePlannerServant(row.getTableView().getItems().indexOf(row.getItem()),
                        new PlannerServantView(), plannerType));
        addNewMenuItem(editableMenuItems, "Insert row below",
                event -> dataManagementServiceFacade.savePlannerServant(row.getTableView().getItems().indexOf(row.getItem()) + 1,
                        new PlannerServantView(), plannerType));
        addNewMenuItem(editableMenuItems, "Add X new rows", event -> {
            TextInputDialog prompt = new TextInputDialog("10");
            prompt.setContentText("How many new rows to add?");
            prompt.setTitle("Add X new rows");
            prompt.setHeaderText("");
            prompt.showAndWait().ifPresent(s -> IntStream.range(0, Integer.parseInt(s)).forEach(
                    i -> dataManagementServiceFacade.savePlannerServant(new PlannerServantView(), plannerType)));
        });
        return editableMenuItems;
    }

    private void addNewMenuItem(List<MenuItem> editableMenuItems, String text, EventHandler<ActionEvent> action) {
        MenuItem importPlannerServantsButton = new MenuItem(text);
        importPlannerServantsButton.setOnAction(action);
        editableMenuItems.add(importPlannerServantsButton);
    }

    private void refreshOnTabSelected() {
        if (plannerTab.isSelected() && dataManagementServiceFacade.isDataLoaded()) {
            syncScrollbars();
        }
    }

    private void syncScrollbars() {
        ScrollBar sumTableBar = getHorizontalScrollBar(sumTable);
        ScrollBar plannerTableBar = getHorizontalScrollBar(plannerTable);
        sumTableBar.valueProperty().bindBidirectional(plannerTableBar.valueProperty());
    }

    private ScrollBar getHorizontalScrollBar(TableView<?> table) {
        return table.lookupAll(".scroll-bar").stream().map(node -> (ScrollBar) node).filter(
                scrollBar -> scrollBar.getOrientation() == Orientation.HORIZONTAL).findFirst().get();
    }

    private void tableInit() {
        sumTable.getSelectionModel().setCellSelectionEnabled(true);
        sumTable.setFixedCellSize(MainController.CELL_HEIGHT);
        initPlannerTable();
    }

    private void initPlannerTable() {
        plannerTable.getSelectionModel().setCellSelectionEnabled(true);
        plannerTable.setFixedCellSize(MainController.CELL_HEIGHT);
        initDesiredInfoColumns();
        initCurrentInfoColumns();
    }

    @SuppressWarnings("unchecked")
    private void initDesiredInfoColumns() {
        initDesiredInfoColumn(0, event -> event.getRowValue().desLevelProperty(), 1, 100);
        initDesiredInfoColumn(1, event -> event.getRowValue().desSkill1Property(), 1, 10);
        initDesiredInfoColumn(2, event -> event.getRowValue().desSkill2Property(), 1, 10);
        initDesiredInfoColumn(3, event -> event.getRowValue().desSkill3Property(), 1, 10);
        desired.getColumns().forEach(col1 -> col1.setPrefWidth(MainController.SHORT_CELL_WIDTH));
        desired.getColumns().forEach(col -> {
            TableColumn<PlannerServantView, Integer> actualCol = (TableColumn<PlannerServantView, Integer>) col;
            actualCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        });
    }

    @SuppressWarnings("unchecked")
    private void initDesiredInfoColumn(int columnIndex,
                                       Function<TableColumn.CellEditEvent<PlannerServantView, ?>, IntegerProperty> getProperty,
                                       int min, int max) {
        desired.getColumns().get(columnIndex).setOnEditCommit(event -> {
            if (event.getRowValue().svtIdProperty().longValue() != 0) {
                getProperty.apply(event).set(getNewValueIfValid((TableColumn.CellEditEvent<?, Integer>) event, min, max));
                plannerTable.refresh();
            }
        });
    }

    private void setCurrentInfoColumnValueFactory(TableColumn<PlannerServantView, Integer> column,
                                                  Function<TableColumn.CellDataFeatures<PlannerServantView, Integer>, IntegerProperty> getProperty) {
        column.setCellValueFactory(param -> {
            IntegerProperty level = null;
            if (param.getValue().svtIdProperty().longValue() != 0) {
                level = new SimpleIntegerProperty(1);
                level.bind(getProperty.apply(param));
            }
            return level == null ? null : level.asObject();
        });
    }

    public void loadLTData() {
        plannerTable.setItems(createLTPlannerServantList());
    }

    private void initCurrentInfoColumns() {
        nameColumn.setPrefWidth(MainController.NAME_CELL_WIDTH);
        nameColumn.setOnEditCommit(event -> {
            if (event.getNewValue().isEmpty()) {
                dataManagementServiceFacade.erasePlannerServant(event.getRowValue(), plannerType);
            } else {
                dataManagementServiceFacade.replaceBaseServantInPlannerRow(event.getTablePosition().getRow(), event.getRowValue(),
                        event.getNewValue(), plannerType);
                event.getTableView().refresh();
            }
        });
        setCurrentInfoColumnValueFactory(level, param -> param.getValue().baseServantProperty().getValue().levelProperty());
        setEditEventForCurrentInfoColumn(level, param -> param.getRowValue().baseServantProperty().getValue().levelProperty(), 1,
                100);
        setCurrentInfoColumnValueFactory(skill1,
                param -> param.getValue().baseServantProperty().getValue().skillLevel1Property());
        setEditEventForCurrentInfoColumn(skill1,
                param -> param.getRowValue().baseServantProperty().getValue().skillLevel1Property(), 1, 10);
        setCurrentInfoColumnValueFactory(skill2,
                param -> param.getValue().baseServantProperty().getValue().skillLevel2Property());
        setEditEventForCurrentInfoColumn(skill2,
                param -> param.getRowValue().baseServantProperty().getValue().skillLevel2Property(), 1, 10);
        setCurrentInfoColumnValueFactory(skill3,
                param -> param.getValue().baseServantProperty().getValue().skillLevel3Property());
        setEditEventForCurrentInfoColumn(skill3,
                param -> param.getRowValue().baseServantProperty().getValue().skillLevel3Property(), 1, 10);
        current.getColumns().forEach(col -> col.setPrefWidth(MainController.SHORT_CELL_WIDTH));
    }

    private void setEditEventForCurrentInfoColumn(TableColumn<PlannerServantView, Integer> column,
                                                  Function<TableColumn.CellEditEvent<PlannerServantView, ?>, IntegerProperty> getProperty,
                                                  int min, int max) {
        column.setOnEditCommit(event -> {
            if (event.getRowValue().svtIdProperty().longValue() != 0) {
                getProperty.apply(event).set(getNewValueIfValid(event, min, max));
                plannerTable.refresh();
            }
        });
        column.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
    }

    private void importInventoryFromCsv() {
        if (dataManagementServiceFacade.isDataLoaded()) {
            File csvFile = importCsvFile();
            if (csvFile != null) {
                loadInventoryDataFromCsv(csvFile);
            }
        } else {
            showNotLoadedYetAlert();
        }
    }

    private void importPlannerServantsFromCsv() {
        if (dataManagementServiceFacade.isDataLoaded()) {
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
        List<String> notFoundNames = dataManagementServiceFacade.importInventoryFromCsv(csvFile);
        if (notFoundNames != null && !notFoundNames.isEmpty()) {
            displayNotFoundAlert(notFoundNames);
        }
    }

    private void loadPlannerDataFromCsv(File csvFile) {
        List<String> notFoundNames = dataManagementServiceFacade.importPlannerServantsFromCsv(csvFile, plannerType);
        if (notFoundNames != null && !notFoundNames.isEmpty()) {
            displayNotFoundAlert(notFoundNames);
        }
    }

    private void displayNotFoundAlert(List<String> notFoundNames) {
        StringBuilder stringBuilder = new StringBuilder();
        Alert notFoundAlert = new Alert(Alert.AlertType.WARNING);
        notFoundNames.forEach(str -> {
            stringBuilder.append(str);
            stringBuilder.append('\n');
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

    private ObservableList<PlannerServantView> createLTPlannerServantList() {
        return new PlannerServantViewFactory().createForLTPlanner(dataManagementServiceFacade.getUserServantList());
    }

    public int getNewValueIfValid(TableColumn.CellEditEvent<?, Integer> event, int min, int max) {
        int result = event.getOldValue();
        int input = event.getNewValue();
        if (input <= max && input >= min) {
            result = input;
        }
        return result;
    }
}
