package com.github.theintelligentone.fgotracker.service.transformer;

import com.github.theintelligentone.fgotracker.domain.item.UpgradeMaterialCost;
import com.github.theintelligentone.fgotracker.domain.view.UpgradeMaterialCostView;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;

public class UpgradeMaterialCostToViewTransformer {
    public UpgradeMaterialCostView transform(UpgradeMaterialCost mat) {
        UpgradeMaterialCostView result = new UpgradeMaterialCostView();
        result.setId(new SimpleLongProperty(mat.getId()));
        result.setItem(new SimpleObjectProperty<>(mat.getItem()));
        result.setAmount(new SimpleIntegerProperty(mat.getAmount()));
        return result;
    }

    public UpgradeMaterialCost transform(UpgradeMaterialCostView mat) {
        UpgradeMaterialCost result = new UpgradeMaterialCost();
        result.setId(mat.idProperty().longValue());
        result.setItem(mat.itemProperty().getValue());
        result.setAmount(mat.amountProperty().intValue());
        return result;
    }
}
