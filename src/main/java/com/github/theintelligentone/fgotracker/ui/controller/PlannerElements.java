package com.github.theintelligentone.fgotracker.ui.controller;

import com.github.theintelligentone.fgotracker.domain.other.PlannerType;
import com.github.theintelligentone.fgotracker.domain.view.InventoryView;
import com.github.theintelligentone.fgotracker.domain.view.UserServantView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import lombok.Data;

@Data
public class PlannerElements {
    private TableView<InventoryView> sumTable;
    private TableColumn<InventoryView, String> sumLabel;
    private TableColumn<InventoryView, String> sumCurrent;
    private TableColumn<InventoryView, String> sumDesired;
    private TableView<UserServantView> plannerTable;
    private TableColumn<UserServantView, String> nameColumn;
    private TableColumn<UserServantView, ?> current;
    private TableColumn<UserServantView, ?> desired;
    private TableColumn<UserServantView, Integer> level;
    private TableColumn<UserServantView, Integer> skill1;
    private TableColumn<UserServantView, Integer> skill2;
    private TableColumn<UserServantView, Integer> skill3;
    private PlannerType plannerType;
}
