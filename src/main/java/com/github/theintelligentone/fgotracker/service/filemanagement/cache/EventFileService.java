package com.github.theintelligentone.fgotracker.service.filemanagement.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.theintelligentone.fgotracker.domain.event.BasicEvent;
import com.github.theintelligentone.fgotracker.service.filemanagement.FileService;

import java.util.List;

public class EventFileService {
    private static final String PNG_FORMAT = "png";
    private static final String BASIC_EVENT_DATA_FILE = "basic_events.json";
    private static final String IMAGE_FOLDER_PATH = "images/";
    private final FileService fileService;

    public EventFileService(FileService fileService) {
        this.fileService = fileService;
    }

    public List<BasicEvent> loadBasicEventData(String gameRegion) {
        return fileService.loadDataListFromCache(gameRegion + "_" + BASIC_EVENT_DATA_FILE, new TypeReference<>() {});
    }

    public void saveBasicEventData(List<BasicEvent> basicEvents, String gameRegion) {
        fileService.saveDataToCache(basicEvents, gameRegion + "_" + BASIC_EVENT_DATA_FILE);
    }
}
