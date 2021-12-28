package com.github.theintelligentone.fgotracker.ui.controller;

import com.github.theintelligentone.fgotracker.domain.event.BasicEvent;
import com.github.theintelligentone.fgotracker.service.datamanagement.DataManagementServiceFacade;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Comparator;

@Component
@FxmlView("/fxml/eventsTab.fxml")
public class EventsController {
    @FXML
    private TextField eventNameField;
    @FXML
    private ChoiceBox<BasicEvent> eventDropdown;

    @Autowired
    private DataManagementServiceFacade dataManagementServiceFacade;

    public void setup() {
        eventDropdown.getItems().addAll(dataManagementServiceFacade.getBasicEvents());
        eventDropdown.getSelectionModel().select(
                dataManagementServiceFacade.getBasicEvents().stream().sorted(Comparator.comparing(BasicEvent::getEndedAt)).filter(
                        basicEvent -> basicEvent.getEndedAt().isAfter(
                                Instant.now())).findFirst().get());
        eventDropdown.setOnAction(event -> eventNameField.setText(eventDropdown.getSelectionModel().getSelectedItem().getName()));
    }
}
