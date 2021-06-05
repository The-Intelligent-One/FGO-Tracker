package com.github.theintelligentone.fgotracker.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.theintelligentone.fgotracker.domain.item.UpgradeMaterial;
import com.github.theintelligentone.fgotracker.domain.other.CardPlacementData;
import com.github.theintelligentone.fgotracker.domain.servant.Servant;
import javafx.scene.image.Image;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class DataRequestService {

    private static final String[] SERVANT_TYPES = {"normal", "heroine"};
    private static final String[] MATERIAL_USES = {"skill", "ascension"};
    private static final String[] EXCLUDED_MATERIAL_TYPES = {"eventItem"};
    private static final Map<String, String> ALL_SERVANT_URL = Map.of("NA",
            "https://api.atlasacademy.io/export/NA/nice_servant.json",
            "JP", "https://api.atlasacademy.io/export/JP/nice_servant_lang_en.json");
    private static final Map<String, String> MAT_URL = Map.of("NA", "https://api.atlasacademy.io/export/NA/nice_item.json",
            "JP", "https://api.atlasacademy.io/export/JP/nice_item_lang_en.json");
    private static final String CLASS_ATTACK_RATE_URL = "https://api.atlasacademy.io/export/NA/NiceClassAttackRate.json";
    private static final String CARD_DETAILS_URL = "https://api.atlasacademy.io/export/NA/NiceCard.json";
    private static final String VERSION_URL = "https://api.atlasacademy.io/info";
    private static final int HOLY_GRAIL_ID = 7999;
    private final ObjectMapper objectMapper;

    public DataRequestService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<Servant> getAllServantData(String gameRegion) {
        List<Servant> dataList = new ArrayList<>();
        try {
            dataList = objectMapper.readValue(new URL(ALL_SERVANT_URL.get(gameRegion)), new TypeReference<>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataList.stream().filter(this::isServant).collect(Collectors.toList());
    }

    public List<UpgradeMaterial> getAllMaterialData(String gameRegion) {
        List<UpgradeMaterial> dataList = new ArrayList<>();
        try {
            dataList = objectMapper.readValue(new URL(MAT_URL.get(gameRegion)), new TypeReference<>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataList.stream().filter(this::isMat).collect(Collectors.toList());
    }

    public Map<String, Integer> getClassAttackRate() {
        Map<String, Integer> classAttackRate = new HashMap<>();
        try {
            classAttackRate = objectMapper.readValue(new URL(CLASS_ATTACK_RATE_URL), new TypeReference<>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classAttackRate;
    }

    public Map<String, Map<Integer, CardPlacementData>> getCardDetails() {
        Map<String, Map<Integer, CardPlacementData>> cardDetailMap = new HashMap<>();
        try {
            cardDetailMap = objectMapper.readValue(new URL(CARD_DETAILS_URL), new TypeReference<>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cardDetailMap;
    }

    public Image getImageForMaterial(UpgradeMaterial material) {
        return new Image(material.getIcon());
    }

    public Map<String, Long> getOnlineVersion() {
        Map<String, Long> response = new HashMap<>();
        try {
            response = objectMapper.readValue(new URL(CARD_DETAILS_URL), new TypeReference<>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    private boolean isServant(Servant svt) {
        return Arrays.asList(SERVANT_TYPES).contains(svt.getType());
    }

    private boolean isMat(UpgradeMaterial mat) {
        return isGrail(mat) || isForSkillOrAsc(mat) && isNotEventItem(mat);
    }

    private boolean isGrail(UpgradeMaterial mat) {
        return mat.getId() == HOLY_GRAIL_ID;
    }

    private boolean isNotEventItem(UpgradeMaterial mat) {
        return Arrays.asList(EXCLUDED_MATERIAL_TYPES).stream().noneMatch(mat.getType()::equalsIgnoreCase);
    }

    private boolean isForSkillOrAsc(UpgradeMaterial mat) {
        return Arrays.asList(MATERIAL_USES).stream().anyMatch(mat.getUses()::contains);
    }
}
