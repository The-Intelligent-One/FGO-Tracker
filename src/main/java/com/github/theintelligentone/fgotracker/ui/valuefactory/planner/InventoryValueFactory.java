package com.github.theintelligentone.fgotracker.ui.valuefactory.planner;

import com.github.theintelligentone.fgotracker.domain.view.InventoryView;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableIntegerValue;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class InventoryValueFactory implements Callback<TableColumn.CellDataFeatures<InventoryView, Number>, ObservableValue<Number>> {
    private long matId;

    public InventoryValueFactory(long matId) {
        this.matId = matId;
    }

    @Override
    public ObservableValue<Number> call(TableColumn.CellDataFeatures<InventoryView, Number> param) {
        return getAmountOfMaterial(param.getValue());
    }

    private ObservableIntegerValue getAmountOfMaterial(InventoryView inventory) {
        return inventory.getInventory().stream().filter(mat -> mat.getId().longValue() == matId).map(mat -> mat.getAmount()).findFirst().orElse(new SimpleIntegerProperty(0));
    }
}
