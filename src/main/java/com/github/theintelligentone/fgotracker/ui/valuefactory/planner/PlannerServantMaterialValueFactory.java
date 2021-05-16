package com.github.theintelligentone.fgotracker.ui.valuefactory.planner;

import com.github.theintelligentone.fgotracker.domain.servant.PlannerServant;
import com.github.theintelligentone.fgotracker.service.AscensionUtils;
import javafx.beans.NamedArg;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class PlannerServantMaterialValueFactory implements Callback<TableColumn.CellDataFeatures<PlannerServant, Number>, ObservableValue<Number>> {
    private long matId;

    public PlannerServantMaterialValueFactory(@NamedArg("matId") long matId) {
        this.matId = matId;
    }

    @Override
    public ObservableValue<Number> call(TableColumn.CellDataFeatures<PlannerServant, Number> param) {
        SimpleIntegerProperty result = null;
        if (param.getValue().getBaseServant() != null) {
            int matSum = new AscensionUtils().getPlannedMatUse(param.getValue(), matId);
            if (matSum > 0) {
                result = new SimpleIntegerProperty(matSum);
            }
        }
        return result;
    }
}
