package com.github.theintelligentone.fgotracker.ui.controller;

import com.github.theintelligentone.fgotracker.domain.servant.UserServant;
import com.github.theintelligentone.fgotracker.service.ServantUtils;
import com.github.theintelligentone.fgotracker.service.datamanagement.DataManagementServiceFacade;
import com.github.theintelligentone.fgotracker.ui.cellfactory.AscensionCheckBoxTableCell;
import com.github.theintelligentone.fgotracker.ui.cellfactory.AutoCompleteTextFieldTableCell;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.beans.Statement;
import java.io.File;
import java.util.List;
import java.util.stream.IntStream;

@Component
@FxmlView("/fxml/rosterTab.fxml")
@Slf4j
public class RosterController {
    private static final Integer[] ONE_TO_FIVE = {1, 2, 3, 4, 5};

    @Autowired
    private DataManagementServiceFacade dataManagementServiceFacade;

    @FXML
    private TableView<UserServant> rosterTable;

    @FXML
    private TableColumn<UserServant, Integer> rarityColumn;

    @FXML
    private TableColumn<UserServant, String> classColumn;

    @FXML
    private TableColumn<UserServant, String> attributeColumn;

    @FXML
    private TableColumn<UserServant, ?> deckColumn;

    @FXML
    private TableColumn<UserServant, ?> npColumn;

    @FXML
    private TableColumn<UserServant, String> nameColumn;

    @FXML
    private TableColumn<UserServant, String> npTypeColumn;

    @FXML
    private TableColumn<UserServant, String> npTargetColumn;

    @FXML
    private TableColumn<UserServant, Integer> npDamageColumn;

    @FXML
    private TableColumn<UserServant, Integer> levelColumn;

    @FXML
    private TableColumn<UserServant, Integer> atkColumn;

    @FXML
    private TableColumn<UserServant, Integer> hpColumn;

    @FXML
    private TableColumn<UserServant, Integer> npLvlColumn;

    @FXML
    private TableColumn<UserServant, Boolean> ascColumn;

    @FXML
    private TableColumn<UserServant, Integer> bondColumn;

    @FXML
    private TableColumn<UserServant, Integer> skill1Column;

    @FXML
    private TableColumn<UserServant, Integer> skill2Column;

    @FXML
    private TableColumn<UserServant, Integer> skill3Column;

    @FXML
    private TableColumn<UserServant, String> notesColumn;

    public void initialize() {
        tableSetup();
    }

    public void tableSetup() {
        rosterTable.getSelectionModel().setCellSelectionEnabled(true);
        rosterTable.setFixedCellSize(MainController.CELL_HEIGHT);
        rosterTable.setRowFactory(tableView -> {
            PseudoClass lastRow = PseudoClass.getPseudoClass("last-row");
            TableRow<UserServant> row = new TableRow<>() {
                @Override
                public void updateIndex(int index) {
                    super.updateIndex(index);
                    pseudoClassStateChanged(lastRow, index >= 0 && index == rosterTable.getItems().size() - 1);
                }
            };
            createContextMenuForTableRow(row);
            return row;
        });
        columnSetup();
    }

    private void createContextMenuForTableRow(TableRow<UserServant> row) {
        MenuItem importButton = new MenuItem("Import roster from csv");
        importButton.setOnAction(event -> importUserServantsFromCsv());
        MenuItem removeRowButton = new MenuItem("Delete row");
        removeRowButton.setOnAction(event -> dataManagementServiceFacade.removeUserServant(row.getIndex()));
        MenuItem clearRowButton = new MenuItem("Clear row");
        clearRowButton.setOnAction(event -> dataManagementServiceFacade.eraseUserServant(row.getIndex()));
        MenuItem addRowAboveButton = new MenuItem("Insert row above");
        addRowAboveButton.setOnAction(event -> dataManagementServiceFacade.saveUserServant(row.getTableView()
                .getItems()
                .indexOf(row.getItem()), new UserServant()));
        MenuItem addRowBelowButton = new MenuItem("Insert row below");
        addRowBelowButton.setOnAction(event -> dataManagementServiceFacade.saveUserServant(row.getTableView()
                .getItems()
                .indexOf(row.getItem()) + 1, new UserServant()));
        MenuItem addMultipleRowsButton = new MenuItem("Add X new rows");
        addMultipleRowsButton.setOnAction(event -> {
            TextInputDialog prompt = new TextInputDialog("10");
            prompt.setContentText("How many new rows to add?");
            prompt.setTitle("Add X new rows");
            prompt.setHeaderText("");
            prompt.showAndWait()
                    .ifPresent(s -> IntStream.range(0, Integer.parseInt(s))
                            .forEach(i -> dataManagementServiceFacade.saveUserServant(new UserServant())));
        });
        ContextMenu menu = new ContextMenu(importButton, addRowAboveButton, addRowBelowButton, addMultipleRowsButton, clearRowButton, removeRowButton);
        row.contextMenuProperty().bind(Bindings.when(row.emptyProperty()).then((ContextMenu) null).otherwise(menu));
    }

    private void importUserServantsFromCsv() {
        if (dataManagementServiceFacade.isDataLoaded()) {
            displayFileChooserForUserCsvImport();
        } else {
            showNotLoadedYetAlert();
        }
    }

    private void displayFileChooserForUserCsvImport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("CSV to import");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File csvFile = fileChooser.showOpenDialog(Stage.getWindows().get(0));
        if (csvFile != null) {
            loadRosterDataFromCsv(csvFile);
        }
    }

    private void showNotLoadedYetAlert() {
        Alert loadingAlert = new Alert(Alert.AlertType.WARNING);
        loadingAlert.setContentText("Servant data still loading.");
        loadingAlert.show();
    }

    private void loadRosterDataFromCsv(File csvFile) {
        List<String> notFoundNames = dataManagementServiceFacade.importUserServantsFromCsv(csvFile);
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
        notFoundAlert.setHeaderText("Servants that couldn't be imported:");
        notFoundAlert.setContentText(stringBuilder.toString());
        notFoundAlert.show();
    }

    private void columnSetup() {
        rarityColumn.setPrefWidth(MainController.CHAR_CELL_WIDTH);
        npColumnSetup();
        deckColumn.getColumns().forEach(column -> column.setPrefWidth(MainController.CHAR_CELL_WIDTH));
        classColumnSetup();
        attributeColumnSetup();
        nameColumnSetup();
        levelColumnSetup();
        atkColumnSetup();
        hpColumnSetup();
        skill1ColumnSetup();
        skill2ColumnSetup();
        skill3ColumnSetup();
        bondColumnSetup();
        ascColumnSetup();
        notesColumn.setStyle("-fx-alignment: center-left");
        notesColumn.setPrefWidth(MainController.LONG_CELL_WIDTH * 3);
    }

    private void npColumnSetup() {
        npColumn.getColumns().forEach(column -> column.setPrefWidth(MainController.MID_CELL_WIDTH));
        npTypeColumn.setCellValueFactory(param -> {
            SimpleStringProperty result = new SimpleStringProperty();
            if (param.getValue().getSvtId() != 0) {
                result.set(ServantUtils.determineNpCard(param.getValue().getBaseServant()));
            }
            return result;
        });
        npTargetColumn.setCellValueFactory(param -> {
            SimpleStringProperty result = new SimpleStringProperty();
            if (param.getValue().getSvtId() != 0) {
                result.set(ServantUtils.determineNpTarget(param.getValue().getBaseServant()));
            }
            return result;
        });
        npDamageColumn.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item <= 0) {
                    setText(null);
                } else {
                    setText(item.toString());
                }
            }
        });
        npLvlColumnSetup();
    }

    private void attributeColumnSetup() {
        attributeColumn.setPrefWidth(MainController.MID_CELL_WIDTH);
        attributeColumn.setCellValueFactory(param -> {
            SimpleStringProperty name = new SimpleStringProperty();
            if (param.getValue().getSvtId() != 0) {
                String attribute = param.getValue().getBaseServant().getAttribute();
                name.set(attribute.substring(0, 1).toUpperCase() + attribute.substring(1));
            }
            return name;
        });
    }

    private void classColumnSetup() {
        classColumn.setPrefWidth(MainController.LONG_CELL_WIDTH);
        classColumn.setCellValueFactory(param -> {
            SimpleStringProperty name = new SimpleStringProperty();
            if (param.getValue().getSvtId() != 0) {
                String className = param.getValue().getBaseServant().getClassName();
                name.set(className.substring(0, 1).toUpperCase() + className.substring(1));
            }
            return name;
        });
    }

    private void ascColumnSetup() {
        ascColumn.setPrefWidth(MainController.SHORT_CELL_WIDTH);
        ascColumn.setCellFactory(param -> new AscensionCheckBoxTableCell());
    }

    private void skill1ColumnSetup() {
        skill1Column.setPrefWidth(MainController.SHORT_CELL_WIDTH);
        skill1Column.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        skill1Column.setOnEditCommit(propertyCommitWithLimits(1, 10, "setSkillLevel1"));
    }

    private void skill2ColumnSetup() {
        skill2Column.setPrefWidth(MainController.SHORT_CELL_WIDTH);
        skill2Column.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        skill2Column.setOnEditCommit(propertyCommitWithLimits(1, 10, "setSkillLevel2"));
    }

    private void skill3ColumnSetup() {
        skill3Column.setPrefWidth(MainController.SHORT_CELL_WIDTH);
        skill3Column.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        skill3Column.setOnEditCommit(propertyCommitWithLimits(1, 10, "setSkillLevel3"));
    }

    private void bondColumnSetup() {
        bondColumn.setPrefWidth(MainController.SHORT_CELL_WIDTH);
        bondColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        bondColumn.setOnEditCommit(propertyCommitWithLimits(0, 15, "setBondLevel"));
    }

    private void levelColumnSetup() {
        levelColumn.setPrefWidth(MainController.SHORT_CELL_WIDTH);
        levelColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        levelColumn.setOnEditCommit(propertyCommitWithLimits(1, 120, "setLevel"));
    }

    private void atkColumnSetup() {
        atkColumn.setPrefWidth(MainController.MID_CELL_WIDTH);
        atkColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        atkColumn.setOnEditCommit(propertyCommitWithLimits(0, 2000, "setFouAtk"));
    }

    private void hpColumnSetup() {
        hpColumn.setPrefWidth(MainController.MID_CELL_WIDTH);
        hpColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        hpColumn.setOnEditCommit(propertyCommitWithLimits(0, 2000, "setFouHp"));
    }

    private void npLvlColumnSetup() {
        npLvlColumn.setCellFactory(list -> {
            ComboBoxTableCell<UserServant, Integer> tableCell = new ComboBoxTableCell<>(FXCollections.observableArrayList(ONE_TO_FIVE));
            tableCell.setComboBoxEditable(true);
            return tableCell;
        });
        npLvlColumn.setOnEditCommit(event -> {
            if (event.getRowValue().getSvtId() != 0) {
                event.getRowValue()
                        .setNpLevel(ServantUtils.getDefaultValueIfInvalid(event.getNewValue(), 1, 5, event.getOldValue()));

                rosterTable.refresh();
            }
        });
    }

    private void nameColumnSetup() {
        nameColumn.setPrefWidth(MainController.NAME_CELL_WIDTH);
        nameColumn.setOnEditCommit(event -> {
            if (event.getNewValue().isEmpty()) {
                dataManagementServiceFacade.eraseUserServant(event.getTablePosition().getRow());
            } else {
                dataManagementServiceFacade.replaceBaseServantInRow(event.getTablePosition()
                        .getRow(), event.getRowValue(), event.getNewValue());
                event.getTableView().refresh();
            }
        });
    }

    public void setup() {
        rosterTable.setItems(dataManagementServiceFacade.getUserServantList());
        if (rosterTable.getItems().size() == 0) {
            IntStream.range(0, 10).forEach(i -> dataManagementServiceFacade.saveUserServant(new UserServant()));
        }
        nameColumn.setCellFactory(AutoCompleteTextFieldTableCell.forTableColumn(dataManagementServiceFacade.getServantNameList()));
    }

    private EventHandler<TableColumn.CellEditEvent<UserServant, Integer>> propertyCommitWithLimits(int min, int max, String propertyName) {
        return event -> {
            if (event.getRowValue().getSvtId() != 0) {
                try {
                    new Statement(event.getRowValue(), propertyName, new Object[]{ServantUtils.getDefaultValueIfInvalid(event.getNewValue(), min, max, event.getOldValue())}).execute();
                } catch (Exception e) {
                    log.error("Set property error: ", e);
                }
                rosterTable.refresh();
            }
        };
    }
}
