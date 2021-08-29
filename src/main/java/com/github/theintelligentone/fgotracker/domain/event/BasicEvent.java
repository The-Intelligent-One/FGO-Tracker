package com.github.theintelligentone.fgotracker.domain.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BasicEvent {
    private long id;
    private String name;
    private Instant startedAt;
    private Instant endedAt;
    private List<Long> warIds;

    @Override
    public String toString() {
        return name;
    }
}
