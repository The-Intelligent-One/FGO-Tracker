package com.github.theintelligentone.fgotracker.domain.servant;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServantOfUser {
    @JsonIgnoreProperties
    private ServantBasicData servant;
    private long svtId;
    private int fouAtk;
    private int fouHp;
    private int level;
    private int ascension;
    private int npLevel;
    private int bondLevel;
    private int skillLevel1;
    private int skillLevel2;
    private int skillLevel3;

    public ServantOfUser(ServantBasicData baseServant) {
        servant = baseServant;
        svtId = baseServant.getId();
    }
}
