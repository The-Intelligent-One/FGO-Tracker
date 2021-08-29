package com.github.theintelligentone.fgotracker.ui.controller;

import com.github.theintelligentone.fgotracker.app.MainApp;
import com.github.theintelligentone.fgotracker.domain.event.BasicEvent;
import com.github.theintelligentone.fgotracker.service.datamanagement.DataManagementServiceFacade;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

public class EventsController {
    @FXML
    private TextField eventNameField;
    @FXML
    private ChoiceBox<BasicEvent> eventDropdown;

    private DataManagementServiceFacade dataManagementServiceFacade;

    public void initialize() {
        dataManagementServiceFacade = MainApp.getDataManagementServiceFacade();
    }

    public void setup() {
        eventDropdown.getItems().addAll(dataManagementServiceFacade.getBasicEvents());
        eventDropdown.setOnAction(event -> eventNameField.setText(eventDropdown.getSelectionModel().getSelectedItem().getName()));
    }
}
