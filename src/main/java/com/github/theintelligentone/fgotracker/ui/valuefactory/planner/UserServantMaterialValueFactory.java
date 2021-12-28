package com.github.theintelligentone.fgotracker.ui.valuefactory.planner;

import com.github.theintelligentone.fgotracker.domain.servant.UserServant;
import com.github.theintelligentone.fgotracker.service.ServantUtils;
import javafx.beans.NamedArg;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class UserServantMaterialValueFactory implements Callback<TableColumn.CellDataFeatures<UserServant, Number>, ObservableValue<Number>> {
    private final long matId;

    public UserServantMaterialValueFactory(@NamedArg("matId") long matId) {
        this.matId = matId;
    }

    @Override
    public ObservableValue<Number> call(TableColumn.CellDataFeatures<UserServant, Number> param) {
        SimpleIntegerProperty result = new SimpleIntegerProperty();
        if (param.getValue().getSvtId() != 0) {
            result.set(ServantUtils.getPlannedMatUse(param.getValue(), matId));
        }
        return result;
    }
}
