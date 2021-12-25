package com.github.theintelligentone.fgotracker.domain.servant;

import com.fasterxml.jackson.annotation.*;
import com.github.theintelligentone.fgotracker.domain.item.UpgradeCost;
import com.github.theintelligentone.fgotracker.domain.view.JsonViews;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserServant {
    @JsonView(JsonViews.Base.class)
    private long svtId;
    @JsonView(JsonViews.Base.class)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonIgnoreProperties(allowGetters = true)
    private Servant baseServant;
    @JsonView(JsonViews.Roster.class)
    private int rarity;
    @JsonView(JsonViews.Roster.class)
    private String svtClass;
    @JsonView(JsonViews.Roster.class)
    private int fouAtk;
    @JsonView(JsonViews.Roster.class)
    private int fouHp;
    @JsonView(JsonViews.Roster.class)
    private int level;
    @JsonView(JsonViews.Roster.class)
    private boolean ascension;
    @JsonView(JsonViews.Roster.class)
    private int npLevel;
    @JsonView(JsonViews.Roster.class)
    private int bondLevel;
    @JsonView(JsonViews.Roster.class)
    private int skillLevel1;
    @JsonView(JsonViews.Roster.class)
    private int skillLevel2;
    @JsonView(JsonViews.Roster.class)
    private int skillLevel3;
    @JsonView(JsonViews.Roster.class)
    private String notes;
    @JsonView(JsonViews.Planner.class)
    private int desLevel;
    @JsonView(JsonViews.Planner.class)
    private int desSkill1;
    @JsonView(JsonViews.Planner.class)
    private int desSkill2;
    @JsonView(JsonViews.Planner.class)
    private int desSkill3;
    @JsonView(JsonViews.Planner.class)
    private List<UpgradeCost> ascensionMaterials;
    @JsonView(JsonViews.Planner.class)
    private List<UpgradeCost> skillMaterials;
}
