package com.github.theintelligentone.fgotracker.ui.view;

import com.github.theintelligentone.fgotracker.domain.item.UpgradeCost;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableIntegerValue;
import javafx.beans.value.ObservableLongValue;
import javafx.beans.value.ObservableObjectValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class PlannerServantView {
    private ObservableLongValue svtId;
    private ObservableObjectValue<UserServantView> baseServant;
    private ObservableIntegerValue desLevel;
    private ObservableIntegerValue desSkill1;
    private ObservableIntegerValue desSkill2;
    private ObservableIntegerValue desSkill3;
    private List<UpgradeCost> ascensionMaterials;
    private List<UpgradeCost> skillMaterials;

    public PlannerServantView() {
        this.svtId = new SimpleLongProperty(0);
        this.baseServant = new SimpleObjectProperty<>();
        this.desLevel = new SimpleIntegerProperty(0);
        this.desSkill1 = new SimpleIntegerProperty(0);
        this.desSkill2 = new SimpleIntegerProperty(0);
        this.desSkill3 = new SimpleIntegerProperty(0);
    }

    public ObservableIntegerValue desLevelProperty() {
        return desLevel;
    }

    public ObservableIntegerValue desSkill1Property() {
        return desSkill1;
    }

    public ObservableIntegerValue desSkill2Property() {
        return desSkill2;
    }

    public ObservableIntegerValue desSkill3Property() {
        return desSkill3;
    }
}
