package com.github.theintelligentone.fgotracker.ui.controller;

import com.github.theintelligentone.fgotracker.app.MainApp;
import com.github.theintelligentone.fgotracker.domain.view.UserServantView;
import com.github.theintelligentone.fgotracker.service.DataManagementService;
import com.github.theintelligentone.fgotracker.service.ServantUtils;
import com.github.theintelligentone.fgotracker.ui.cellfactory.AscensionCheckBoxTableCell;
import com.github.theintelligentone.fgotracker.ui.cellfactory.AutoCompleteTextFieldTableCell;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;

import java.util.stream.IntStream;

public class RosterController {
    private static final String[] ONE_TO_FIVE = {"1", "2", "3", "4", "5"};
    private DataManagementService dataManagementService;
    private ServantUtils servantUtils;

    @FXML
    private TableView<UserServantView> rosterTable;

    @FXML
    private TableColumn<UserServantView, Integer> rarityColumn;

    @FXML
    private TableColumn<UserServantView, String> classColumn;

    @FXML
    private TableColumn<UserServantView, String> attributeColumn;

    @FXML
    private TableColumn<UserServantView, ?> deckColumn;

    @FXML
    private TableColumn<UserServantView, ?> npColumn;

    @FXML
    private TableColumn<UserServantView, String> nameColumn;

    @FXML
    private TableColumn<UserServantView, String> npTypeColumn;

    @FXML
    private TableColumn<UserServantView, String> npTargetColumn;

    @FXML
    private TableColumn<UserServantView, Integer> npDamageColumn;

    @FXML
    private TableColumn<UserServantView, Integer> levelColumn;

    @FXML
    private TableColumn<UserServantView, Integer> atkColumn;

    @FXML
    private TableColumn<UserServantView, Integer> hpColumn;

    // this need to be set to String for some reason. If I set it to Integer it freaks out in the edit commit event handler about casting during runtime
    @FXML
    private TableColumn<UserServantView, String> npLvlColumn;

    @FXML
    private TableColumn<UserServantView, Boolean> ascColumn;

    @FXML
    private TableColumn<UserServantView, Integer> bondColumn;

    @FXML
    private TableColumn<UserServantView, Integer> skill1Column;

    @FXML
    private TableColumn<UserServantView, Integer> skill2Column;

    @FXML
    private TableColumn<UserServantView, Integer> skill3Column;

    public void initialize() {
        dataManagementService = MainApp.getDataManagementService();
        servantUtils = new ServantUtils();
        tableSetup();
    }

    public void tableSetup() {
        rosterTable.getSelectionModel().setCellSelectionEnabled(true);
        rosterTable.setFixedCellSize(MainController.CELL_HEIGHT);
        PseudoClass lastRow = PseudoClass.getPseudoClass("last-row");
        rosterTable.setRowFactory(tableView -> {
            TableRow<UserServantView> row = new TableRow<>() {
                @Override
                public void updateIndex(int index) {
                    super.updateIndex(index);
                    pseudoClassStateChanged(lastRow,
                            index >= 0 && index == rosterTable.getItems().size() - 1);
                }
            };
            createContextMenuForTableRow(row);
            return row;
        });
        columnSetup();
    }

    private void createContextMenuForTableRow(TableRow<UserServantView> row) {
        MenuItem removeRowButton = new MenuItem("Delete row");
        removeRowButton.setOnAction(event -> dataManagementService.removeUserServant(row.getItem()));
        MenuItem clearRowButton = new MenuItem("Clear row");
        clearRowButton.setOnAction(event -> dataManagementService.eraseUserServant(row.getItem()));
        MenuItem addRowButton = new MenuItem("Insert new row");
        addRowButton.setOnAction(event -> dataManagementService.saveUserServant(row.getTableView().getItems().indexOf(row.getItem()), new UserServantView()));
        MenuItem addMultipleRowsButton = new MenuItem("Add X new rows");
        addMultipleRowsButton.setOnAction(event -> {
            TextInputDialog prompt = new TextInputDialog("10");
            prompt.setContentText("How many new rows to add?");
            prompt.setTitle("Add X new rows");
            prompt.setHeaderText("");
            prompt.showAndWait().ifPresent(s -> {
                IntStream.range(0, Integer.parseInt(s)).forEach(i -> dataManagementService.saveUserServant(new UserServantView()));
            });
        });
        ContextMenu menu = new ContextMenu(addRowButton, addMultipleRowsButton, clearRowButton, removeRowButton);
        row.contextMenuProperty().bind(Bindings.when(row.emptyProperty()).then((ContextMenu) null).otherwise(menu));
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
    }

    private void npColumnSetup() {
        npColumn.getColumns().forEach(column -> column.setPrefWidth(MainController.MID_CELL_WIDTH));
        npTypeColumn.setCellValueFactory(param -> {
            SimpleStringProperty result = new SimpleStringProperty();
            if (param.getValue().getBaseServant().getValue() != null) {
                result.set(servantUtils.determineNpCard(param.getValue().getBaseServant().getValue()));
            }
            return result;
        });
        npTargetColumn.setCellValueFactory(param -> {
            SimpleStringProperty result = new SimpleStringProperty();
            if (param.getValue().getBaseServant().getValue() != null) {
                result.set(servantUtils.determineNpTarget(param.getValue().getBaseServant().getValue()));
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
            if (param.getValue().getBaseServant().getValue() != null) {
                String attribute = param.getValue().getBaseServant().getValue().getAttribute();
                name.set(attribute.substring(0, 1).toUpperCase() + attribute.substring(1));
            }
            return name;
        });
    }

    private void classColumnSetup() {
        classColumn.setPrefWidth(MainController.LONG_CELL_WIDTH);
        classColumn.setCellValueFactory(param -> {
            SimpleStringProperty name = new SimpleStringProperty();
            if (param.getValue().getBaseServant() != null && param.getValue().getBaseServant().getValue() != null) {
                String className = param.getValue().getBaseServant().getValue().getClassName();
                name.set(className.substring(0, 1).toUpperCase() + className.substring(1));
            }
            return name;
        });
    }

    private void ascColumnSetup() {
        ascColumn.setPrefWidth(MainController.SHORT_CELL_WIDTH);
        ascColumn.setCellFactory(cell -> new AscensionCheckBoxTableCell());
    }

    private void skill1ColumnSetup() {
        skill1Column.setPrefWidth(MainController.SHORT_CELL_WIDTH);
        skill1Column.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        skill1Column.setOnEditCommit(event -> {
            event.getRowValue().getSkillLevel1().set(servantUtils.getNewValueIfValid(event, 1, 10));
            rosterTable.refresh();
        });
    }

    private void skill2ColumnSetup() {
        skill2Column.setPrefWidth(MainController.SHORT_CELL_WIDTH);
        skill2Column.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        skill2Column.setOnEditCommit(event -> {
            event.getRowValue().getSkillLevel2().set(servantUtils.getNewValueIfValid(event, 1, 10));
            rosterTable.refresh();
        });
    }

    private void skill3ColumnSetup() {
        skill3Column.setPrefWidth(MainController.SHORT_CELL_WIDTH);
        skill3Column.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        skill3Column.setOnEditCommit(event -> {
            event.getRowValue().getSkillLevel3().set(servantUtils.getNewValueIfValid(event, 1, 10));
            rosterTable.refresh();
        });
    }

    private void bondColumnSetup() {
        bondColumn.setPrefWidth(MainController.SHORT_CELL_WIDTH);
        bondColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        bondColumn.setOnEditCommit(event -> {
            event.getRowValue().getBondLevel().set(servantUtils.getNewValueIfValid(event, 0, 15));
            rosterTable.refresh();
        });
    }

    private void levelColumnSetup() {
        levelColumn.setPrefWidth(MainController.SHORT_CELL_WIDTH);
        levelColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        levelColumn.setOnEditCommit(event -> {
            event.getRowValue().getLevel().set(servantUtils.getNewValueIfValid(event, 1, 100));
            rosterTable.refresh();
        });
    }

    private void atkColumnSetup() {
        atkColumn.setPrefWidth(MainController.MID_CELL_WIDTH);
        atkColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        atkColumn.setOnEditCommit(event -> {
            event.getRowValue().getFouAtk().set(servantUtils.getNewValueIfValid(event, 0, 2000));
            rosterTable.refresh();
        });
    }

    private void hpColumnSetup() {
        hpColumn.setPrefWidth(MainController.MID_CELL_WIDTH);
        hpColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        hpColumn.setOnEditCommit(event -> {
            event.getRowValue().getFouHp().set(servantUtils.getNewValueIfValid(event, 0, 2000));
            rosterTable.refresh();
        });
    }

    private void npLvlColumnSetup() {
        npLvlColumn.setCellFactory(list -> {
            ComboBoxTableCell<UserServantView, String> tableCell = new ComboBoxTableCell<>(FXCollections.observableArrayList(ONE_TO_FIVE));
            tableCell.setComboBoxEditable(true);
            return tableCell;
        });
        npLvlColumn.setOnEditCommit(event -> {
            int input = Integer.parseInt(event.getNewValue());
            if (input <= 5 && input >= 1) {
                event.getRowValue().getNpLevel().set(input);
            }
            rosterTable.refresh();
        });
    }

    private void nameColumnSetup() {
        nameColumn.setPrefWidth(MainController.NAME_CELL_WIDTH);
        nameColumn.setOnEditCommit(event -> {
            if (event.getNewValue().isEmpty()) {
                dataManagementService.eraseUserServant(event.getRowValue());
            } else {
                dataManagementService.replaceBaseServantInRow(event.getTablePosition().getRow(), event.getRowValue(), event.getNewValue());
                event.getTableView().refresh();
            }
        });
        nameColumn.setCellValueFactory(param -> {
            SimpleStringProperty name = new SimpleStringProperty();
            if (param.getValue().getBaseServant() != null && param.getValue().getBaseServant().getValue() != null) {
                name.set(param.getValue().getBaseServant().getValue().getName());
            }
            return name;
        });
    }

    public void setup() {
        rosterTable.setItems(dataManagementService.getUserServantList());
        if (rosterTable.getItems().size() == 0) {
            IntStream.range(0, 10).forEach(i -> dataManagementService.saveUserServant(new UserServantView()));
        }
        nameColumn.setCellFactory(AutoCompleteTextFieldTableCell.forTableColumn(dataManagementService.getServantNameList()));
    }
}
