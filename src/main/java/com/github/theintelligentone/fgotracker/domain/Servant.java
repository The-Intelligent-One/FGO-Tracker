package com.github.theintelligentone.fgotracker.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Servant {
    @JsonUnwrapped
    private ServantBasicData basicData;
}
