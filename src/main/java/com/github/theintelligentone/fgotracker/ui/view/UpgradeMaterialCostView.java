package com.github.theintelligentone.fgotracker.ui.view;

import com.github.theintelligentone.fgotracker.domain.item.UpgradeMaterial;
import javafx.beans.property.*;
import lombok.Data;

@Data
public class UpgradeMaterialCostView {
    LongProperty id;
    ObjectProperty<UpgradeMaterial> item;
    IntegerProperty amount;

    public UpgradeMaterialCostView() {
        this.id = new SimpleLongProperty(0);
        this.item = new SimpleObjectProperty<>();
        this.amount = new SimpleIntegerProperty(0);
    }
}
