package com.github.theintelligentone.fgotracker.domain.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.scene.image.Image;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpgradeMaterial {
    private long id;
    private String name;
    private String icon;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<String> uses;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String type;
    @JsonIgnore
    private Image iconImage;
    private String background;
}
