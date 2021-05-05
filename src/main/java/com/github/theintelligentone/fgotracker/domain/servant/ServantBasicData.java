package com.github.theintelligentone.fgotracker.domain.servant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServantBasicData {
    private long id;
    private String name;
    private String type;
    private String className;
    private String attribute;
    private int rarity;
    private int atkMax;
    private int hpMax;
}
