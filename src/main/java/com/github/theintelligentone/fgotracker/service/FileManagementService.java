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
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class FileManagementService {
    private static final String BASE_DATA_PATH = "data/";
    private static final String CACHE_PATH = "cache/";
    private static final String USER_DATA_PATH = "userdata/";
    private static final String PNG_FORMAT = "png";
    private static final String MANAGER_DB_PATH = "/managerDB-v1.3.3.csv";

    private static final String VERSION_FILE = "dbVersion.json";
    private static final String FULL_DATA_FILE = "servants.json";
    private static final String MATERIAL_DATA_FILE = "mats.json";
    private static final String IMAGE_FOLDER_PATH = "images/";
    private static final String CLASS_ATTACK_FILE = "classAttack.json";
    private static final String CARD_DATA_FILE = "cardData.json";

    private static final String GAME_REGION_FILE = "region.json";
    private static final String USER_SERVANT_FILE = "servants.json";
    private static final String PLANNED_SERVANT_FILE = "planned.json";
    private static final String PRIORITY_SERVANT_FILE = "priority.json";
    private static final String INVENTORY_FILE = "inventory.json";
    private static final int LINES_TO_SKIP_IN_ROSTER_CSV = 2;
    private static final int LINES_TO_SKIP_IN_LT_CSV = 12;
    private static final String DARKMODE_FILE = "darkmode.json";

    private final ObjectMapper objectMapper;

    public FileManagementService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        try {
            initFileStructure();
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
        }
    }

    public void saveFullServantData(List<Servant> servants, String gameRegion) {
        saveDataToFile(servants, new File(BASE_DATA_PATH + CACHE_PATH, gameRegion + "_" + FULL_DATA_FILE));
    }

    public void saveMaterialData(List<UpgradeMaterial> materials, String gameRegion) {
        materials.forEach(mat -> saveImage(mat.getIconImage(), mat.getId()));
        saveDataToFile(materials, new File(BASE_DATA_PATH + CACHE_PATH, gameRegion + "_" + MATERIAL_DATA_FILE));
    }

    public void saveClassAttackRate(Map<String, Integer> classAttackRate) {
        saveDataToFile(classAttackRate, new File(BASE_DATA_PATH, CLASS_ATTACK_FILE));
    }

    public void saveCardData(Map<String, Map<Integer, CardPlacementData>> cardData) {
        saveDataToFile(cardData, new File(BASE_DATA_PATH, CARD_DATA_FILE));
    }

    public void saveUserServants(List<UserServant> servants) {
        saveDataToFile(servants, new File(BASE_DATA_PATH + USER_DATA_PATH, USER_SERVANT_FILE));
    }

    public void savePlannerServants(List<PlannerServant> servants) {
        saveDataToFile(servants, new File(BASE_DATA_PATH + USER_DATA_PATH, PLANNED_SERVANT_FILE));
    }

    public void savePriorityServants(List<PlannerServant> servants) {
        saveDataToFile(servants, new File(BASE_DATA_PATH + USER_DATA_PATH, PRIORITY_SERVANT_FILE));
    }

    public void saveInventory(Inventory inventory) {
        saveDataToFile(inventory.getInventory(), new File(BASE_DATA_PATH + USER_DATA_PATH, INVENTORY_FILE));
    }

    public List<Servant> loadFullServantData(String gameRegion) {
        List<Servant> servantList = getDataListFromFile(BASE_DATA_PATH + CACHE_PATH, gameRegion + "_" + FULL_DATA_FILE,
                new TypeReference<>() {});
        return servantList;
    }

    public List<UpgradeMaterial> loadMaterialData(String gameRegion) {
        List<UpgradeMaterial> itemList = getDataListFromFile(BASE_DATA_PATH + CACHE_PATH, gameRegion + "_" + MATERIAL_DATA_FILE,
                new TypeReference<>() {});
        itemList.forEach(this::loadImageForMaterial);
        return itemList;
    }

    public List<UserServant> loadUserData() {
        return getDataListFromFile(BASE_DATA_PATH + USER_DATA_PATH, USER_SERVANT_FILE, new TypeReference<>() {});
    }

    public List<PlannerServant> loadPlannedServantData() {
        return getDataListFromFile(BASE_DATA_PATH + USER_DATA_PATH, PLANNED_SERVANT_FILE, new TypeReference<>() {});
    }

    public List<PlannerServant> loadPriorityServantData() {
        return getDataListFromFile(BASE_DATA_PATH + USER_DATA_PATH, PRIORITY_SERVANT_FILE, new TypeReference<>() {});
    }

    public Map<String, Integer> getClassAttackRate() {
        File file = new File(BASE_DATA_PATH, CLASS_ATTACK_FILE);
        Map<String, Integer> classAttackMap = new HashMap<>();
        if (file.length() != 0) {
            try {
                classAttackMap = objectMapper.readValue(file, new TypeReference<>() {});
            } catch (IOException e) {
                log.error(e.getLocalizedMessage());
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
                log.error(e.getLocalizedMessage());
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
            log.error(e.getLocalizedMessage());
        }
        return versionMap;
    }

    public boolean loadDarkMode() {
        File file = new File(BASE_DATA_PATH + USER_DATA_PATH, DARKMODE_FILE);
        boolean darkMode = true;
        if (file.length() != 0) {
            try {
                darkMode = objectMapper.readValue(file, new TypeReference<>() {});
            } catch (IOException e) {
                log.error(e.getLocalizedMessage());
            }
        }
        return darkMode;
    }

    public String loadGameRegion() {
        String regionAsString = "";
        try {
            regionAsString = Files.readString(Path.of(BASE_DATA_PATH + USER_DATA_PATH, GAME_REGION_FILE));
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
        }
        return regionAsString;
    }

    public void saveNewVersion(Map<String, VersionDTO> versionMap) {
        File file = new File(BASE_DATA_PATH, VERSION_FILE);
        saveDataToFile(versionMap, file);
    }

    public void saveDarkMode(boolean value) {
        File file = new File(BASE_DATA_PATH + USER_DATA_PATH, DARKMODE_FILE);
        saveDataToFile(value, file);
    }

    public void saveGameRegion(String region) {
        try {
            Files.writeString(Path.of(BASE_DATA_PATH + USER_DATA_PATH, GAME_REGION_FILE), region);
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
        }
    }

    public List<String[]> importRosterCsv(File sourceFile) {
        return importServantsFromCsv(sourceFile, LINES_TO_SKIP_IN_ROSTER_CSV);
    }

    public List<String[]> importPlannerCsv(File sourceFile) {
        return importServantsFromCsv(sourceFile, LINES_TO_SKIP_IN_LT_CSV);
    }

    public Inventory loadInventory() {
        List<UpgradeMaterialCost> matList = getDataListFromFile(BASE_DATA_PATH + USER_DATA_PATH, INVENTORY_FILE,
                new TypeReference<>() {});
        Inventory result = new Inventory();
        result.setLabel("Inventory");
        result.setInventory(matList);
        return result;
    }

    public void saveImage(Image image, long id) {
        File file = new File(BASE_DATA_PATH + IMAGE_FOLDER_PATH, id + "." + PNG_FORMAT);
        try {
            if (!ImageIO.write(SwingFXUtils.fromFXImage(image, null), PNG_FORMAT, file)) {
                throw new IOException();
            }
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
        }
    }

    private void initFileStructure() throws IOException {
        createFileIfDoesNotExist(CACHE_PATH + "NA_" + FULL_DATA_FILE);
        createFileIfDoesNotExist(CACHE_PATH + "JP_" + FULL_DATA_FILE);
        createFileIfDoesNotExist(CACHE_PATH + "NA_" + MATERIAL_DATA_FILE);
        createFileIfDoesNotExist(CACHE_PATH + "JP_" + MATERIAL_DATA_FILE);
        createFileIfDoesNotExist(USER_DATA_PATH + USER_SERVANT_FILE);
        createFileIfDoesNotExist(USER_DATA_PATH + INVENTORY_FILE);
        createFileIfDoesNotExist(USER_DATA_PATH + GAME_REGION_FILE);
        createFileIfDoesNotExist(USER_DATA_PATH + DARKMODE_FILE);
        createFileIfDoesNotExist(VERSION_FILE);
        Files.createDirectories(Path.of(BASE_DATA_PATH, IMAGE_FOLDER_PATH));
    }

    private void createFileIfDoesNotExist(String filePath) throws IOException {
        File file = new File(BASE_DATA_PATH, filePath);
        if (file.getParentFile().mkdirs()) {
            log.debug("File structure created for path: {}", filePath);
        }
        if (file.createNewFile()) {
            log.debug("File created with path: {}", filePath);
        }
    }

    private void saveDataToFile(Object data, File file) {
        try {
            objectMapper.writeValue(file, data);
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
        }
    }

    private <T> List<T> getDataListFromFile(String directory, String filename, TypeReference<List<T>> expectedType) {
        File file = new File(directory, filename);
        List<T> basicDataList = new ArrayList<>();
        if (file.length() != 0) {
            try {
                basicDataList = objectMapper.readValue(file, expectedType);
            } catch (IOException e) {
                log.error(e.getLocalizedMessage());
            }
        }
        return basicDataList;
    }

    private List<String[]> importServantsFromCsv(File sourceFile, int linesToSkip) {
        List<String[]> strings = new ArrayList<>();
        try {
            FileReader fileReader = new FileReader(sourceFile, Charset.defaultCharset());
            CSVReader csvReader = new CSVReaderBuilder(fileReader)
                    .withSkipLines(linesToSkip)
                    .build();
            strings = csvReader.readAll();
        } catch (IOException | CsvException e) {
            log.error(e.getLocalizedMessage());
        }
        return strings;
    }

    public Map<String, Integer> importInventoryCsv(File sourceFile) {
        List<String[]> strings = new ArrayList<>();
        try {
            FileReader fileReader = new FileReader(sourceFile, Charset.defaultCharset());
            CSVReader csvReader = new CSVReaderBuilder(fileReader)
                    .withSkipLines(7)
                    .build();
            strings.add(csvReader.readNext());
            csvReader.readNext();
            csvReader.readNext();
            strings.add(csvReader.readNext());
        } catch (IOException | CsvException e) {
            log.error(e.getLocalizedMessage());
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
                Objects.requireNonNull(getClass().getResourceAsStream(MANAGER_DB_PATH)), Charset.defaultCharset()));
        CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
        List<String[]> strings = new ArrayList<>();
        try {
            strings = csvReader.readAll();
        } catch (IOException | CsvException e) {
            log.error(e.getLocalizedMessage());
        }
        return strings.stream().map(this::buildLookupObject).collect(Collectors.toList());
    }

    private ManagerServant buildLookupObject(String... strings) {
        return new ManagerServant(Integer.parseInt(strings[1]), strings[0]);
    }
}
