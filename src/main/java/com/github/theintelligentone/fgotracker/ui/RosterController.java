package com.github.theintelligentone.fgotracker.ui;

import com.github.theintelligentone.fgotracker.app.MainApp;
import com.github.theintelligentone.fgotracker.domain.AutoCompleteTextFieldTableCell;
import com.github.theintelligentone.fgotracker.domain.servant.ServantOfUser;
import com.github.theintelligentone.fgotracker.domain.servant.UserServantFactory;
import com.github.theintelligentone.fgotracker.service.DataManagementService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;

public class RosterController {
    private static final String[] ONE_TO_FIVE = {"1", "2", "3", "4", "5"};
    private static final String[] ZERO_TO_FOUR = {"0", "1", "2", "3", "4"};
    private DataManagementService dataManagementService;

    @FXML
    private TableView<ServantOfUser> rosterTable;

    @FXML
    private TableColumn<ServantOfUser, String> nameColumn;

    @FXML
    private TableColumn<ServantOfUser, Integer> levelColumn;

    @FXML
    private TableColumn<ServantOfUser, Integer> atkColumn;

    @FXML
    private TableColumn<ServantOfUser, Integer> hpColumn;

    // these need to be set to String for some reason. If I set it to Integer it freaks out in the edit commit event handler about casting during runtime
    @FXML
    private TableColumn<ServantOfUser, String> npColumn;

    @FXML
    private TableColumn<ServantOfUser, String> ascColumn;

    @FXML
    private TableColumn<ServantOfUser, Integer> bondColumn;

    @FXML
    private TableColumn<ServantOfUser, Integer> skill1Column;

    @FXML
    private TableColumn<ServantOfUser, Integer> skill2Column;

    @FXML
    private TableColumn<ServantOfUser, Integer> skill3Column;

    public void initialize() {
        dataManagementService = MainApp.getDataManagementService();
        tableSetup();

    }

    private void tableSetup() {
        rosterTable.setItems(getUserServants());
        rosterTable.getSelectionModel().setCellSelectionEnabled(true);
        rosterTable.getColumns().forEach(column -> column.setResizable(false));
        nameColumnSetup();
        npColumnSetup();
        levelColumnSetup();
        atkColumnSetup();
        hpColumnSetup();
        skill1ColumnSetup();
        skill2ColumnSetup();
        skill3ColumnSetup();
        bondColumnSetup();
        ascColumnSetup();
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

    private void ascColumnSetup() {
        ascColumn.setCellFactory(list -> {
            ComboBoxTableCell<ServantOfUser, String> tableCell = new ComboBoxTableCell<>(FXCollections.observableArrayList(ZERO_TO_FOUR));
            tableCell.setComboBoxEditable(true);
            return tableCell;
        });
        ascColumn.setOnEditCommit(event -> {
            int input = Integer.parseInt(event.getNewValue());
            if (input <= 4 && input >= 0) {
                event.getRowValue().setAscension(input);
            }
            rosterTable.refresh();
        });
    }

    private void skill1ColumnSetup() {
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
        skill1Column.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        skill1Column.setOnEditCommit(event -> {
            int input = event.getNewValue();
            if (input <= 15 && input >= 0) {
                event.getRowValue().setSkillLevel1(input);
            }
            rosterTable.refresh();
        });
    }

    private void levelColumnSetup() {
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
        hpColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        hpColumn.setOnEditCommit(event -> {
            int input = event.getNewValue();
            if (input <= 2000 && input >= 0) {
                event.getRowValue().setFouHp(input);
            }
            rosterTable.refresh();
        });
    }

    private void npColumnSetup() {
        npColumn.setCellFactory(list -> {
            ComboBoxTableCell<ServantOfUser, String> tableCell = new ComboBoxTableCell<>(FXCollections.observableArrayList(ONE_TO_FIVE));
            tableCell.setComboBoxEditable(true);
            return tableCell;
        });
        npColumn.setOnEditCommit(event -> {
            int input = Integer.parseInt(event.getNewValue());
            if (input <= 5 && input >= 1) {
                event.getRowValue().setNpLevel(input);
            }
            rosterTable.refresh();
        });
    }

    private void nameColumnSetup() {
        nameColumn.setOnEditCommit(event -> {
            if (event.getNewValue().isEmpty()) {
                getUserServants().set(getUserServants().indexOf(event.getRowValue()), null);
            } else {
                getUserServants().set(event.getTablePosition().getRow(), new UserServantFactory().replaceBaseServant(event.getRowValue(), dataManagementService.getServantByName(event.getNewValue())));
            }
        });
        nameColumn.setCellFactory(AutoCompleteTextFieldTableCell.forTableColumn(dataManagementService.getServantNameList()));
    }

    public ObservableList<ServantOfUser> getUserServants() {
        return dataManagementService.getUserServantList();
    }
}
