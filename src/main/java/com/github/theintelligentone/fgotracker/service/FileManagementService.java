package com.github.theintelligentone.fgotracker.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.theintelligentone.fgotracker.domain.Servant;
import com.github.theintelligentone.fgotracker.domain.ServantBasicData;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileManagementService {
    private static final String BASE_DATA_PATH = "data/";
    private static final String VERSION_FILE = "dbVersion.json";
    private static final String BASIC_DATA_FILE = "cache/servant/basic.json";
    private static final String FULL_DATA_FILE = "cache/servant/full.json";
    private static final String USER_DATA_FILE = "userdata/servants.json";
    private final ObjectMapper objectMapper;

    public FileManagementService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        try {
            initFileStructure();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveBasicServantData(List<ServantBasicData> servants) throws IOException {
        File file = new File(BASE_DATA_PATH, BASIC_DATA_FILE);
        objectMapper.writeValue(file, servants);
    }

    public void saveFullServantData(List<Servant> servants) throws IOException {
        File file = new File(BASE_DATA_PATH, FULL_DATA_FILE);
        objectMapper.writeValue(file, servants);
    }

    public void saveUserServants(List<ServantBasicData> servants) throws IOException {
        File file = new File(BASE_DATA_PATH, USER_DATA_FILE);
        objectMapper.writeValue(file, servants);
    }

    public List<ServantBasicData> loadBasicServantData() throws IOException {
        File file = new File(BASE_DATA_PATH, BASIC_DATA_FILE);
        return objectMapper.readValue(file, new TypeReference<>() {});
    }

    public List<Servant> loadFullServantData() throws IOException {
        File file = new File(BASE_DATA_PATH, FULL_DATA_FILE);
        return objectMapper.readValue(file, new TypeReference<>() {});
    }

    public List<ServantBasicData> loadUserData() throws IOException {
        File file = new File(BASE_DATA_PATH, USER_DATA_FILE);
        return objectMapper.readValue(file, new TypeReference<>() {});
    }

    private void initFileStructure() throws IOException {
        createFileIfDoesNotExist(BASIC_DATA_FILE);
        createFileIfDoesNotExist(FULL_DATA_FILE);
        createFileIfDoesNotExist(USER_DATA_FILE);
        createFileIfDoesNotExist(VERSION_FILE);
    }

    private void createFileIfDoesNotExist(String filePath) throws IOException {
        File file = new File(BASE_DATA_PATH, filePath);
        file.getParentFile().mkdirs();
        file.createNewFile();
    }
}
