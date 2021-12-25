package com.github.theintelligentone.fgotracker.ui.valuefactory.planner;

import com.github.theintelligentone.fgotracker.domain.view.UserServantView;
import com.github.theintelligentone.fgotracker.service.ServantUtils;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableIntegerValue;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class PlannerServantGrailValueFactory implements Callback<TableColumn.CellDataFeatures<UserServantView, Number>, ObservableValue<Number>> {

    @Override
    public ObservableValue<Number> call(TableColumn.CellDataFeatures<UserServantView, Number> param) {
        ObservableIntegerValue result = new SimpleIntegerProperty();
        if (param.getValue().baseServantProperty().getValue() != null) {
            result = ServantUtils.sumNeededAscensionGrails(param.getValue());
        }
        return result;
    }
}
