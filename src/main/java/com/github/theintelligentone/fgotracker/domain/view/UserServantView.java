package com.github.theintelligentone.fgotracker.domain.view;

import com.github.theintelligentone.fgotracker.domain.servant.Servant;
import javafx.beans.property.*;
import lombok.Setter;

@Setter
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
}
