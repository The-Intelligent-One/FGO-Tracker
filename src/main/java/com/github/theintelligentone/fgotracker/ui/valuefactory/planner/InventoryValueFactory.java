package com.github.theintelligentone.fgotracker.ui.valuefactory.planner;

import com.github.theintelligentone.fgotracker.domain.item.Inventory;
import com.github.theintelligentone.fgotracker.domain.item.UpgradeMaterialCost;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import lombok.Getter;

import java.util.Optional;

public class InventoryValueFactory implements Callback<TableColumn.CellDataFeatures<Inventory, Integer>, ObservableValue<Integer>> {
    @Getter
    private final long matId;

    public InventoryValueFactory(long matId) {
        this.matId = matId;
    }

    @Override
    public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Inventory, Integer> param) {
        ObjectProperty<Integer> amount = new SimpleIntegerProperty().asObject();
        Optional<Integer> property = param.getValue().getInventory().stream()
                .filter(mat -> mat.getId() == matId)
                .map(UpgradeMaterialCost::getAmount)
                .findFirst();
        property.ifPresent(amount::set);
        return amount;
    }

}
