package com.github.theintelligentone.fgotracker.ui.valuefactory.planner;

import com.github.theintelligentone.fgotracker.service.ServantUtils;
import com.github.theintelligentone.fgotracker.ui.view.PlannerServantView;
import javafx.beans.NamedArg;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class PlannerServantMaterialValueFactory implements Callback<TableColumn.CellDataFeatures<PlannerServantView, Number>, ObservableValue<Number>> {
    private long matId;

    public PlannerServantMaterialValueFactory(@NamedArg("matId") long matId) {
        this.matId = matId;
    }

    @Override
    public ObservableValue<Number> call(TableColumn.CellDataFeatures<PlannerServantView, Number> param) {
        SimpleIntegerProperty result = null;
        if (param.getValue().getBaseServant().getValue() != null) {
            int matSum = new ServantUtils().getPlannedMatUse(param.getValue(), matId);
            if (matSum > 0) {
                result = new SimpleIntegerProperty(matSum);
            }
        }
        return result;
    }
}
