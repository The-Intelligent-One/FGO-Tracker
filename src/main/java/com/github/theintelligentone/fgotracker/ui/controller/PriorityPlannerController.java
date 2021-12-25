//package com.github.theintelligentone.fgotracker.ui.controller;
//
//import com.github.theintelligentone.fgotracker.domain.other.PlannerType;
//import com.github.theintelligentone.fgotracker.domain.view.InventoryView;
//import com.github.theintelligentone.fgotracker.domain.view.PlannerServantView;
//import com.github.theintelligentone.fgotracker.service.datamanagement.DataManagementServiceFacade;
//import javafx.fxml.FXML;
//import javafx.scene.control.TableColumn;
//import javafx.scene.control.TableView;
//import lombok.extern.slf4j.Slf4j;
//import net.rgielen.fxweaver.core.FxmlView;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//@FxmlView("/fxml/priorityPlannerTab.fxml")
//public class PriorityPlannerController {
//
//    private PlannerHandler plannerHandler;
//
//    @FXML
//    private TableView<InventoryView> sumTable;
//
//    @FXML
//    private TableColumn<InventoryView, String> label;
//
//    @FXML
//    private TableColumn<InventoryView, String> sumCurrent;
//
//    @FXML
//    private TableColumn<InventoryView, String> sumDesired;
//
//    @FXML
//    private TableView<PlannerServantView> plannerTable;
//
//    @FXML
//    private TableColumn<PlannerServantView, String> nameColumn;
//
//    @FXML
//    private TableColumn<PlannerServantView, ?> current;
//
//    @FXML
//    private TableColumn<PlannerServantView, ?> desired;
//
//    @FXML
//    private TableColumn<PlannerServantView, Integer> level;
//
//    @FXML
//    private TableColumn<PlannerServantView, Integer> skill1;
//
//    @FXML
//    private TableColumn<PlannerServantView, Integer> skill2;
//
//    @FXML
//    private TableColumn<PlannerServantView, Integer> skill3;
//
//    @Autowired
//    private DataManagementServiceFacade dataManagementServiceFacade;
//
//    public void setup() {
//        plannerHandler.setup();
//    }
//
//    public void initialize() {
//        PlannerElements elements = new PlannerElements();
////        elements.setPlannerTab(plannerTab);
//        elements.setSumTable(sumTable);
//        elements.setSumLabel(label);
//        elements.setSumCurrent(sumCurrent);
//        elements.setSumDesired(sumDesired);
//        elements.setPlannerTable(plannerTable);
//        elements.setNameColumn(nameColumn);
//        elements.setCurrent(current);
//        elements.setDesired(desired);
//        elements.setLevel(level);
//        elements.setSkill1(skill1);
//        elements.setSkill2(skill2);
//        elements.setSkill3(skill3);
//        elements.setPlannerType(PlannerType.PRIORITY);
//        plannerHandler = new PlannerHandler(elements, dataManagementServiceFacade);
//        plannerHandler.tableInit();
//    }
//}
