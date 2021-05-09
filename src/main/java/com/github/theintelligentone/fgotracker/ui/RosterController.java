package com.github.theintelligentone.fgotracker.ui;

import com.github.theintelligentone.fgotracker.app.MainApp;
import com.github.theintelligentone.fgotracker.domain.AutoCompleteTextFieldTableCell;
import com.github.theintelligentone.fgotracker.domain.servant.ServantOfUser;
import com.github.theintelligentone.fgotracker.domain.servant.UserServantFactory;
import com.github.theintelligentone.fgotracker.service.DataManagementService;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;

public class RosterController {
    private DataManagementService dataManagementService;

    @FXML
    private TableView<ServantOfUser> rosterTable;

    @FXML
    private TableColumn<ServantOfUser, String> nameColumn;

    @FXML
    private TableColumn<ServantOfUser, String> levelColumn;

    @FXML
    private TableColumn<ServantOfUser, String> npColumn;

    public void initialize() {
        dataManagementService = MainApp.getDataManagementService();
        rosterTable.setItems(getUserServants());
        nameColumn.setOnEditCommit(event -> {
            if (event.getNewValue().isEmpty()) {
                event.getTableView().getItems().set(event.getTableView().getItems().indexOf(event.getRowValue()), null);
            } else {
                getUserServants().set(event.getTablePosition().getRow(), new UserServantFactory().replaceBaseServant(event.getRowValue(), dataManagementService.getServantByName(event.getNewValue())));
            }
        });
        nameColumn.setCellFactory(AutoCompleteTextFieldTableCell.forTableColumn(dataManagementService.getServantNameList()));
        npColumn.setCellFactory(ComboBoxTableCell.<ServantOfUser, String>forTableColumn("1", "2", "3", "4", "5"));
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

    public ObservableList<ServantOfUser> getUserServants() {
        return dataManagementService.getUserServantList();
    }
}
