package com.github.theintelligentone.fgotracker.domain.servant;

import com.github.theintelligentone.fgotracker.domain.servant.propertyobjects.UpgradeCost;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PlannerServant {
    private long svtId;
    private String name;
    private IntegerProperty level;
    private BooleanProperty ascension;
    private int desLevel;
    private IntegerProperty skill1;
    private IntegerProperty skill2;
    private IntegerProperty skill3;
    private int desSkill1;
    private int desSkill2;
    private int desSkill3;
    private List<UpgradeCost> ascensionMaterials;
    private List<UpgradeCost> skillMaterials;
}
