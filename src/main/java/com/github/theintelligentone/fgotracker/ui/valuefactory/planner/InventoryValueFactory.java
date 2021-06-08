package com.github.theintelligentone.fgotracker.ui.valuefactory.planner;

import com.github.theintelligentone.fgotracker.domain.view.InventoryView;
import com.github.theintelligentone.fgotracker.domain.view.UpgradeMaterialCostView;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import lombok.Getter;

public class InventoryValueFactory implements Callback<TableColumn.CellDataFeatures<InventoryView, Integer>, ObservableValue<Integer>> {
    @Getter
    private final long matId;

    public InventoryValueFactory(long matId) {
        this.matId = matId;
    }

    @Override
    public ObservableValue<Integer> call(TableColumn.CellDataFeatures<InventoryView, Integer> param) {
        return getAmountOfMaterial(param.getValue());
    }

    private ObservableValue<Integer> getAmountOfMaterial(InventoryView inventory) {
        IntegerProperty property = inventory.getInventory().stream().filter(mat -> mat.getId().longValue() == matId).map(
                UpgradeMaterialCostView::getAmount).findFirst().orElse(null);
        return property == null ? new SimpleIntegerProperty(0).asObject() : property.asObject();
    }
}
