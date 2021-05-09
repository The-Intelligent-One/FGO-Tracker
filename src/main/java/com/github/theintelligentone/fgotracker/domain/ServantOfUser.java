package com.github.theintelligentone.fgotracker.domain;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServantOfUser {
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference
    private ServantBasicData servant;
    private int fouAtk;
    private int fouHp;
    private int level;
    private int ascension;
    private int npLevel;
    private int bondLevel;
    private int skillLevel1;
    private int skillLevel2;
    private int skillLevel3;
}
