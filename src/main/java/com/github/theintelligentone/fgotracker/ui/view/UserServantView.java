package com.github.theintelligentone.fgotracker.ui.view;

import com.github.theintelligentone.fgotracker.domain.servant.Servant;
import javafx.beans.property.*;
import lombok.Data;

@Data
public class UserServantView {
    private LongProperty svtId;
    private ObjectProperty<Servant> baseServant;
    private IntegerProperty rarity;
    private IntegerProperty fouAtk;
    private IntegerProperty fouHp;
    private IntegerProperty level;
    private BooleanProperty ascension;
    private IntegerProperty npLevel;
    private StringProperty npType;
    private StringProperty npTarget;
    private IntegerProperty bondLevel;
    private IntegerProperty skillLevel1;
    private IntegerProperty skillLevel2;
    private IntegerProperty skillLevel3;
}
