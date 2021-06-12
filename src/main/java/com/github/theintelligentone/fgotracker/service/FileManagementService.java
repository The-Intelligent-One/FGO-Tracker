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
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class FileManagementService {
    private static final String BASE_DATA_PATH = "data/";
    private static final String CACHE_PATH = "cache/";
    private static final String USER_DATA_PATH = "userdata/";
    private static final String OFFLINE_BASE_PATH = "/offline/";
    private static final String PNG_FORMAT = "png";
    private static final String MANAGER_DB_PATH = "/managerDB-v1.3.3.csv";

    private static final String VERSION_FILE = "dbVersion.json";
    private static final String SERVANT_DATA_FILE = "servants.json";
    private static final String MATERIAL_DATA_FILE = "mats.json";
    private static final String IMAGE_FOLDER_PATH = "images/";
    private static final String CLASS_ATTACK_FILE = "classAttack.json";
    private static final String CARD_DATA_FILE = "cardData.json";

    private static final String GAME_REGION_FILE = "region.json";
    private static final String USER_SERVANT_FILE = "servants.json";
    private static final String PLANNED_SERVANT_FILE = "planned.json";
    private static final String PRIORITY_SERVANT_FILE = "priority.json";
    private static final String INVENTORY_FILE = "inventory.json";
    private static final String DARKMODE_FILE = "darkmode.json";

    private static final int LINES_TO_SKIP_IN_ROSTER_CSV = 2;
    private static final int LINES_TO_SKIP_IN_LT_CSV = 12;

    private final ObjectMapper objectMapper;

    public FileManagementService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        try {
            Files.createDirectories(Path.of(BASE_DATA_PATH + CACHE_PATH, IMAGE_FOLDER_PATH));
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
        }
    }

    public void loadOfflineData() {
        copyOfflineBackupToCache("NA_" + SERVANT_DATA_FILE);
        copyOfflineBackupToCache("JP_" + SERVANT_DATA_FILE);
        copyOfflineBackupToCache("NA_" + MATERIAL_DATA_FILE);
        copyOfflineBackupToCache("JP_" + MATERIAL_DATA_FILE);
        copyOfflineBackupToCache(VERSION_FILE);
        copyOfflineBackupToCache(CARD_DATA_FILE);
        copyOfflineBackupToCache(CLASS_ATTACK_FILE);
        copyImagesFromOfflineBackupToCache();
    }

    private void copyImagesFromOfflineBackupToCache() {
        try {
            File imageFolder = new File(getClass().getResource(OFFLINE_BASE_PATH + IMAGE_FOLDER_PATH).toURI());
            for (File file : imageFolder.listFiles()) {
                Files.copy(file.toPath(), new File(BASE_DATA_PATH + CACHE_PATH + IMAGE_FOLDER_PATH, file.getName()).toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (URISyntaxException | IOException e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    private void copyOfflineBackupToCache(String filePath) {
        try (InputStream servantStream = getClass().getResource(OFFLINE_BASE_PATH + filePath).openStream()) {
            File file = new File(BASE_DATA_PATH + CACHE_PATH, filePath);
            createFileIfDoesNotExist(file);
            Files.copy(servantStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    public void saveFullServantData(List<Servant> servants, String gameRegion) {
        saveDataToFile(servants, new File(BASE_DATA_PATH + CACHE_PATH, gameRegion + "_" + SERVANT_DATA_FILE));
    }

    public void saveMaterialData(List<UpgradeMaterial> materials, String gameRegion) {
        materials.forEach(mat -> saveImage(mat.getIconImage(), mat.getId()));
        saveDataToFile(materials, new File(BASE_DATA_PATH + CACHE_PATH, gameRegion + "_" + MATERIAL_DATA_FILE));
    }

    public void saveClassAttackRate(Map<String, Integer> classAttackRate) {
        saveDataToFile(classAttackRate, new File(BASE_DATA_PATH + CACHE_PATH, CLASS_ATTACK_FILE));
    }

    public void saveCardData(Map<String, Map<Integer, CardPlacementData>> cardData) {
        saveDataToFile(cardData, new File(BASE_DATA_PATH + CACHE_PATH, CARD_DATA_FILE));
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
        return getDataListFromFile(new File(BASE_DATA_PATH + CACHE_PATH, gameRegion + "_" + SERVANT_DATA_FILE),
                new TypeReference<>() {});
    }

    public List<UpgradeMaterial> loadMaterialData(String gameRegion) {
        List<UpgradeMaterial> itemList = getDataListFromFile(
                new File(BASE_DATA_PATH + CACHE_PATH, gameRegion + "_" + MATERIAL_DATA_FILE), new TypeReference<>() {});
        itemList.forEach(this::loadImageForMaterial);
        return itemList;
    }

    public List<UserServant> loadUserData() {
        return getDataListFromFile(new File(BASE_DATA_PATH + USER_DATA_PATH, USER_SERVANT_FILE), new TypeReference<>() {});
    }

    public List<PlannerServant> loadPlannedServantData() {
        return getDataListFromFile(new File(BASE_DATA_PATH + USER_DATA_PATH, PLANNED_SERVANT_FILE), new TypeReference<>() {});
    }

    public List<PlannerServant> loadPriorityServantData() {
        return getDataListFromFile(new File(BASE_DATA_PATH + USER_DATA_PATH, PRIORITY_SERVANT_FILE), new TypeReference<>() {});
    }

    public Map<String, Integer> getClassAttackRate() {
        Map<String, Integer> classAttackMap = new HashMap<>();
        try {
            classAttackMap = objectMapper.readValue(new File(BASE_DATA_PATH + CACHE_PATH, CLASS_ATTACK_FILE),
                    new TypeReference<>() {});
        } catch (FileNotFoundException e) {
            log.debug("No valid class damage multiplier file found. Loading blank value");
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return classAttackMap;
    }

    public void loadImageForMaterial(UpgradeMaterial material) {
        File file = new File(BASE_DATA_PATH + CACHE_PATH + IMAGE_FOLDER_PATH, material.getId() + "." + PNG_FORMAT);
        Image iconImage = new Image(file.toURI().toString());
        material.setIconImage(iconImage);
    }

    public Map<String, Map<Integer, CardPlacementData>> getCardData() {
        Map<String, Map<Integer, CardPlacementData>> cardDataMap = new HashMap<>();
        try {
            cardDataMap = objectMapper.readValue(new File(BASE_DATA_PATH + CACHE_PATH, CARD_DATA_FILE), new TypeReference<>() {});
        } catch (FileNotFoundException e) {
            log.debug("No valid card data file found, loading blank value");
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return cardDataMap;
    }

    public Map<String, VersionDTO> getCurrentVersion() {
        Map<String, VersionDTO> versionMap = new HashMap<>();
        try {
            versionMap = objectMapper.readValue(new File(BASE_DATA_PATH + CACHE_PATH, VERSION_FILE), new TypeReference<>() {});
        } catch (JsonMappingException | FileNotFoundException e) {
            log.debug("No valid DB version file found. Loading blank values");
            versionMap.put("NA", new VersionDTO());
            versionMap.put("JP", new VersionDTO());
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return versionMap;
    }

    public boolean loadDarkMode() {
        File file = new File(BASE_DATA_PATH + USER_DATA_PATH, DARKMODE_FILE);
        boolean darkMode = true;
        try {
            darkMode = objectMapper.readValue(file, new TypeReference<>() {});
        } catch (FileNotFoundException e) {
            log.debug("No valid dark mode setting file found. Defaulting to on.");
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return darkMode;
    }

    public String loadGameRegion() {
        String regionAsString = "";
        try {
            regionAsString = Files.readString(new File(BASE_DATA_PATH + USER_DATA_PATH, GAME_REGION_FILE).toPath());
        } catch (NoSuchFileException e) {
            log.debug("No valid game region file found. Loading blank value.");
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return regionAsString;
    }

    public void saveNewVersion(Map<String, VersionDTO> versionMap) {
        File file = new File(BASE_DATA_PATH + CACHE_PATH, VERSION_FILE);
        saveDataToFile(versionMap, file);
    }

    public void saveDarkMode(boolean value) {
        File file = new File(BASE_DATA_PATH + USER_DATA_PATH, DARKMODE_FILE);
        saveDataToFile(value, file);
    }

    public void saveGameRegion(String region) {
        try {
            File file = new File(BASE_DATA_PATH + USER_DATA_PATH, GAME_REGION_FILE);
            createFileIfDoesNotExist(file);
            Files.writeString(file.toPath(), region);
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    public List<String[]> importRosterCsv(File sourceFile) {
        return importServantsFromCsv(sourceFile, LINES_TO_SKIP_IN_ROSTER_CSV);
    }

    public List<String[]> importPlannerCsv(File sourceFile) {
        return importServantsFromCsv(sourceFile, LINES_TO_SKIP_IN_LT_CSV);
    }

    public Inventory loadInventory() {
        List<UpgradeMaterialCost> matList = getDataListFromFile(new File(BASE_DATA_PATH + USER_DATA_PATH, INVENTORY_FILE),
                new TypeReference<>() {});
        Inventory result = new Inventory();
        result.setLabel("Inventory");
        result.setInventory(matList);
        return result;
    }

    public void saveImage(Image image, long id) {
        File file = new File(BASE_DATA_PATH + CACHE_PATH + IMAGE_FOLDER_PATH, id + "." + PNG_FORMAT);
        try {
            if (!ImageIO.write(SwingFXUtils.fromFXImage(image, null), PNG_FORMAT, file)) {
                throw new IOException();
            }
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    private void createFileIfDoesNotExist(File file) {
        if (file.getParentFile().mkdirs()) {
            log.debug("File structure created for path: {}", file.getPath());
        }
        try {
            if (file.createNewFile()) {
                log.debug("File created with path: {}", file.getPath());
            }
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    private void saveDataToFile(Object data, File file) {
        createFileIfDoesNotExist(file);
        try {
            objectMapper.writeValue(file, data);
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    private <T> List<T> getDataListFromFile(File file, TypeReference<List<T>> expectedType) {
        List<T> basicDataList = new ArrayList<>();
        if (file.length() != 0) {
            try {
                basicDataList = objectMapper.readValue(file, expectedType);
            } catch (FileNotFoundException e) {
                log.debug("Didn't find file: " + file + ", data list loaded as empty.");
            } catch (IOException e) {
                log.error(e.getLocalizedMessage(), e);
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
            log.error(e.getLocalizedMessage(), e);
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
            log.error(e.getLocalizedMessage(), e);
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
            log.error(e.getLocalizedMessage(), e);
        }
        return strings.stream().map(this::buildLookupObject).collect(Collectors.toList());
    }

    private ManagerServant buildLookupObject(String... strings) {
        return new ManagerServant(Integer.parseInt(strings[1]), strings[0]);
    }
}
