package com.github.theintelligentone.fgotracker.ui;

import com.github.theintelligentone.fgotracker.domain.servant.ServantOfUser;
import com.github.theintelligentone.fgotracker.service.DataManagementService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import lombok.Getter;

public class MainWindow {
    private DataManagementService dataManagementService;

    @FXML
    private TableView<ServantOfUser> rosterTable;

    @FXML
    private TableColumn<ServantOfUser, String> nameColumn;

    private ObservableList<ServantOfUser> userServants;

    public void initialize() {
        dataManagementService = new DataManagementService();
        rosterTable.setItems(getUserServants());
        dataManagementService.saveUserServant(dataManagementService.tempLoad(), 3);
        nameColumn.setOnEditCommit(event -> {
            event.getRowValue();
        });
    }

    public void tearDown() {
        dataManagementService.saveUserState();
    }

    public ObservableList<ServantOfUser> getUserServants() {
        return dataManagementService.getUserServantList();
    }
}
