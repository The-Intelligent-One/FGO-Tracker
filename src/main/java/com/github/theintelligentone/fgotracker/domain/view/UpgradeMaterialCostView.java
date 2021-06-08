package com.github.theintelligentone.fgotracker.domain.view;

import com.github.theintelligentone.fgotracker.domain.item.UpgradeMaterial;
import javafx.beans.property.*;
import lombok.Data;

@Data
public class UpgradeMaterialCostView {
    private LongProperty id;
    private ObjectProperty<UpgradeMaterial> item;
    private IntegerProperty amount;

    public UpgradeMaterialCostView() {
        this.id = new SimpleLongProperty(0);
        this.item = new SimpleObjectProperty<>();
        this.amount = new SimpleIntegerProperty(0);
    }
}
