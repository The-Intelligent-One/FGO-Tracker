package com.github.theintelligentone.fgotracker.ui.valuefactory.planner;

import com.github.theintelligentone.fgotracker.service.ServantUtils;
import com.github.theintelligentone.fgotracker.ui.view.PlannerServantView;
import javafx.beans.value.ObservableIntegerValue;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class PlannerServantGrailValueFactory implements Callback<TableColumn.CellDataFeatures<PlannerServantView, Number>, ObservableValue<Number>> {

    @Override
    public ObservableValue<Number> call(TableColumn.CellDataFeatures<PlannerServantView, Number> param) {
        ObservableIntegerValue result = null;
        if (param.getValue().getBaseServant().getValue() != null) {
            ObservableIntegerValue plannedGrails = new ServantUtils().sumNeededAscensionGrails(param.getValue());
            int matSum = plannedGrails.intValue();
            if (matSum > 0) {
                result = plannedGrails;
            }
        }
        return result;
    }
}
