package com.github.theintelligentone.fgotracker.ui.valuefactory.planner;

import com.github.theintelligentone.fgotracker.domain.view.InventoryView;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import lombok.Getter;

public class InventoryValueFactory implements Callback<TableColumn.CellDataFeatures<InventoryView, Integer>, ObservableValue<Integer>> {
    @Getter
    private long matId;

    public InventoryValueFactory(long matId) {
        this.matId = matId;
    }

    @Override
    public ObservableValue<Integer> call(TableColumn.CellDataFeatures<InventoryView, Integer> param) {
        return getAmountOfMaterial(param.getValue());
    }

    private ObservableValue<Integer> getAmountOfMaterial(InventoryView inventory) {
        IntegerProperty property = inventory.getInventory().stream().filter(mat -> mat.getId().longValue() == matId).map(mat -> mat.getAmount()).findFirst().get();
        return property != null ? property.asObject() : new SimpleIntegerProperty(0).asObject();
    }
}
