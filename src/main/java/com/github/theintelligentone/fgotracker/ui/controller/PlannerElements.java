package com.github.theintelligentone.fgotracker.ui.controller;

import com.github.theintelligentone.fgotracker.domain.item.Inventory;
import com.github.theintelligentone.fgotracker.domain.other.PlannerType;
import com.github.theintelligentone.fgotracker.domain.servant.PlannerServant;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import lombok.Data;

@Data
public class PlannerElements {
    private TableView<Inventory> sumTable;
    private TableColumn<Inventory, String> sumLabel;
    private TableColumn<Inventory, String> sumCurrent;
    private TableColumn<Inventory, String> sumDesired;
    private TableView<PlannerServant> plannerTable;
    private TableColumn<PlannerServant, String> nameColumn;
    private TableColumn<PlannerServant, ?> current;
    private TableColumn<PlannerServant, ?> desired;
    private TableColumn<PlannerServant, Integer> level;
    private TableColumn<PlannerServant, Integer> skill1;
    private TableColumn<PlannerServant, Integer> skill2;
    private TableColumn<PlannerServant, Integer> skill3;
    private PlannerType plannerType;
    private Inventory planned;
    private Inventory sum;
}
