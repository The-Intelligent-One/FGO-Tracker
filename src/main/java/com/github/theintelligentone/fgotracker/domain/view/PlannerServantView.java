package com.github.theintelligentone.fgotracker.domain.view;

import com.github.theintelligentone.fgotracker.domain.item.UpgradeCost;
import javafx.beans.property.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Setter;

import java.util.List;

@Setter
@Builder
@AllArgsConstructor
public class PlannerServantView {
    private LongProperty svtId;
    private ObjectProperty<UserServantView> baseServant;
    private IntegerProperty desLevel;
    private IntegerProperty desSkill1;
    private IntegerProperty desSkill2;
    private IntegerProperty desSkill3;
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

    public LongProperty svtIdProperty() {
        return svtId;
    }


    public ObjectProperty<UserServantView> baseServantProperty() {
        return baseServant;
    }

    public IntegerProperty desLevelProperty() {
        return desLevel;
    }

    public IntegerProperty desSkill1Property() {
        return desSkill1;
    }

    public IntegerProperty desSkill2Property() {
        return desSkill2;
    }

    public IntegerProperty desSkill3Property() {
        return desSkill3;
    }

    public List<UpgradeCost> getAscensionMaterials() {
        return ascensionMaterials;
    }

    public List<UpgradeCost> getSkillMaterials() {
        return skillMaterials;
    }
}
