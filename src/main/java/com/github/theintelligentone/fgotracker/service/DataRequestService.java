package com.github.theintelligentone.fgotracker.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.theintelligentone.fgotracker.domain.servant.Servant;
import com.github.theintelligentone.fgotracker.domain.servant.ServantBasicData;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DataRequestService {

    private static final String[] SERVANT_TYPES = {"normal", "heroine"};
    private static final String ALL_SERVANT_BASIC_URL = "https://api.atlasacademy.io/export/NA/basic_servant.json";
    private static final String ALL_SERVANT_URL = "https://api.atlasacademy.io/export/NA/nice_servant.json";
    private static final String VERSION_URL = "https://api.atlasacademy.io/info";
    private final ObjectMapper objectMapper;

    public DataRequestService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<ServantBasicData> getAllBasicServantData() {
        List<ServantBasicData> dataList = null;
        try {
            dataList = objectMapper.readValue(new URL(ALL_SERVANT_BASIC_URL), new TypeReference<>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataList.stream().filter(this::isServant).collect(Collectors.toList());
    }

    public List<Servant> getAllServantData() {
        List<Servant> dataList = null;
        try {
            dataList = objectMapper.readValue(new URL(ALL_SERVANT_URL), new TypeReference<>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataList.stream().filter(svt -> isServant(svt.getBasicData())).collect(Collectors.toList());
    }

    public long getOnlineVersion() {
        JsonNode response = null;
        try {
            response = objectMapper.readTree(new URL(VERSION_URL));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response.get("NA").get("timestamp").asInt();
    }

    private boolean isServant(ServantBasicData svt) {
        return Arrays.asList(SERVANT_TYPES).contains(svt.getType());
    }
}
