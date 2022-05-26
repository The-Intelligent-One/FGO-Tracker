package com.github.theintelligentone.fgotracker.ui.valuefactory.planner;

import com.github.theintelligentone.fgotracker.domain.servant.PlannerServant;
import com.github.theintelligentone.fgotracker.service.ServantUtils;
import javafx.beans.NamedArg;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class PlannerServantMaterialValueFactory implements Callback<TableColumn.CellDataFeatures<PlannerServant, Number>, ObservableValue<Number>> {
    private final long matId;

    public PlannerServantMaterialValueFactory(@NamedArg("matId") long matId) {
        this.matId = matId;
    }

    @Override
    public ObservableValue<Number> call(TableColumn.CellDataFeatures<PlannerServant, Number> param) {
        SimpleIntegerProperty result = new SimpleIntegerProperty();
        if (param.getValue().getSvtId() != 0) {
            result.set(ServantUtils.getPlannedMatUse(param.getValue(), matId));
        }
        return result;
    }
}
