package com.github.theintelligentone.fgotracker.ui.valuefactory.planner;

import com.github.theintelligentone.fgotracker.domain.item.Inventory;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class InventoryValueFactory implements Callback<TableColumn.CellDataFeatures<Inventory, Number>, ObservableValue<Number>> {
    private long matId;

    public InventoryValueFactory(long matId) {
        this.matId = matId;
    }

    @Override
    public ObservableValue<Number> call(TableColumn.CellDataFeatures<Inventory, Number> param) {
        return new SimpleIntegerProperty(getAmountOfMaterial(param.getValue()));
    }

    private int getAmountOfMaterial(Inventory inventory) {
        return inventory.getInventory().stream().filter(mat -> mat.getId() == matId).mapToInt(mat -> mat.getAmount()).findFirst().orElse(0);
    }
}
