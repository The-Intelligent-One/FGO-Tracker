package com.github.theintelligentone.fgotracker.ui.valuefactory.planner;

import com.github.theintelligentone.fgotracker.domain.servant.PlannerServant;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class PlannerServantNameValueFactory implements Callback<TableColumn.CellDataFeatures<PlannerServant, String>, ObservableValue<String>> {
    @Override
    public ObservableValue<String> call(TableColumn.CellDataFeatures<PlannerServant, String> param) {
        SimpleStringProperty name = new SimpleStringProperty();
        if (param.getValue().getBaseServant() != null) {
            name.set(param.getValue().getBaseServant().getBaseServant().getName());
        }
        return name;
    }
}
