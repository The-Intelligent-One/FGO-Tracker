package com.github.theintelligentone.fgotracker.ui.controller;

import com.github.theintelligentone.fgotracker.domain.other.PlannerType;
import com.github.theintelligentone.fgotracker.domain.servant.UserServant;
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
    private TableView<UserServant> plannerTable;
    private TableColumn<UserServant, String> nameColumn;
    private TableColumn<UserServant, ?> current;
    private TableColumn<UserServant, ?> desired;
    private TableColumn<UserServant, Integer> level;
    private TableColumn<UserServant, Integer> skill1;
    private TableColumn<UserServant, Integer> skill2;
    private TableColumn<UserServant, Integer> skill3;
    private PlannerType plannerType;
}
