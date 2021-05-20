package com.github.theintelligentone.fgotracker.domain.servant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(value = "baseServant", ignoreUnknown = true)
public class UserServant {
    private long svtId;
    private Servant baseServant;
    private int rarity;
    private int fouAtk;
    private int fouHp;
    private int level;
    private boolean ascension;
    private int npLevel;
    private int bondLevel;
    private int skillLevel1;
    private int skillLevel2;
    private int skillLevel3;
}
