package com.github.theintelligentone.fgotracker.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.theintelligentone.fgotracker.domain.item.Inventory;
import com.github.theintelligentone.fgotracker.domain.item.UpgradeMaterial;
import com.github.theintelligentone.fgotracker.domain.item.UpgradeMaterialCost;
import com.github.theintelligentone.fgotracker.domain.other.CardPlacementData;
import com.github.theintelligentone.fgotracker.domain.other.VersionDTO;
import com.github.theintelligentone.fgotracker.domain.servant.ManagerServant;
import com.github.theintelligentone.fgotracker.domain.servant.PlannerServant;
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
import java.util.*;
import java.util.stream.Collectors;

public class FileManagementService {
    private static final String BASE_DATA_PATH = "data/";
    private static final String CACHE_PATH = "cache/";
    private static final String USER_DATA_PATH = "userdata/";
    private static final String PNG_FORMAT = "png";
    private static final String MANAGER_DB_PATH = "/managerDB-v1.3.2.csv";

    private static final String VERSION_FILE = "dbVersion.json";
    private static final String FULL_DATA_FILE = "servants.json";
    private static final String MATERIAL_DATA_FILE = "mats.json";
    private static final String IMAGE_FOLDER_PATH = "images/";
    private static final String CLASS_ATTACK_FILE = "classAttack.json";
    private static final String CARD_DATA_FILE = "cardData.json";

    private static final String GAME_REGION_FILE = "region.json";
    private static final String USER_SERVANT_FILE = "servants.json";
    private static final String PLANNED_SERVANT_FILE = "planned.json";
    private static final String INVENTORY_FILE = "inventory.json";

    private final ObjectMapper objectMapper;

    public FileManagementService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        try {
            initFileStructure();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveFullServantData(List<Servant> servants, String gameRegion) {
        File file = new File(BASE_DATA_PATH + CACHE_PATH, gameRegion + "_" + FULL_DATA_FILE);
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

    public void saveMaterialData(List<UpgradeMaterial> materials, String gameRegion) {
        File file = new File(BASE_DATA_PATH + CACHE_PATH, gameRegion + "_" + MATERIAL_DATA_FILE);
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

    public void saveUserServants(List<UserServant> servants) {
        File file = new File(BASE_DATA_PATH + USER_DATA_PATH, USER_SERVANT_FILE);
        saveDataToFile(servants, file);
    }

    public void savePlannerServants(List<PlannerServant> servants) {
        File file = new File(BASE_DATA_PATH + USER_DATA_PATH, PLANNED_SERVANT_FILE);
        saveDataToFile(servants, file);
    }

    public void saveInventory(Inventory inventory) {
        File file = new File(BASE_DATA_PATH + USER_DATA_PATH, INVENTORY_FILE);
        saveDataToFile(inventory.getInventory(), file);
    }

    private void saveDataToFile(Object data, File file) {
        try {
            objectMapper.writeValue(file, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Servant> loadFullServantData(String gameRegion) {
        File file = new File(BASE_DATA_PATH + CACHE_PATH, gameRegion + "_" + FULL_DATA_FILE);
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

    public List<UpgradeMaterial> loadMaterialData(String gameRegion) {
        File file = new File(BASE_DATA_PATH + CACHE_PATH, gameRegion + "_" + MATERIAL_DATA_FILE);
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
        File file = new File(BASE_DATA_PATH + USER_DATA_PATH, USER_SERVANT_FILE);
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

    public List<PlannerServant> loadPlannedServantData() {
        File file = new File(BASE_DATA_PATH + USER_DATA_PATH, PLANNED_SERVANT_FILE);
        List<PlannerServant> basicDataList = new ArrayList<>();
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

    public Map<String, VersionDTO> getCurrentVersion() {
        File file = new File(BASE_DATA_PATH, VERSION_FILE);
        Map<String, VersionDTO> versionMap = new HashMap<>();
        try {
            versionMap = objectMapper.readValue(file, new TypeReference<>() {});
        } catch (JsonMappingException e) {
            versionMap.put("NA", new VersionDTO());
            versionMap.put("JP", new VersionDTO());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return versionMap;
    }

    public String loadGameRegion() {
        String regionAsString = "";
        try {
            regionAsString = Files.readString(Path.of(BASE_DATA_PATH + USER_DATA_PATH, GAME_REGION_FILE));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return regionAsString;
    }

    public void saveNewVersion(Map<String, VersionDTO> versionMap) {
        File file = new File(BASE_DATA_PATH, VERSION_FILE);
        saveDataToFile(versionMap, file);
    }

    public void saveGameRegion(String region) {
        try {
            Files.writeString(Path.of(BASE_DATA_PATH + USER_DATA_PATH, GAME_REGION_FILE), region);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initFileStructure() throws IOException {
        createFileIfDoesNotExist(BASE_DATA_PATH + CACHE_PATH + "NA_" + FULL_DATA_FILE);
        createFileIfDoesNotExist(BASE_DATA_PATH + CACHE_PATH + "JP_" + FULL_DATA_FILE);
        createFileIfDoesNotExist(BASE_DATA_PATH + CACHE_PATH + "NA_" + MATERIAL_DATA_FILE);
        createFileIfDoesNotExist(BASE_DATA_PATH + CACHE_PATH + "JP_" + MATERIAL_DATA_FILE);
        createFileIfDoesNotExist(BASE_DATA_PATH + USER_DATA_PATH + USER_SERVANT_FILE);
        createFileIfDoesNotExist(BASE_DATA_PATH + USER_DATA_PATH + INVENTORY_FILE);
        createFileIfDoesNotExist(BASE_DATA_PATH + USER_DATA_PATH + GAME_REGION_FILE);
        createFileIfDoesNotExist(BASE_DATA_PATH + VERSION_FILE);
        Files.createDirectories(Path.of(BASE_DATA_PATH, IMAGE_FOLDER_PATH));
    }

    private void createFileIfDoesNotExist(String filePath) throws IOException {
        File file = new File(filePath);
        file.getParentFile().mkdirs();
        file.createNewFile();
    }

    public List<String[]> importRosterCsv(File sourceFile) {
        List<String[]> strings = new ArrayList<>();
        try {
            FileReader fileReader = new FileReader(sourceFile);
            CSVReader csvReader = new CSVReaderBuilder(fileReader)
                    .withSkipLines(2)
                    .build();
            strings = csvReader.readAll();
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
        return strings;
    }

    public List<String[]> importPlannerCsv(File sourceFile) {
        List<String[]> strings = new ArrayList<>();
        try {
            FileReader fileReader = new FileReader(sourceFile);
            CSVReader csvReader = new CSVReaderBuilder(fileReader)
                    .withSkipLines(12)
                    .build();
            strings = csvReader.readAll();
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
        return strings;
    }

    public Map<String, Integer> importInventoryCsv(File sourceFile) {
        List<String[]> strings = new ArrayList<>();
        try {
            FileReader fileReader = new FileReader(sourceFile);
            CSVReader csvReader = new CSVReaderBuilder(fileReader)
                    .withSkipLines(7)
                    .build();
            strings.add(csvReader.readNext());
            csvReader.readNext();
            csvReader.readNext();
            strings.add(csvReader.readNext());
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
        return transformToInventoryMap(strings);
    }

    private Map<String, Integer> transformToInventoryMap(List<String[]> strings) {
        Map<String, Integer> result = new HashMap<>();
        for (int i = 13; i < strings.get(0).length; i++) {
            if (!strings.get(1)[i].isEmpty()) {
                String amountAsString = strings.get(0)[i].isEmpty() ? "0" : strings.get(0)[i].replaceAll("[\\D.]", "");
                result.put(strings.get(1)[i], Integer.parseInt(amountAsString));
            }
        }
        return result;
    }

    public List<ManagerServant> loadManagerLookupTable() {
        Reader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(getClass().getResourceAsStream(MANAGER_DB_PATH))));
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

    public Inventory loadInventory() {
        File file = new File(BASE_DATA_PATH + USER_DATA_PATH, INVENTORY_FILE);
        List<UpgradeMaterialCost> matList = new ArrayList<>();
        if (file.length() != 0) {
            try {
                matList = objectMapper.readValue(file, new TypeReference<>() {});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Inventory result = new Inventory();
        result.setLabel("Inventory");
        result.setInventory(matList);
        return result;
    }
}
