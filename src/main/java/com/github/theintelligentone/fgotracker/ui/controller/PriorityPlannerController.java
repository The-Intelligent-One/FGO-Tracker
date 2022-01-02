package com.github.theintelligentone.fgotracker.ui.controller;

import com.github.theintelligentone.fgotracker.domain.item.Inventory;
import com.github.theintelligentone.fgotracker.domain.other.PlannerType;
import com.github.theintelligentone.fgotracker.domain.servant.UserServant;
import com.github.theintelligentone.fgotracker.service.datamanagement.DataManagementServiceFacade;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@FxmlView("/fxml/priorityPlannerTab.fxml")
public class PriorityPlannerController {

    private PlannerHandler plannerHandler;

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

    @Autowired
    private DataManagementServiceFacade dataManagementServiceFacade;

    public void setup() {
        plannerHandler.setup();
    }

    public void initialize() {
        PlannerElements elements = new PlannerElements();
        elements.setSumTable(sumTable);
        elements.setSumLabel(label);
        elements.setSumCurrent(sumCurrent);
        elements.setSumDesired(sumDesired);
        elements.setPlannerTable(plannerTable);
        elements.setNameColumn(nameColumn);
        elements.setCurrent(current);
        elements.setDesired(desired);
        elements.setLevel(level);
        elements.setSkill1(skill1);
        elements.setSkill2(skill2);
        elements.setSkill3(skill3);
        elements.setPlannerType(PlannerType.PRIORITY);
        plannerHandler = new PlannerHandler(elements, dataManagementServiceFacade);
        plannerHandler.tableInit();
    }
}
