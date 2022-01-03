package com.github.theintelligentone.fgotracker.ui.controller;

import com.github.theintelligentone.fgotracker.domain.item.Inventory;
import com.github.theintelligentone.fgotracker.domain.other.PlannerType;
import com.github.theintelligentone.fgotracker.domain.servant.UserServant;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import lombok.Data;

@Data
public class PlannerElements {
    private Tab tab;
    private TableView<Inventory> sumTable;
    private TableColumn<Inventory, String> sumLabel;
    private TableColumn<Inventory, String> sumCurrent;
    private TableColumn<Inventory, String> sumDesired;
    private TableView<UserServant> plannerTable;
    private TableColumn<UserServant, String> nameColumn;
    private TableColumn<UserServant, ?> current;
    private TableColumn<UserServant, ?> desired;
    private TableColumn<UserServant, Integer> level;
    private TableColumn<UserServant, Integer> skill1;
    private TableColumn<UserServant, Integer> skill2;
    private TableColumn<UserServant, Integer> skill3;
    private PlannerType plannerType;
    private Inventory planned;
}
