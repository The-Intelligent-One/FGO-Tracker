package com.github.theintelligentone.fgotracker.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.theintelligentone.fgotracker.domain.ServantBasicData;

import java.net.URL;
import java.util.List;

public class DataRequestService {

    ObjectMapper objectMapper = new ObjectMapper();

    public List<ServantBasicData> getAllServantData() throws Exception {
        return objectMapper.readValue(new URL("https://api.atlasacademy.io/export/NA/basic_servant.json"), new TypeReference<>(){});
    }
}
