package com.github.theintelligentone.fgotracker.ui.valuefactory.planner;

import com.github.theintelligentone.fgotracker.domain.view.PlannerServantView;
import com.github.theintelligentone.fgotracker.service.ServantUtils;
import javafx.beans.NamedArg;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableIntegerValue;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class PlannerServantMaterialValueFactory implements Callback<TableColumn.CellDataFeatures<PlannerServantView, Number>, ObservableValue<Number>> {
    private final long matId;

    public PlannerServantMaterialValueFactory(@NamedArg("matId") long matId) {
        this.matId = matId;
    }

    @Override
    public ObservableValue<Number> call(TableColumn.CellDataFeatures<PlannerServantView, Number> param) {
        ObservableIntegerValue result = new SimpleIntegerProperty();
        if (param.getValue().getBaseServant().getValue() != null && param.getValue().getBaseServant().getValue().getBaseServant().getValue() != null) {
            result = new ServantUtils().getPlannedMatUse(param.getValue(), matId);
        }
        return result;
    }
}
