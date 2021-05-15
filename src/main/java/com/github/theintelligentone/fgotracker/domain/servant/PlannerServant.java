package com.github.theintelligentone.fgotracker.domain.servant;

import com.github.theintelligentone.fgotracker.domain.servant.propertyobjects.UpgradeCost;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlannerServant {
    private long svtId;
    private UserServant baseServant;
    private int desLevel;
    private int desSkill1;
    private int desSkill2;
    private int desSkill3;
    private List<UpgradeCost> ascensionMaterials;
    private List<UpgradeCost> skillMaterials;
}
