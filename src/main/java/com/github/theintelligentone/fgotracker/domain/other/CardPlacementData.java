package com.github.theintelligentone.fgotracker.domain.other;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CardPlacementData {
    private int adjustAtk;
}
