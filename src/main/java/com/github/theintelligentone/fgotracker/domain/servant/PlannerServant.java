package com.github.theintelligentone.fgotracker.domain.servant;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.github.theintelligentone.fgotracker.domain.item.UpgradeCost;
import lombok.*;

import java.util.List;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(value = {"baseServant", "ascensionMaterials", "skillMaterials"}, ignoreUnknown = true)
public class PlannerServant {
    @EqualsAndHashCode.Include
    private long svtId;
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonIgnoreProperties(allowGetters = true)
    private UserServant baseServant;
    private int desLevel;
    private int desSkill1;
    private int desSkill2;
    private int desSkill3;
    private List<UpgradeCost> ascensionMaterials;
    private List<UpgradeCost> skillMaterials;

    public int getLevel() {
        return baseServant.getLevel();
    }

    public int getSkillLevel1() {
        return baseServant.getSkillLevel1();
    }

    public int getSkillLevel2() {
        return baseServant.getSkillLevel2();
    }

    public int getSkillLevel3() {
        return baseServant.getSkillLevel3();
    }
}
