package com.github.theintelligentone.fgotracker.service.filemanagement;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.theintelligentone.fgotracker.domain.view.JsonViews;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class FileService {
    private static final String BASE_DATA_PATH = "data/";
    private static final String CACHE_PATH = "cache/";
    private static final String USER_DATA_PATH = "userdata/";
    private static final String MANAGER_DB_PATH = "/managerDB-v1.3.3.csv";

    private final ObjectMapper objectMapper;

    @Autowired
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
        saveDataToFile(data, new File(BASE_DATA_PATH + CACHE_PATH, relativePath), null);
    }

    public void saveUserData(Object data, String relativePath, Class<? extends JsonViews.Base> jsonView) {
        saveDataToFile(data, new File(BASE_DATA_PATH + USER_DATA_PATH, relativePath), jsonView);
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

    public <T> T loadUserData(String relativePath, TypeReference<T> expectedType) {
        return loadData(new File(BASE_DATA_PATH + USER_DATA_PATH, relativePath), expectedType);
    }

    public Image getImageFromFolder(String imageFolder, String fileName) {
        File file = new File(BASE_DATA_PATH + CACHE_PATH + imageFolder, fileName);
        return file.exists() ? new Image(file.toURI().toString()) : null;
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

    private void saveDataToFile(Object data, File file, Class<? extends JsonViews.Base> jsonView) {
        createFileIfDoesNotExist(file);
        try {
            if (jsonView == null) {
                objectMapper.writeValue(file, data);
            } else {
                objectMapper.writerWithView(jsonView).writeValue(file, data);
            }
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

    @SuppressWarnings("unchecked")
    private <T> T loadData(File file, TypeReference<T> expectedType) {
        T data = null;
        if (file.length() != 0) {
            try {
                data = objectMapper.readValue(file, expectedType);
            } catch (FileNotFoundException e) {
                log.debug("Didn't find file: " + file + ", data list loaded as empty.");
            } catch (JsonParseException e) {
                log.error("Couldn't parse file, attempting to load as String. Error message: \n" + e.getLocalizedMessage(), e);
                data = (T) loadFileAsString(file);
            } catch (Exception e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }
        return data;
    }

    private String loadFileAsString(File file) {
        String dataList = "";
        try {
            dataList = Files.readString(file.toPath());
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return dataList;
    }
}
