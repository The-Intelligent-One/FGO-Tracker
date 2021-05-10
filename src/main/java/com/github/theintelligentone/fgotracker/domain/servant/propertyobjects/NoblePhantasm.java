package com.github.theintelligentone.fgotracker.domain.servant.propertyobjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NoblePhantasm {
    private String card;
    private List<FgoFunction> functions;
}
