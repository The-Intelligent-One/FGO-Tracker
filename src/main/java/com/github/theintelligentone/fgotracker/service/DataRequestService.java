package com.github.theintelligentone.fgotracker.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.theintelligentone.fgotracker.domain.other.CardPlacementData;
import com.github.theintelligentone.fgotracker.domain.servant.Servant;
import com.github.theintelligentone.fgotracker.domain.servant.propertyobjects.UpgradeMaterial;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class DataRequestService {

    private static final String[] SERVANT_TYPES = {"normal", "heroine"};
    private static final String[] MATERIAL_USES = {"skill", "ascension"};
    private static final String[] EXCLUDED_MATERIAL_TYPES = {"eventItem"};
    private static final String ALL_SERVANT_URL = "https://api.atlasacademy.io/export/NA/nice_servant.json";
    private static final String MAT_URL = "https://api.atlasacademy.io/export/NA/nice_item.json";
    private static final String CLASS_ATTACK_RATE_URL = "https://api.atlasacademy.io/export/NA/NiceClassAttackRate.json";
    private static final String CARD_DETAILS_URL = "https://api.atlasacademy.io/export/NA/NiceCard.json";
    private static final String VERSION_URL = "https://api.atlasacademy.io/info";
    private final ObjectMapper objectMapper;

    public DataRequestService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<Servant> getAllServantData() {
        List<Servant> dataList = new ArrayList<>();
        try {
            dataList = objectMapper.readValue(new URL(ALL_SERVANT_URL), new TypeReference<>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataList.stream().filter(this::isServant).collect(Collectors.toList());
    }

    public List<UpgradeMaterial> getAllMaterialData() {
        List<UpgradeMaterial> dataList = new ArrayList<>();
        try {
            dataList = objectMapper.readValue(new URL(MAT_URL), new TypeReference<>() {});
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

    public long getOnlineVersion() {
        JsonNode response = null;
        try {
            response = objectMapper.readTree(new URL(VERSION_URL));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Objects.requireNonNull(response).get("NA").get("timestamp").asInt();
    }

    private boolean isServant(Servant svt) {
        return Arrays.asList(SERVANT_TYPES).contains(svt.getType());
    }

    private boolean isMat(UpgradeMaterial mat) {
        return Arrays.asList(MATERIAL_USES).stream().anyMatch(mat.getUses()::contains) && Arrays.asList(EXCLUDED_MATERIAL_TYPES).stream().noneMatch(mat.getType()::equalsIgnoreCase);
    }
}
