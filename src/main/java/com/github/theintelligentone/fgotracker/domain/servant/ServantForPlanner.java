package com.github.theintelligentone.fgotracker.domain.servant;

import com.github.theintelligentone.fgotracker.domain.servant.propertyobjects.UpgradeCost;
import lombok.Data;

import java.util.List;

@Data
public class ServantForPlanner {
    private long svtId;
    private String name;
    private int level;
    private boolean ascension;
    private int desLevel;
    private int skill1;
    private int skill2;
    private int skill3;
    private int desSkill1;
    private int desSkill2;
    private int desSkill3;
    private List<UpgradeCost> ascensionMaterials;
    private List<UpgradeCost> skillMaterials;
}
