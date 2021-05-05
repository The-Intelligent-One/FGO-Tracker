package com.github.theintelligentone.fgotracker.domain.servant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Servant {
    @JsonUnwrapped
    private ServantBasicData basicData;
}
