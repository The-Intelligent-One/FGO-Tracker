package com.github.theintelligentone.fgotracker.service.datamanagement;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.theintelligentone.fgotracker.domain.event.BasicEvent;
import com.github.theintelligentone.fgotracker.domain.item.UpgradeMaterial;
import com.github.theintelligentone.fgotracker.domain.other.CardPlacementData;
import com.github.theintelligentone.fgotracker.domain.other.VersionDTO;
import com.github.theintelligentone.fgotracker.domain.servant.BasicServant;
import com.github.theintelligentone.fgotracker.domain.servant.Servant;
import javafx.scene.image.Image;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class DataRequestService {

    private static final String[] SERVANT_TYPES = {"normal", "heroine"};
    private static final String[] MATERIAL_USES = {"skill", "ascension"};
    private static final String[] EXCLUDED_MATERIAL_TYPES = {"eventItem"};
    private static final String EVENT_QUEST = "eventQuest";
    private static final String NA_REGION = "NA";
    private static final String JP_REGION = "JP";
    private static final Map<String, String> ALL_SERVANT_URL = Map.of(
            NA_REGION, "https://api.atlasacademy.io/export/NA/nice_servant.json",
            JP_REGION, "https://api.atlasacademy.io/export/JP/nice_servant_lang_en.json");
    private static final Map<String, String> MAT_URL = Map.of(
            NA_REGION, "https://api.atlasacademy.io/export/NA/nice_item.json",
            JP_REGION, "https://api.atlasacademy.io/export/JP/nice_item_lang_en.json");
    private static final String CLASS_ATTACK_RATE_URL = "https://api.atlasacademy.io/export/JP/NiceClassAttackRate.json";
    private static final String CARD_DETAILS_URL = "https://api.atlasacademy.io/export/JP/NiceCard.json";
    private static final String VERSION_URL = "https://api.atlasacademy.io/info";
    private static final int HOLY_GRAIL_ID = 7999;
    private static final Map<String, String> BASIC_EVENT_URL = Map.of(
            NA_REGION, "https://api.atlasacademy.io/export/NA/basic_event.json",
            JP_REGION, "https://api.atlasacademy.io/export/JP/basic_event_lang_en.json");
    private static final Map<String, String> BASIC_SERVANT_URL = Map.of(
            NA_REGION, "https://api.atlasacademy.io/export/NA/basic_servant.json",
            JP_REGION, "https://api.atlasacademy.io/export/JP/basic_servant_lang_en.json");
    private static final Map<String, String> SERVANT_ID_SEARCH_URL = Map.of(
            NA_REGION, "https://api.atlasacademy.io/nice/NA/servant/%d",
            JP_REGION, "https://api.atlasacademy.io/nice/JP/servant/%d?lang=en");
    private final ObjectMapper objectMapper;

    public DataRequestService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<Servant> getAllServantData(String gameRegion) {
        List<Servant> dataList = getDataFromUrl(ALL_SERVANT_URL.get(gameRegion), new TypeReference<>() {});
        return dataList.stream().filter(this::isServant).collect(Collectors.toList());
    }

    public Servant getServantDataById(String gameRegion, long id) {
        return getDataFromEitherRegion(gameRegion, id, SERVANT_ID_SEARCH_URL, new TypeReference<>() {});
    }

    public List<BasicServant> getBasicServantData(String gameRegion) {
        return getDataListFromEitherRegion(gameRegion, BASIC_SERVANT_URL, Comparator.comparing(BasicServant::getCollectionNo),
                new TypeReference<>() {}).stream().filter(this::isBasicServant).collect(Collectors.toList());
    }

    public List<UpgradeMaterial> getAllMaterialData(String gameRegion) {
        List<UpgradeMaterial> dataList = getDataListFromEitherRegion(gameRegion, MAT_URL,
                Comparator.comparing(UpgradeMaterial::getId), new TypeReference<>() {});
        return dataList.stream().filter(this::isMat).collect(Collectors.toList());
    }

    public List<BasicEvent> getBasicEventData(String gameRegion) {
        List<BasicEvent> allBasicEvents = getDataListFromEitherRegion(gameRegion, BASIC_EVENT_URL,
                Comparator.comparing(BasicEvent::getStartedAt), new TypeReference<>() {});
        allBasicEvents.removeIf(basicEvent -> !EVENT_QUEST.equals(basicEvent.getType()));
        return allBasicEvents;
    }

    public Map<String, Integer> getClassAttackRate() {
        Map<String, Integer> dataFromUrl = getDataFromUrl(CLASS_ATTACK_RATE_URL, new TypeReference<>() {});
        return dataFromUrl == null ? new HashMap<>() : dataFromUrl;
    }

    public Map<String, Map<Integer, CardPlacementData>> getCardDetails() {
        Map<String, Map<Integer, CardPlacementData>> dataFromUrl = getDataFromUrl(CARD_DETAILS_URL, new TypeReference<>() {});
        return dataFromUrl == null ? new HashMap<>() : dataFromUrl;
    }

    public Image getImageForMaterial(UpgradeMaterial material) {
        Image image = null;
        try {
            image = new Image(material.getIcon());
        } catch (IllegalArgumentException e) {
            log.warn("Couldn't find image from url for material: " + material.getName(), e);
        }
        return image;
    }

    public Map<String, VersionDTO> getOnlineVersion() {
        Map<String, VersionDTO> dataFromUrl = getDataFromUrl(VERSION_URL, new TypeReference<>() {});
        return dataFromUrl == null ? new HashMap<>() : dataFromUrl;
    }

    private <T> T getDataFromEitherRegion(String gameRegion, long id, Map<String, String> urlMap, TypeReference<T> typeRef) {
        T dataFromUrl = getDataFromUrl(String.format(SERVANT_ID_SEARCH_URL.get(gameRegion), id), typeRef);
        return dataFromUrl != null ? dataFromUrl : getDataFromUrl(String.format(urlMap.get(JP_REGION), id), typeRef);
    }

    private <T> T getDataFromUrl(String url, TypeReference<T> typeRef) {
        T returnedData = null;
        try {
            returnedData = objectMapper.readValue(new URL(url), typeRef);
        } catch (UnknownHostException e) {
            log.warn("Couldn't reach host: " + e.getLocalizedMessage(), e);
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return returnedData;
    }

    private <T> List<T> getDataListFromEitherRegion(String gameRegion, Map<String, String> urlRegionMap,
                                                    Comparator<T> comparator, TypeReference<List<T>> typeRef) {
        List<T> allResults = new ArrayList<>();
        List<T> jpResults = getDataFromUrl(urlRegionMap.get(JP_REGION), typeRef);
        if (NA_REGION.equals(gameRegion)) {
            allResults.addAll(getDataFromUrl(urlRegionMap.get(NA_REGION), typeRef));
            allResults.sort(comparator);
            jpResults.removeIf(basicEvent -> comparator.compare(allResults.get(allResults.size() - 1), basicEvent) >= 0);
        }
        allResults.addAll(jpResults);
        allResults.sort(comparator);
        return allResults;
    }

    private boolean isServant(Servant svt) {
        return Arrays.asList(SERVANT_TYPES).contains(svt.getType());
    }

    private boolean isBasicServant(BasicServant svt) {
        return Arrays.asList(SERVANT_TYPES).contains(svt.getType());
    }

    private boolean isMat(UpgradeMaterial mat) {
        return isGrail(mat) || isForSkillOrAsc(mat) && isNotEventItem(mat);
    }

    private boolean isGrail(UpgradeMaterial mat) {
        return mat.getId() == HOLY_GRAIL_ID;
    }

    private boolean isNotEventItem(UpgradeMaterial mat) {
        return Arrays.stream(EXCLUDED_MATERIAL_TYPES).noneMatch(mat.getType()::equalsIgnoreCase);
    }

    private boolean isForSkillOrAsc(UpgradeMaterial mat) {
        return Arrays.stream(MATERIAL_USES).anyMatch(mat.getUses()::contains);
    }
}
