package com.github.theintelligentone.fgotracker.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.theintelligentone.fgotracker.domain.other.CardPlacementData;
import com.github.theintelligentone.fgotracker.domain.servant.Servant;
import com.github.theintelligentone.fgotracker.domain.servant.ServantOfUser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileManagementService {
    private static final String BASE_DATA_PATH = "data/";
    private static final String VERSION_FILE = "dbVersion.json";
    private static final String FULL_DATA_FILE = "cache/servant/full.json";
    private static final String CLASS_ATTACK_FILE = "cache/classAttack.json";
    private static final String CARD_DATA_FILE = "cache/cardData.json";
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

    public void saveFullServantData(List<Servant> servants) {
        File file = new File(BASE_DATA_PATH, FULL_DATA_FILE);
        saveDataToFile(servants, file);
    }

    public void saveClassAttackRate(Map<String, Integer> classAttackRate) {
        File file = new File(BASE_DATA_PATH, CLASS_ATTACK_FILE);
        saveDataToFile(classAttackRate, file);
    }

    public void saveCardData(Map<String, Map<Integer, CardPlacementData>> cardData) {
        File file = new File(BASE_DATA_PATH, CARD_DATA_FILE);
        saveDataToFile(cardData, file);
    }

    private void saveDataToFile(Object data, File file) {
        try {
            objectMapper.writeValue(file, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveUserServants(List<ServantOfUser> servants) {
        File file = new File(BASE_DATA_PATH, USER_DATA_FILE);
        saveDataToFile(servants, file);
    }

    public List<Servant> loadFullServantData() {
        File file = new File(BASE_DATA_PATH, FULL_DATA_FILE);
        List<Servant> servantList = new ArrayList<>();
        if (file.length() != 0) {
            try {
                servantList = objectMapper.readValue(file, new TypeReference<>() {});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return servantList;
    }

    public List<ServantOfUser> loadUserData() {
        File file = new File(BASE_DATA_PATH, USER_DATA_FILE);
        List<ServantOfUser> basicDataList = new ArrayList<>();
        if (file.length() != 0) {
            try {
                basicDataList = objectMapper.readValue(file, new TypeReference<>() {});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return basicDataList;
    }

    public Map<String, Integer> getClassAttackRate() {
        File file = new File(BASE_DATA_PATH, CLASS_ATTACK_FILE);
        Map<String, Integer> classAttackMap = new HashMap<>();
        if (file.length() != 0) {
            try {
                classAttackMap = objectMapper.readValue(file, new TypeReference<>() {});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return classAttackMap;
    }

    public Map<String, Map<Integer, CardPlacementData>> getCardData() {
        File file = new File(BASE_DATA_PATH, CARD_DATA_FILE);
        Map<String, Map<Integer, CardPlacementData>> cardDataMap = new HashMap<>();
        if (file.length() != 0) {
            try {
                cardDataMap = objectMapper.readValue(file, new TypeReference<>() {});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return cardDataMap;
    }

    public long getCurrentVersion() {
        String versionAsString = "";
        try {
            versionAsString = Files.readString(Path.of(BASE_DATA_PATH, VERSION_FILE));
        } catch (IOException e) {
            e.printStackTrace();
        }
        long currentTimestamp = versionAsString.isEmpty() ? 0 : Long.parseLong(versionAsString);
        return currentTimestamp;
    }

    public void saveNewVersion(long timestamp) {
        try {
            Files.writeString(Path.of(BASE_DATA_PATH, VERSION_FILE), String.valueOf(timestamp));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initFileStructure() throws IOException {
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
