package com.github.theintelligentone.fgotracker.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URL;

public class DataRequestService {

    ObjectMapper objectMapper = new ObjectMapper();

    public JsonNode getAllServantData() throws Exception{
        return objectMapper.readTree(new URL("https://api.atlasacademy.io/export/NA/basic_servant.json"));
    }
}
