package com.github.theintelligentone.fgotracker.ui.valuefactory.planner;

import com.github.theintelligentone.fgotracker.domain.servant.PlannerServant;
import com.github.theintelligentone.fgotracker.service.AscensionUtils;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class PlannerServantGrailValueFactory implements Callback<TableColumn.CellDataFeatures<PlannerServant, Number>, ObservableValue<Number>> {

    @Override
    public ObservableValue<Number> call(TableColumn.CellDataFeatures<PlannerServant, Number> param) {
        SimpleIntegerProperty result = null;
        if (param.getValue().getBaseServant() != null) {
            int matSum = new AscensionUtils().sumNeededAscensionGrails(param.getValue());
            if (matSum > 0) {
                result = new SimpleIntegerProperty(matSum);
            }
        }
        return result;
    }
}
