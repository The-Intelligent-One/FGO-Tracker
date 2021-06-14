package com.github.theintelligentone.fgotracker.domain.view;

import com.github.theintelligentone.fgotracker.domain.item.UpgradeMaterial;
import javafx.beans.property.*;
import lombok.Setter;

@Setter
public class UpgradeMaterialCostView {
    private LongProperty id;
    private ObjectProperty<UpgradeMaterial> item;
    private IntegerProperty amount;

    public UpgradeMaterialCostView() {
        this.id = new SimpleLongProperty(0);
        this.item = new SimpleObjectProperty<>();
        this.amount = new SimpleIntegerProperty(0);
    }

    public LongProperty idProperty() {
        return id;
    }

    public ObjectProperty<UpgradeMaterial> itemProperty() {
        return item;
    }

    public IntegerProperty amountProperty() {
        return amount;
    }
}
