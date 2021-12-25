package com.github.theintelligentone.fgotracker.domain.view;

import com.github.theintelligentone.fgotracker.domain.item.UpgradeCost;
import com.github.theintelligentone.fgotracker.domain.servant.Servant;
import javafx.beans.property.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Builder
@AllArgsConstructor
public class UserServantView {
    private LongProperty svtId;
    private ObjectProperty<Servant> baseServant;
    private IntegerProperty rarity;
    private IntegerProperty fouAtk;
    private IntegerProperty fouHp;
    private IntegerProperty level;
    private BooleanProperty ascension;
    private IntegerProperty npLevel;
    private IntegerProperty bondLevel;
    private IntegerProperty skillLevel1;
    private IntegerProperty skillLevel2;
    private IntegerProperty skillLevel3;
    private StringProperty notes;
    private IntegerProperty desLevel;
    private IntegerProperty desSkill1;
    private IntegerProperty desSkill2;
    private IntegerProperty desSkill3;
    private List<UpgradeCost> ascensionMaterials;
    private List<UpgradeCost> skillMaterials;

    public UserServantView() {
        this.svtId = new SimpleLongProperty(0);
        this.baseServant = new SimpleObjectProperty<>();
        this.rarity = new SimpleIntegerProperty(0);
        this.fouAtk = new SimpleIntegerProperty(0);
        this.fouHp = new SimpleIntegerProperty(0);
        this.level = new SimpleIntegerProperty(0);
        this.npLevel = new SimpleIntegerProperty(0);
        this.ascension = new SimpleBooleanProperty(false);
        this.bondLevel = new SimpleIntegerProperty(0);
        this.skillLevel1 = new SimpleIntegerProperty(0);
        this.skillLevel2 = new SimpleIntegerProperty(0);
        this.skillLevel3 = new SimpleIntegerProperty(0);
        this.notes = new SimpleStringProperty("");
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

    public ObjectProperty<Servant> baseServantProperty() {
        return baseServant;
    }

    public IntegerProperty rarityProperty() {
        return rarity;
    }

    public IntegerProperty fouAtkProperty() {
        return fouAtk;
    }

    public IntegerProperty fouHpProperty() {
        return fouHp;
    }

    public IntegerProperty levelProperty() {
        return level;
    }

    public BooleanProperty ascensionProperty() {
        return ascension;
    }

    public IntegerProperty npLevelProperty() {
        return npLevel;
    }

    public IntegerProperty bondLevelProperty() {
        return bondLevel;
    }

    public IntegerProperty skillLevel1Property() {
        return skillLevel1;
    }

    public IntegerProperty skillLevel2Property() {
        return skillLevel2;
    }

    public IntegerProperty skillLevel3Property() {
        return skillLevel3;
    }

    public StringProperty notesProperty() {
        return notes;
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
