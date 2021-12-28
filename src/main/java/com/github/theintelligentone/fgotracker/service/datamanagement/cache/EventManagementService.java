package com.github.theintelligentone.fgotracker.service.datamanagement.cache;

import com.github.theintelligentone.fgotracker.domain.event.BasicEvent;
import com.github.theintelligentone.fgotracker.service.datamanagement.DataRequestService;
import com.github.theintelligentone.fgotracker.service.filemanagement.FileManagementServiceFacade;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EventManagementService {
    @Autowired
    private FileManagementServiceFacade fileServiceFacade;
    @Autowired
    private DataRequestService requestService;

    @Getter
    private List<BasicEvent> basicEvents;
    @Getter
    private boolean bannersResized;

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
