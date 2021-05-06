package com.github.theintelligentone.fgotracker.ui;

import com.github.theintelligentone.fgotracker.domain.servant.ServantOfUser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import lombok.Getter;

public class MainWindow {
    @FXML
    private TableView<ServantOfUser> rosterTable;

    @Getter
    private ObservableList<ServantOfUser> userServants = FXCollections.observableArrayList();

    public void initialize() {
        rosterTable.setItems(userServants);
    }
}
