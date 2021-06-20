package com.github.theintelligentone.fgotracker.service.filemanagement;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class FileService {
    private static final String BASE_DATA_PATH = "data/";
    private static final String CACHE_PATH = "cache/";
    private static final String USER_DATA_PATH = "userdata/";
    private static final String OFFLINE_BASE_PATH = "/offline/";
    private static final String MANAGER_DB_PATH = "/managerDB-v1.3.3.csv";

    private static final String VERSION_FILE = "dbVersion.json";
    private static final String SERVANT_DATA_FILE = "servants.json";
    private static final String CLASS_ATTACK_FILE = "classAttack.json";
    private static final String CARD_DATA_FILE = "cardData.json";

    private static final String GAME_REGION_FILE = "region.json";
    private static final String USER_SERVANT_FILE = "servants.json";
    private static final String PLANNED_SERVANT_FILE = "planned.json";
    private static final String PRIORITY_SERVANT_FILE = "priority.json";
    private static final String INVENTORY_FILE = "inventory.json";
    private static final String DARKMODE_FILE = "darkmode.json";
    private ObjectMapper objectMapper;

    public FileService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void createCacheStructureForDirectory(String imageFolderPath) {
        try {
            Files.createDirectories(Path.of(BASE_DATA_PATH + CACHE_PATH, imageFolderPath));
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
        }
    }

    public void saveDataToCache(Object data, String relativePath) {
        saveDataToFile(data, new File(BASE_DATA_PATH + CACHE_PATH, relativePath));
    }

    public void saveUserData(Object data, String relativePath) {
        saveDataToFile(data, new File(BASE_DATA_PATH + USER_DATA_PATH, relativePath));
    }

    public <T> List<T> loadUserDataList(String relativePath, TypeReference<List<T>> expectedType) {
        return getDataListFromFile(new File(BASE_DATA_PATH + USER_DATA_PATH, relativePath), expectedType);
    }

    public <T> List<T> loadDataListFromCache(String relativePath, TypeReference<List<T>> expectedType) {
        return getDataListFromFile(new File(BASE_DATA_PATH + CACHE_PATH, relativePath), expectedType);
    }

    public <T> Map<String, T> loadDataMapFromCache(String relativePath, TypeReference<Map<String, T>> expectedType) {
        return getDataMapFromFile(new File(BASE_DATA_PATH + CACHE_PATH, relativePath), expectedType);
    }

    public Image getImageFromFolder(String imageFolder, String fileName) {
        File file = new File(BASE_DATA_PATH + CACHE_PATH + imageFolder, fileName);
        return new Image(file.toURI().toString());
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

    public void saveImageToCache(Image image, String filePath, String format) {
        File file = new File(BASE_DATA_PATH + CACHE_PATH, filePath + '.' + format);
        try {
            if (!ImageIO.write(SwingFXUtils.fromFXImage(image, null), format, file)) {
                throw new IOException();
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
        List<T> dataList = new ArrayList<>();
        if (file.length() != 0) {
            try {
                dataList = objectMapper.readValue(file, expectedType);
            } catch (FileNotFoundException e) {
                log.debug("Didn't find file: " + file + ", data list loaded as empty.");
            } catch (IOException e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }
        return dataList;
    }

    private <T> Map<String, T> getDataMapFromFile(File file, TypeReference<Map<String, T>> expectedType) {
        Map<String, T> dataMap = new HashMap<>();
        if (file.length() != 0) {
            try {
                dataMap = objectMapper.readValue(file, expectedType);
            } catch (FileNotFoundException e) {
                log.debug("Didn't find file: " + file + ", data map loaded as empty.");
            } catch (IOException e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }
        return dataMap;
    }

    public void copyImagesFromOfflineBackupToCache(String imageFolderPath) {
        try {
            File imageFolder = new File(getClass().getResource(OFFLINE_BASE_PATH + imageFolderPath).toURI());
            for (File file : imageFolder.listFiles()) {
                Files.copy(file.toPath(), new File(BASE_DATA_PATH + CACHE_PATH + imageFolderPath, file.getName()).toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (URISyntaxException | IOException e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    public void copyOfflineBackupToCache(String filePath) {
        try (InputStream servantStream = getClass().getResource(OFFLINE_BASE_PATH + filePath).openStream()) {
            File file = new File(BASE_DATA_PATH + CACHE_PATH, filePath);
            createFileIfDoesNotExist(file);
            Files.copy(servantStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }
}
