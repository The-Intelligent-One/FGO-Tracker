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
@JsonIgnoreProperties(value = {"baseServant"}, ignoreUnknown = true)
public class UserServant {
    @EqualsAndHashCode.Include
    private long svtId;
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonIgnoreProperties(allowGetters = true)
    private Servant baseServant;
    private int rarity;
    private String svtClass;
    private int fouAtk;
    private int fouHp;
    private int level;
    private boolean ascension;
    private int npLevel;
    private int bondLevel;
    private int skillLevel1;
    private int skillLevel2;
    private int skillLevel3;
    private int appendSkillLevel1;
    private int appendSkillLevel2;
    private int appendSkillLevel3;
    private String notes;

    public String getName() {
        return baseServant.getName();
    }
}
