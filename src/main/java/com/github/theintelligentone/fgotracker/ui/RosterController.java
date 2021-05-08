package com.github.theintelligentone.fgotracker.ui;

import com.github.theintelligentone.fgotracker.app.MainApp;
import com.github.theintelligentone.fgotracker.domain.servant.ServantOfUser;
import com.github.theintelligentone.fgotracker.domain.servant.UserServantFactory;
import com.github.theintelligentone.fgotracker.service.DataManagementService;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

public class RosterController {
    private DataManagementService dataManagementService;

    @FXML
    private TableView<ServantOfUser> rosterTable;

    @FXML
    private TableColumn<ServantOfUser, String> nameColumn;

    public void initialize() {
        dataManagementService = MainApp.getDataManagementService();
        rosterTable.setItems(getUserServants());
        nameColumn.setOnEditCommit(event -> {
            getUserServants().set(event.getTablePosition().getRow(), new UserServantFactory().replaceBaseServant(event.getRowValue(), dataManagementService.getServantByName(event.getNewValue())));
        });
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
