package com.github.theintelligentone.fgotracker.ui.controller;

import com.github.theintelligentone.fgotracker.domain.item.Inventory;
import com.github.theintelligentone.fgotracker.domain.servant.UserServant;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@FxmlView("/fxml/plannerTabComponent.fxml")
@Getter
public class PlannerComponentController {
    @FXML
    private TableView<Inventory> sumTable;

    @FXML
    private TableColumn<Inventory, String> label;

    @FXML
    private TableColumn<Inventory, String> sumCurrent;

    @FXML
    private TableColumn<Inventory, String> sumDesired;

    @FXML
    private TableView<UserServant> plannerTable;

    @FXML
    private TableColumn<UserServant, String> nameColumn;

    @FXML
    private TableColumn<UserServant, ?> current;

    @FXML
    private TableColumn<UserServant, ?> desired;

    @FXML
    private TableColumn<UserServant, Integer> level;

    @FXML
    private TableColumn<UserServant, Integer> skill1;

    @FXML
    private TableColumn<UserServant, Integer> skill2;

    @FXML
    private TableColumn<UserServant, Integer> skill3;
}
