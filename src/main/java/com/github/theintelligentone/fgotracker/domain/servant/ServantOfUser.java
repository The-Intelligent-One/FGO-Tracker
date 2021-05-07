package com.github.theintelligentone.fgotracker.domain.servant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(value = {"servant"}, ignoreUnknown = true)
public class ServantOfUser {
    private long svtId;
    private String name;
    private String className;
    private String attribute;
    private List<String> cards;
    private int rarity;
    private int fouAtk;
    private int fouHp;
    private int level;
    private int ascension;
    private int npLevel;
    private String npType;
    private String npTarget;
    private int npDamage;
    private int bondLevel;
    private int skillLevel1;
    private int skillLevel2;
    private int skillLevel3;

    public ServantOfUser(Servant baseServant) {
        svtId = baseServant.getId();
        name = baseServant.getName();
        className = baseServant.getClassName().substring(0,1).toUpperCase() + baseServant.getClassName().substring(1);
        attribute = baseServant.getAttribute().substring(0,1).toUpperCase() + baseServant.getAttribute().substring(1);
        rarity = baseServant.getRarity();
        cards = List.copyOf(baseServant.getCards());
        fouAtk = 0;
        fouHp = 0;
        level = 1;
        ascension = 0;
        npLevel = 1;
        bondLevel = 0;
        skillLevel1 = 1;
        skillLevel2 = 1;
        skillLevel3 = 1;
        npDamage = 0;
        npType = "Buster";
        npTarget = "AoE";
    }


}
