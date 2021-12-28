package com.github.theintelligentone.fgotracker.domain.servant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.theintelligentone.fgotracker.domain.item.UpgradeCost;
import com.github.theintelligentone.fgotracker.domain.servant.propertyobjects.NoblePhantasm;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Servant {
    private long id;
    private int collectionNo;
    private String name;
    private String type;
    private String className;
    private String attribute;
    private List<String> cards;
    private List<NoblePhantasm> noblePhantasms;
    private int rarity;
    private List<Integer> atkGrowth;
    private List<Integer> hpGrowth;
    private Map<Integer, UpgradeCost> ascensionMaterials;
    private Map<Integer, UpgradeCost> skillMaterials;
}
