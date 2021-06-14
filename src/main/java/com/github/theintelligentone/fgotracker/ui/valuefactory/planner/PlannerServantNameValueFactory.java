package com.github.theintelligentone.fgotracker.ui.valuefactory.planner;

import com.github.theintelligentone.fgotracker.domain.view.PlannerServantView;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class PlannerServantNameValueFactory implements Callback<TableColumn.CellDataFeatures<PlannerServantView, String>, ObservableValue<String>> {
    @Override
    public ObservableValue<String> call(TableColumn.CellDataFeatures<PlannerServantView, String> param) {
        SimpleStringProperty name = new SimpleStringProperty();
        if (param.getValue().baseServantProperty().getValue() != null && param.getValue().baseServantProperty().getValue().baseServantProperty().getValue() != null) {
            name.set(param.getValue().baseServantProperty().getValue().baseServantProperty().getValue().getName());
        }
        return name;
    }
}
