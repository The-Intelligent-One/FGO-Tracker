package com.github.theintelligentone.fgotracker.ui.controller;

import com.github.theintelligentone.fgotracker.app.MainApp;
import com.github.theintelligentone.fgotracker.domain.servant.UserServant;
import com.github.theintelligentone.fgotracker.service.DataManagementService;
import com.github.theintelligentone.fgotracker.ui.cellfactory.AscensionCheckBoxTableCell;
import com.github.theintelligentone.fgotracker.ui.cellfactory.AutoCompleteTextFieldTableCell;
import javafx.beans.property.SimpleBooleanProperty;
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
    private TableColumn<UserServant, Integer> levelColumn;

    @FXML
    private TableColumn<UserServant, Integer> atkColumn;

    @FXML
    private TableColumn<UserServant, Integer> hpColumn;

    // this need to be set to String for some reason. If I set it to Integer it freaks out in the edit commit event handler about casting during runtime
    @FXML
    private TableColumn<UserServant, String> npLvlColumn;

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

    public void initialize() {
        dataManagementService = MainApp.getDataManagementService();
        tableSetup();
    }

    public void tableSetup() {
        rosterTable.getSelectionModel().setCellSelectionEnabled(true);
        columnSetup();
        PseudoClass lastRow = PseudoClass.getPseudoClass("last-row");
        rosterTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            public void updateIndex(int index) {
                super.updateIndex(index);
                pseudoClassStateChanged(lastRow,
                        index >= 0 && index == rosterTable.getItems().size() - 1);
            }
        });
    }

    private void columnSetup() {
        rarityColumn.setPrefWidth(MainController.CHAR_CELL_WIDTH);
        deckColumn.getColumns().forEach(column -> column.setPrefWidth(MainController.CHAR_CELL_WIDTH));
        npColumn.getColumns().forEach(column -> column.setPrefWidth(MainController.MID_CELL_WIDTH));
        classColumnSetup();
        attributeColumnSetup();
        nameColumnSetup();
        npLvlColumnSetup();
        levelColumnSetup();
        atkColumnSetup();
        hpColumnSetup();
        skill1ColumnSetup();
        skill2ColumnSetup();
        skill3ColumnSetup();
        bondColumnSetup();
        ascColumnSetup();
    }

    private void attributeColumnSetup() {
        attributeColumn.setPrefWidth(MainController.MID_CELL_WIDTH);
        attributeColumn.setCellValueFactory(param -> {
            SimpleStringProperty name = new SimpleStringProperty();
            if (param.getValue().getBaseServant() != null) {
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
            if (param.getValue().getBaseServant() != null) {
                String className = param.getValue().getBaseServant().getClassName();
                name.set(className.substring(0, 1).toUpperCase() + className.substring(1));
            }
            return name;
        });
    }

    private void ascColumnSetup() {
        ascColumn.setPrefWidth(MainController.SHORT_CELL_WIDTH);
        ascColumn.setCellFactory(cell -> new AscensionCheckBoxTableCell());
        ascColumn.setCellValueFactory(cellData -> {
            UserServant servant = cellData.getValue();
            SimpleBooleanProperty simpleBooleanProperty = new SimpleBooleanProperty(servant != null && servant.isAscension());
            simpleBooleanProperty.addListener((observable, oldValue, newValue) -> cellData.getValue().setAscension(newValue));
            return simpleBooleanProperty;
        });
    }

    private void skill1ColumnSetup() {
        skill1Column.setPrefWidth(MainController.CHAR_CELL_WIDTH);
        skill1Column.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        skill1Column.setOnEditCommit(event -> {
            int input = event.getNewValue();
            if (input <= 10 && input >= 1) {
                event.getRowValue().setSkillLevel1(input);
            }
            rosterTable.refresh();
        });
    }

    private void skill2ColumnSetup() {
        skill2Column.setPrefWidth(MainController.CHAR_CELL_WIDTH);
        skill2Column.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        skill2Column.setOnEditCommit(event -> {
            int input = event.getNewValue();
            if (input <= 10 && input >= 1) {
                event.getRowValue().setSkillLevel2(input);
            }
            rosterTable.refresh();
        });
    }

    private void skill3ColumnSetup() {
        skill3Column.setPrefWidth(MainController.CHAR_CELL_WIDTH);
        skill3Column.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        skill3Column.setOnEditCommit(event -> {
            int input = event.getNewValue();
            if (input <= 10 && input >= 1) {
                event.getRowValue().setSkillLevel3(input);
            }
            rosterTable.refresh();
        });
    }

    private void bondColumnSetup() {
        bondColumn.setPrefWidth(MainController.CHAR_CELL_WIDTH);
        bondColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        bondColumn.setOnEditCommit(event -> {
            int input = event.getNewValue();
            if (input <= 15 && input >= 0) {
                event.getRowValue().setBondLevel(input);
            }
            rosterTable.refresh();
        });
    }

    private void levelColumnSetup() {
        levelColumn.setPrefWidth(MainController.SHORT_CELL_WIDTH);
        levelColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        levelColumn.setOnEditCommit(event -> {
            int input = event.getNewValue();
            if (input <= 100 && input >= 1) {
                event.getRowValue().setLevel(input);
            }
            rosterTable.refresh();
        });
    }

    private void atkColumnSetup() {
        atkColumn.setPrefWidth(MainController.MID_CELL_WIDTH);
        atkColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        atkColumn.setOnEditCommit(event -> {
            int input = event.getNewValue();
            if (input <= 2000 && input >= 0) {
                event.getRowValue().setFouAtk(input);
            }
            rosterTable.refresh();
        });
    }

    private void hpColumnSetup() {
        hpColumn.setPrefWidth(MainController.MID_CELL_WIDTH);
        hpColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        hpColumn.setOnEditCommit(event -> {
            int input = event.getNewValue();
            if (input <= 2000 && input >= 0) {
                event.getRowValue().setFouHp(input);
            }
            rosterTable.refresh();
        });
    }

    private void npLvlColumnSetup() {
        npLvlColumn.setCellFactory(list -> {
            ComboBoxTableCell<UserServant, String> tableCell = new ComboBoxTableCell<>(FXCollections.observableArrayList(ONE_TO_FIVE));
            tableCell.setComboBoxEditable(true);
            return tableCell;
        });
        npLvlColumn.setOnEditCommit(event -> {
            int input = Integer.parseInt(event.getNewValue());
            if (input <= 5 && input >= 1) {
                event.getRowValue().setNpLevel(input);
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
            }
        });
        nameColumn.setCellFactory(AutoCompleteTextFieldTableCell.forTableColumn(dataManagementService.getServantNameList()));
        nameColumn.setCellValueFactory(param -> {
            SimpleStringProperty name = new SimpleStringProperty();
            if (param.getValue().getBaseServant() != null) {
                name.set(param.getValue().getBaseServant().getName());
            }
            return name;
        });
    }

    public void setup() {
        rosterTable.setItems(dataManagementService.getUserServantList());
        if (rosterTable.getItems().size() == 0) {
            IntStream.range(0, 10).forEach(i -> dataManagementService.saveUserServant(new UserServant()));
        }
    }
}
