package com.github.theintelligentone.fgotracker.ui;

import com.github.theintelligentone.fgotracker.domain.servant.ServantOfUser;
import com.github.theintelligentone.fgotracker.domain.servant.UserServantFactory;
import com.github.theintelligentone.fgotracker.service.DataManagementService;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class MainWindow {
    private DataManagementService dataManagementService;

    @FXML
    private TableView<ServantOfUser> rosterTable;

    @FXML
    private TableColumn<ServantOfUser, String> nameColumn;

    public void initialize() {
        dataManagementService = new DataManagementService();
        rosterTable.setItems(getUserServants());
        dataManagementService.saveUserServant(dataManagementService.tempLoad(), 3);
        nameColumn.setOnEditCommit(event -> {
            getUserServants().set(event.getTablePosition().getRow(), new UserServantFactory().replaceBaseServant(event.getRowValue(), dataManagementService.getServantByName(event.getNewValue())));
        });
    }

    public void tearDown() {
        dataManagementService.saveUserState();
    }

    public ObservableList<ServantOfUser> getUserServants() {
        return dataManagementService.getUserServantList();
    }
}
