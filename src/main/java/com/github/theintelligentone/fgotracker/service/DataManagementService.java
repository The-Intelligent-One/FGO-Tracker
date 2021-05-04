package com.github.theintelligentone.fgotracker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.theintelligentone.fgotracker.domain.Servant;

import java.io.IOException;
import java.util.List;

public class DataManagementService {
    private final DataRequestService requestService;
    private final FileManagementService fileService;

    public DataManagementService() {
        ObjectMapper objectMapper = new ObjectMapper();
        this.requestService = new DataRequestService(objectMapper);
        this.fileService = new FileManagementService(objectMapper);
        List<Servant> temp = requestService.getAllServantData();
        try {
            fileService.saveFullServantData(temp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
