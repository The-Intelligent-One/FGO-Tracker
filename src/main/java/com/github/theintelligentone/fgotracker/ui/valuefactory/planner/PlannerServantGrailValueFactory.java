package com.github.theintelligentone.fgotracker.ui.valuefactory.planner;

import com.github.theintelligentone.fgotracker.service.ServantUtils;
import com.github.theintelligentone.fgotracker.ui.view.PlannerServantView;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableIntegerValue;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class PlannerServantGrailValueFactory implements Callback<TableColumn.CellDataFeatures<PlannerServantView, Number>, ObservableValue<Number>> {

    @Override
    public ObservableValue<Number> call(TableColumn.CellDataFeatures<PlannerServantView, Number> param) {
        ObservableIntegerValue result = new SimpleIntegerProperty();
        if (param.getValue().getBaseServant().getValue() != null) {
            result = new ServantUtils().sumNeededAscensionGrails(param.getValue());
        }
        return result;
    }
}
