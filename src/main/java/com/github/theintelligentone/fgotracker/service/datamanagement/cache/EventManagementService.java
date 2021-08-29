package com.github.theintelligentone.fgotracker.service.datamanagement.cache;

import com.github.theintelligentone.fgotracker.domain.event.BasicEvent;
import com.github.theintelligentone.fgotracker.service.datamanagement.DataRequestService;
import com.github.theintelligentone.fgotracker.service.filemanagement.FileManagementServiceFacade;
import lombok.Getter;

import java.util.List;

public class EventManagementService {
    private final FileManagementServiceFacade fileServiceFacade;
    private final DataRequestService requestService;

    @Getter
    private List<BasicEvent> basicEvents;
    @Getter
    private boolean bannersResized;

    public EventManagementService(FileManagementServiceFacade fileServiceFacade, DataRequestService requestService) {
        this.fileServiceFacade = fileServiceFacade;
        this.requestService = requestService;
    }

    public void loadBasicEventDataFromCache(String gameRegion) {
        basicEvents = fileServiceFacade.loadBasicEventData(gameRegion);
        bannersResized = true;
    }

    public void downloadNewBasicEventData(String gameRegion) {
        bannersResized = false;
        basicEvents = requestService.getBasicEventData(gameRegion);
    }

    public void saveBasicEventData(String gameRegion) {
        fileServiceFacade.saveBasicEventData(basicEvents, gameRegion);
    }


}
