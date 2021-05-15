package com.github.theintelligentone.fgotracker.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.theintelligentone.fgotracker.domain.item.UpgradeMaterial;
import com.github.theintelligentone.fgotracker.domain.other.CardPlacementData;
import com.github.theintelligentone.fgotracker.domain.servant.ManagerServant;
import com.github.theintelligentone.fgotracker.domain.servant.Servant;
import com.github.theintelligentone.fgotracker.domain.servant.UserServant;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FileManagementService {
    private static final String BASE_DATA_PATH = "data/";
    private static final String VERSION_FILE = "dbVersion.json";
    private static final String FULL_DATA_FILE = "cache/servants.json";
    private static final String MATERIAL_DATA_FILE = "cache/mats.json";
    private static final String IMAGE_FOLDER_PATH = "cache/images/";
    private static final String CLASS_ATTACK_FILE = "cache/classAttack.json";
    private static final String CARD_DATA_FILE = "cache/cardData.json";
    private static final String USER_DATA_FILE = "userdata/servants.json";
    private static final String MANAGER_DB_PATH = "/managerDB-v1.3.2.csv";
    private static final String PNG_FORMAT = "png";
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

    public void saveImage(Image image, long id) {
        File file = new File(BASE_DATA_PATH + IMAGE_FOLDER_PATH, id + "." + PNG_FORMAT);
        try {
            if (!ImageIO.write(SwingFXUtils.fromFXImage(image, null), PNG_FORMAT, file)) {
                throw new IOException();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveMaterialData(List<UpgradeMaterial> materials) {
        File file = new File(BASE_DATA_PATH, MATERIAL_DATA_FILE);
        materials.forEach(mat -> saveImage(mat.getIconImage(), mat.getId()));
        saveDataToFile(materials, file);
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

    public void saveUserServants(List<UserServant> servants) {
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

    public List<UpgradeMaterial> loadMaterialData() {
        File file = new File(BASE_DATA_PATH, MATERIAL_DATA_FILE);
        List<UpgradeMaterial> itemList = new ArrayList<>();
        if (file.length() != 0) {
            try {
                itemList = objectMapper.readValue(file, new TypeReference<>() {});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        itemList.forEach(this::loadImageForMaterial);
        return itemList;
    }

    public List<UserServant> loadUserData() {
        File file = new File(BASE_DATA_PATH, USER_DATA_FILE);
        List<UserServant> basicDataList = new ArrayList<>();
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

    public void loadImageForMaterial(UpgradeMaterial material) {
        File file = new File(BASE_DATA_PATH + IMAGE_FOLDER_PATH, material.getId() + "." + PNG_FORMAT);
        Image iconImage = new Image(file.toURI().toString());
        material.setIconImage(iconImage);
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
        return versionAsString.isEmpty() ? 0 : Long.parseLong(versionAsString);
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
        createFileIfDoesNotExist(MATERIAL_DATA_FILE);
        createFileIfDoesNotExist(USER_DATA_FILE);
        createFileIfDoesNotExist(VERSION_FILE);
        Files.createDirectories(Path.of(BASE_DATA_PATH, IMAGE_FOLDER_PATH));
    }

    private void createFileIfDoesNotExist(String filePath) throws IOException {
        File file = new File(BASE_DATA_PATH, filePath);
        file.getParentFile().mkdirs();
        file.createNewFile();
    }

    public List<String[]> importCsv(File sourceFile) {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(sourceFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        CSVReader csvReader = new CSVReaderBuilder(fileReader)
                .withSkipLines(2)
                .build();
        List<String[]> strings = new ArrayList<>();
        try {
            strings = csvReader.readAll();
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
        return strings;
    }

    public List<ManagerServant> loadManagerLookupTable() {
        Reader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(MANAGER_DB_PATH)));
        CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
        List<String[]> strings = new ArrayList<>();
        try {
            strings = csvReader.readAll();
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
        return strings.stream().map(this::buildLookupObject).collect(Collectors.toList());
    }

    private ManagerServant buildLookupObject(String[] strings) {
        return new ManagerServant(Integer.parseInt(strings[1]), strings[0]);
    }
}
