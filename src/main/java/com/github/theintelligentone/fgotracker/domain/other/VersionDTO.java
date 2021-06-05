package com.github.theintelligentone.fgotracker.domain.other;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VersionDTO {
    private String hash;
    private long timestamp;
}
