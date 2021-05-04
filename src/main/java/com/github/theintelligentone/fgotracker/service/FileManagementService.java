package com.github.theintelligentone.fgotracker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.theintelligentone.fgotracker.domain.ServantBasicData;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class FileManagementService {
    private static final String BASE_DATA_PATH = "data/";
    private final ObjectMapper objectMapper;

    public FileManagementService(ObjectMapper objectMapper) throws IOException {
        this.objectMapper = objectMapper;
        initFileStructure();
    }

    private void initFileStructure() throws IOException {
        File file = new File(BASE_DATA_PATH,"test.txt");
        file.getParentFile().mkdirs();
        file.createNewFile();
    }

    public void saveBasicServantData(List<ServantBasicData> servants) throws IOException {
        File file = new File(BASE_DATA_PATH,"test.txt");
        objectMapper.writeValue(file, servants);
    }
}
