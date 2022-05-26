package com.github.theintelligentone.fgotracker.ui.controller;

import com.github.theintelligentone.fgotracker.domain.item.Inventory;
import com.github.theintelligentone.fgotracker.domain.servant.PlannerServant;
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
    private TableView<PlannerServant> plannerTable;

    @FXML
    private TableColumn<PlannerServant, String> nameColumn;

    @FXML
    private TableColumn<PlannerServant, ?> current;

    @FXML
    private TableColumn<PlannerServant, ?> desired;

    @FXML
    private TableColumn<PlannerServant, Integer> level;

    @FXML
    private TableColumn<PlannerServant, Integer> skill1;

    @FXML
    private TableColumn<PlannerServant, Integer> skill2;

    @FXML
    private TableColumn<PlannerServant, Integer> skill3;
}
