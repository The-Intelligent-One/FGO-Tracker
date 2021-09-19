package com.github.theintelligentone.fgotracker.ui.controller;

import com.github.theintelligentone.fgotracker.domain.other.PlannerType;
import com.github.theintelligentone.fgotracker.domain.view.InventoryView;
import com.github.theintelligentone.fgotracker.domain.view.PlannerServantView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import lombok.Data;

@Data
public class PlannerElements {
    private TableView<InventoryView> sumTable;
    private TableColumn<InventoryView, String> sumLabel;
    private TableColumn<InventoryView, String> sumCurrent;
    private TableColumn<InventoryView, String> sumDesired;
    private TableView<PlannerServantView> plannerTable;
    private TableColumn<PlannerServantView, String> nameColumn;
    private TableColumn<PlannerServantView, ?> current;
    private TableColumn<PlannerServantView, ?> desired;
    private TableColumn<PlannerServantView, Integer> level;
    private TableColumn<PlannerServantView, Integer> skill1;
    private TableColumn<PlannerServantView, Integer> skill2;
    private TableColumn<PlannerServantView, Integer> skill3;
    private PlannerType plannerType;
}
