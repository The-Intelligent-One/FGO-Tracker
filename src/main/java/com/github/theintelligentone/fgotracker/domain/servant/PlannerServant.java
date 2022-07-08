package com.github.theintelligentone.fgotracker.domain.servant;

import com.fasterxml.jackson.annotation.*;
import com.github.theintelligentone.fgotracker.domain.item.UpgradeCost;
import lombok.*;

import java.util.List;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(value = {"baseServant", "ascensionMaterials", "skillMaterials", "appendSkillMaterials"}, ignoreUnknown = true)
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
    private int desAppendSkill1;
    private int desAppendSkill2;
    private int desAppendSkill3;
    private List<UpgradeCost> ascensionMaterials;
    private List<UpgradeCost> skillMaterials;
    private List<UpgradeCost> appendSkillMaterials;

    @JsonIgnore
    public int getLevel() {
        return baseServant.getLevel();
    }
    @JsonIgnore
    public int getSkillLevel1() {
        return baseServant.getSkillLevel1();
    }
    @JsonIgnore
    public int getSkillLevel2() {
        return baseServant.getSkillLevel2();
    }
    @JsonIgnore
    public int getSkillLevel3() {
        return baseServant.getSkillLevel3();
    }
    @JsonIgnore
    public int getAppendSkillLevel1() {
        return baseServant.getAppendSkillLevel1();
    }
    @JsonIgnore
    public int getAppendSkillLevel2() {
        return baseServant.getAppendSkillLevel2();
    }
    @JsonIgnore
    public int getAppendSkillLevel3() {
        return baseServant.getAppendSkillLevel3();
    }
    @JsonIgnore
    public void setLevel(int level) {
        baseServant.setLevel(level);
    }
    @JsonIgnore
    public void setSkillLevel1(int skillLevel) {
        baseServant.setSkillLevel1(skillLevel);
    }
    @JsonIgnore
    public void setSkillLevel2(int skillLevel) {
        baseServant.setSkillLevel2(skillLevel);
    }
    @JsonIgnore
    public void setSkillLevel3(int skillLevel) {
        baseServant.setSkillLevel3(skillLevel);
    }
    @JsonIgnore
    public void setAppendSkillLevel1(int skillLevel) {
        baseServant.setAppendSkillLevel1(skillLevel);
    }
    @JsonIgnore
    public void setAppendSkillLevel2(int skillLevel) {
        baseServant.setAppendSkillLevel2(skillLevel);
    }
    @JsonIgnore
    public void setAppendSkillLevel3(int skillLevel) {
        baseServant.setAppendSkillLevel3(skillLevel);
    }
}
