package com.github.theintelligentone.fgotracker.service.datamanagement.cache;

import com.github.theintelligentone.fgotracker.domain.other.CardPlacementData;
import com.github.theintelligentone.fgotracker.domain.servant.BasicServant;
import com.github.theintelligentone.fgotracker.domain.servant.Servant;
import com.github.theintelligentone.fgotracker.service.datamanagement.DataRequestService;
import com.github.theintelligentone.fgotracker.service.filemanagement.FileManagementServiceFacade;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.github.theintelligentone.fgotracker.service.datamanagement.DataManagementServiceFacade.NAME_FORMAT;

public class ServantManagementService {
    public static Map<String, Integer> CLASS_ATTACK_MULTIPLIER;
    public static Map<String, Map<Integer, CardPlacementData>> CARD_DATA;

    private final FileManagementServiceFacade fileServiceFacade;
    private final DataRequestService requestService;

    @Getter
    private List<Servant> servantDataList;
    @Getter
    private List<BasicServant> basicServantDataList;
    @Getter
    private ObservableList<String> servantNameList;

    public ServantManagementService(FileManagementServiceFacade fileServiceFacade, DataRequestService requestService) {
        this.fileServiceFacade = fileServiceFacade;
        this.requestService = requestService;
    }

    public void downloadNewServantData(String gameRegion) {
        basicServantDataList = requestService.getBasicServantData(gameRegion);
        servantDataList = fileServiceFacade.loadFullServantData(gameRegion);
        CLASS_ATTACK_MULTIPLIER = requestService.getClassAttackRate();
        CARD_DATA = requestService.getCardDetails();
    }

    public void createServantNameList() {
        servantNameList = FXCollections.observableArrayList();
        servantNameList.addAll(basicServantDataList.stream()
                .map(svt -> String.format(NAME_FORMAT, svt.getName(), svt.getRarity(), svt.getClassName()))
                .collect(Collectors.toList()));
    }

    public Servant findServantByFormattedName(String name, String gameRegion) {
        Optional<Servant> searchResult = servantDataList.stream()
                .filter(svt -> name.equalsIgnoreCase(
                        String.format(NAME_FORMAT, svt.getName(), svt.getRarity(), svt.getClassName())))
                .findFirst();
        return searchResult.orElseGet(() -> requestService.getServantDataById(gameRegion, findServantIdByFormattedName(name)));
    }

    private long findServantIdByFormattedName(String name) {
        return Objects.requireNonNull(basicServantDataList.stream()
                        .filter(svt -> name.equalsIgnoreCase(
                                String.format(NAME_FORMAT, svt.getName(), svt.getRarity(), svt.getClassName())))
                        .findFirst().orElse(null))
                .getId();
    }

    public void loadServantDataFromCache(String gameRegion) {
        servantDataList = fileServiceFacade.loadFullServantData(gameRegion);
        basicServantDataList = fileServiceFacade.loadBasicServantData(gameRegion);
        CLASS_ATTACK_MULTIPLIER = fileServiceFacade.loadClassAttackRate();
        CARD_DATA = fileServiceFacade.loadCardData();
    }

    public void saveServantDataToCache(String gameRegion) {
        fileServiceFacade.saveFullServantData(servantDataList, gameRegion);
        fileServiceFacade.saveClassAttackRate(CLASS_ATTACK_MULTIPLIER);
        fileServiceFacade.saveCardData(CARD_DATA);
    }

    public void saveBasicServantDataToCache(String gameRegion) {
        fileServiceFacade.saveBasicServantData(basicServantDataList, gameRegion);
    }
}
