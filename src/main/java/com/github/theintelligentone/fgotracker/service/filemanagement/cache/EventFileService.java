package com.github.theintelligentone.fgotracker.service.filemanagement.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.theintelligentone.fgotracker.domain.event.BasicEvent;
import com.github.theintelligentone.fgotracker.service.filemanagement.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EventFileService {
    private static final String PNG_FORMAT = "png";
    private static final String BASIC_EVENT_DATA_FILE = "basic_events.json";
    private static final String IMAGE_FOLDER_PATH = "images/";

    @Autowired
    private FileService fileService;

    public List<BasicEvent> loadBasicEventData(String gameRegion) {
        return fileService.loadDataListFromCache(gameRegion + "_" + BASIC_EVENT_DATA_FILE, new TypeReference<>() {});
    }

    public void saveBasicEventData(List<BasicEvent> basicEvents, String gameRegion) {
        fileService.saveDataToCache(basicEvents, gameRegion + "_" + BASIC_EVENT_DATA_FILE);
    }
}
