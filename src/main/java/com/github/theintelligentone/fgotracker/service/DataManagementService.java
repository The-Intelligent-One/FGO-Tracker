package com.github.theintelligentone.fgotracker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.theintelligentone.fgotracker.domain.item.Inventory;
import com.github.theintelligentone.fgotracker.domain.item.UpgradeMaterial;
import com.github.theintelligentone.fgotracker.domain.item.UpgradeMaterialCost;
import com.github.theintelligentone.fgotracker.domain.other.CardPlacementData;
import com.github.theintelligentone.fgotracker.domain.other.PlannerType;
import com.github.theintelligentone.fgotracker.domain.other.VersionDTO;
import com.github.theintelligentone.fgotracker.domain.servant.ManagerServant;
import com.github.theintelligentone.fgotracker.domain.servant.PlannerServant;
import com.github.theintelligentone.fgotracker.domain.servant.Servant;
import com.github.theintelligentone.fgotracker.domain.servant.UserServant;
import com.github.theintelligentone.fgotracker.domain.servant.factory.PlannerServantViewFactory;
import com.github.theintelligentone.fgotracker.domain.servant.factory.UserServantFactory;
import com.github.theintelligentone.fgotracker.domain.view.InventoryView;
import com.github.theintelligentone.fgotracker.domain.view.PlannerServantView;
import com.github.theintelligentone.fgotracker.domain.view.UpgradeMaterialCostView;
import com.github.theintelligentone.fgotracker.domain.view.UserServantView;
import com.github.theintelligentone.fgotracker.service.transformer.InventoryToViewTransformer;
import com.github.theintelligentone.fgotracker.service.transformer.PlannerServantToViewTransformer;
import com.github.theintelligentone.fgotracker.service.transformer.UserServantToViewTransformer;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import lombok.Getter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DataManagementService {
    public static final String VERSION = "v0.2.0-beta";
    public static final int[] MAX_LEVELS = {65, 60, 65, 70, 80, 90};

    private static final int MIN_TABLE_SIZE = 25;
    private static final String NAME_FORMAT = "%s [%s]";
    private static final Map<String, Integer> ROSTER_IMPORT_INDEX_MAP = Map.of(
            "name", 0,
            "npLevel", 14,
            "level", 15,
            "skill1", 16,
            "skill2", 17,
            "skill3", 18,
            "fouHp", 19,
            "fouAtk", 20,
            "bond", 21,
            "notes", 23);
    public static Map<String, Integer> CLASS_ATTACK_MULTIPLIER;
    public static Map<String, Map<Integer, CardPlacementData>> CARD_DATA;
    private static Map<String, String> MAT_NAME_TRANSLATE_MAP;

    private final DataRequestService requestService;
    private final FileManagementService fileService;
    private final UserServantToViewTransformer userServantToViewTransformer;
    private final InventoryToViewTransformer inventoryToViewTransformer;
    private final PlannerServantToViewTransformer plannerServantToViewTransformer;

    @Getter
    private ObservableList<String> servantNameList;
    @Getter
    private ObservableList<String> userServantNameList;
    @Getter
    private List<UpgradeMaterial> materials;
    @Getter
    private InventoryView inventory;
    @Getter
    private boolean iconsResized;
    @Getter
    private String gameRegion;
    private BooleanProperty darkMode;
    private ObservableList<UserServantView> userServantList;
    private ObservableList<PlannerServantView> plannerServantList;
    private ObservableList<PlannerServantView> priorityPlannerServantList;
    private List<Servant> servantDataList;
    private Map<String, VersionDTO> currentVersion;

    public DataManagementService() {
        ObjectMapper objectMapper = new ObjectMapper();
        this.requestService = new DataRequestService(objectMapper);
        this.fileService = new FileManagementService(objectMapper);
        this.userServantToViewTransformer = new UserServantToViewTransformer();
        this.inventoryToViewTransformer = new InventoryToViewTransformer();
        this.plannerServantToViewTransformer = new PlannerServantToViewTransformer();
        setupMatTranslateMap();
        gameRegion = fileService.loadGameRegion();
        darkMode = new SimpleBooleanProperty(true);
    }

    private void setupMatTranslateMap() {
        MAT_NAME_TRANSLATE_MAP = new HashMap<>();
        MAT_NAME_TRANSLATE_MAP.put("blue", "gem");
        MAT_NAME_TRANSLATE_MAP.put("red", "magic gem");
        MAT_NAME_TRANSLATE_MAP.put("gold", "secret gem");
        MAT_NAME_TRANSLATE_MAP.put("Permafrost", "ice");
        MAT_NAME_TRANSLATE_MAP.put("Seashell", "shell");
        MAT_NAME_TRANSLATE_MAP.put("Stinger", "needle");
        MAT_NAME_TRANSLATE_MAP.put("Crown", "光銀の冠");
        MAT_NAME_TRANSLATE_MAP.put("Shard", "煌星のカケラ");
        MAT_NAME_TRANSLATE_MAP.put("Vein", "神脈霊子");
        MAT_NAME_TRANSLATE_MAP.put("Fruit", "悠久の実");
        MAT_NAME_TRANSLATE_MAP.put("Thread", "虹の糸玉");
    }

    public BooleanProperty darkModeProperty() {
        return darkMode;
    }

    public ObservableList<PlannerServantView> getPlannerServantList(PlannerType plannerType) {
        ObservableList<PlannerServantView> processedPlannerList;
        switch (plannerType) {
            case REGULAR:
                processedPlannerList = getPlannerList(plannerServantList);
                break;
            case PRIORITY:
                processedPlannerList = getPlannerList(priorityPlannerServantList);
                break;
            default:
                processedPlannerList = FXCollections.observableArrayList();
        }
        return processedPlannerList;
    }

    private ObservableList<PlannerServantView> getPlannerList(ObservableList<PlannerServantView> sourceList) {
        if (sourceList.size() < MIN_TABLE_SIZE) {
            IntStream.range(0, MIN_TABLE_SIZE - sourceList.size()).forEach(
                    i -> sourceList.add(new PlannerServantView()));
        }
        return sourceList;
    }

    public ObservableList<UserServantView> getUserServantList() {
        if (userServantList.size() < MIN_TABLE_SIZE) {
            IntStream.range(0, MIN_TABLE_SIZE - userServantList.size()).forEach(
                    i -> saveUserServant(new UserServantView()));
        }
        return userServantList;
    }

    public boolean isDataLoaded() {
        return servantDataList != null && !servantDataList.isEmpty();
    }

    public void initApp(String selectedRegion) {
        if (gameRegion.isEmpty()) {
            gameRegion = selectedRegion;
            fileService.saveGameRegion(gameRegion);
        }
        initDataLists();
        refreshAllData();
    }

    private void initDataLists() {
        userServantList = FXCollections.observableArrayList(
                param -> new Observable[]{param.getSvtId(), param.getLevel(), param.getSkillLevel1(), param.getSkillLevel2(), param.getSkillLevel3(), param.getAscension()});
        plannerServantList = FXCollections.observableArrayList(
                param -> new Observable[]{param.getBaseServant(), param.getDesLevel(), param.getDesSkill1(), param.getDesSkill2(), param.getDesSkill3()});
        priorityPlannerServantList = FXCollections.observableArrayList(
                param -> new Observable[]{param.getBaseServant(), param.getDesLevel(), param.getDesSkill1(), param.getDesSkill2(), param.getDesSkill3()});
        userServantList.addListener(this::removeServantFromPlannersWhenRemovedFromRoster);
        userServantList.addListener(this::updateRosterNameListOnChange);
        servantNameList = FXCollections.observableArrayList();
        userServantNameList = FXCollections.observableArrayList();
    }

    private void updateRosterNameListOnChange(ListChangeListener.Change<? extends UserServantView> c) {
        userServantNameList.clear();
        userServantNameList.addAll(c.getList().stream()
                .filter(svt -> svt.getBaseServant().getValue() != null)
                .map(svt -> String.format(NAME_FORMAT, svt.getBaseServant().getValue().getName(),
                        svt.getBaseServant().getValue().getClassName()))
                .collect(Collectors.toList()));
    }

    private void removeServantFromPlannersWhenRemovedFromRoster(ListChangeListener.Change<? extends UserServantView> c) {
        List<Long> ids = c.getList().stream().map(svt -> svt.getSvtId().get()).collect(Collectors.toList());
        plannerServantList.removeIf(
                svt -> svt.getBaseServant().getValue() != null && !ids.contains(svt.getSvtId().longValue()));
        PlannerServantView dummy = new PlannerServantView();
        plannerServantList.add(dummy);
        plannerServantList.remove(dummy);
        priorityPlannerServantList.add(dummy);
        priorityPlannerServantList.remove(dummy);
    }

    private void refreshAllData() {
        darkMode.set(fileService.loadDarkMode());
        if (newVersionAvailable()) {
            refreshCache();
        } else {
            loadFromCache();
        }
        userServantList.addAll(createAssociatedUserServantList());
        inventory = createInventoryWithAssociatedMatList();
        plannerServantList.addAll(createAssociatedPlannerServantList(fileService.loadPlannedServantData()));
        priorityPlannerServantList.addAll(createAssociatedPlannerServantList(fileService.loadPriorityServantData()));
        servantNameList.addAll(
                servantDataList.stream().map(svt -> String.format(NAME_FORMAT, svt.getName(), svt.getClassName())).collect(
                        Collectors.toList()));
    }

    private InventoryView createInventoryWithAssociatedMatList() {
        Inventory inventory = fileService.loadInventory();
        if (inventory.getInventory().size() == 0) {
            inventory = createEmptyInventory();
        } else {
            for (UpgradeMaterialCost mat : inventory.getInventory()) {
                mat.setItem(materials.stream().filter(material -> material.getId() == mat.getId()).findFirst().get());
            }
        }
        inventory.setLabel("Inventory");
        return inventoryToViewTransformer.transform(inventory);
    }

    public Inventory createEmptyInventory() {
        Inventory inventory = new Inventory();
        List<UpgradeMaterialCost> inventoryList = new ArrayList<>();
        materials.forEach(material -> {
            UpgradeMaterialCost mat = new UpgradeMaterialCost();
            mat.setId(material.getId());
            mat.setItem(material);
            mat.setAmount(0);
            inventoryList.add(mat);
        });
        inventory.setInventory(inventoryList);
        return inventory;
    }

    private List<UserServantView> createAssociatedUserServantList() {
        List<UserServant> userServants = fileService.loadUserData();
        userServants.forEach(svt -> {
            if (svt.getSvtId() != 0L) {
                svt.setBaseServant(findServantById(svt.getSvtId()));
            }
        });
        return userServantToViewTransformer.transformAllToViews(userServants);
    }

    private List<PlannerServantView> createAssociatedPlannerServantList(List<PlannerServant> servants) {
        List<PlannerServantView> plannerServants = plannerServantToViewTransformer.transformAll(
                servants);
        plannerServants.forEach(svt -> {
            if (svt.getSvtId().longValue() != 0L) {
                svt.getBaseServant().set(findUserServantById(svt.getSvtId().longValue()));
            }
        });
        return plannerServants;
    }

    private void loadFromCache() {
        servantDataList = fileService.loadFullServantData(gameRegion);
        materials = fileService.loadMaterialData(gameRegion);
        iconsResized = true;
        CLASS_ATTACK_MULTIPLIER = fileService.getClassAttackRate();
        CARD_DATA = fileService.getCardData();
    }

    private void refreshCache() {
        iconsResized = false;
        downloadNewData();
        saveNewDataToCache();
    }

    private void saveNewDataToCache() {
        fileService.saveFullServantData(servantDataList, gameRegion);
        fileService.saveClassAttackRate(CLASS_ATTACK_MULTIPLIER);
        fileService.saveCardData(CARD_DATA);
        fileService.saveNewVersion(currentVersion);
    }

    public void saveMaterialData() {
        fileService.saveMaterialData(materials, gameRegion);
    }

    private void downloadNewData() {
        servantDataList = requestService.getAllServantData(gameRegion);
        materials = requestService.getAllMaterialData(gameRegion);
        materials.forEach(material -> material.setIconImage(requestService.getImageForMaterial(material)));
        CLASS_ATTACK_MULTIPLIER = requestService.getClassAttackRate();
        CARD_DATA = requestService.getCardDetails();
    }

    private boolean newVersionAvailable() {
        currentVersion = fileService.getCurrentVersion();
        Map<String, VersionDTO> onlineVersion = requestService.getOnlineVersion();
        boolean needUpdate = false;
        if (!onlineVersion.isEmpty() && (onlineVersion.get(gameRegion).getTimestamp() > currentVersion.get(
                gameRegion).getTimestamp())) {
            needUpdate = true;
            currentVersion.put(gameRegion, onlineVersion.get(gameRegion));
        } else if (currentVersion.get(gameRegion).getTimestamp() == 0) {
            fileService.loadOfflineData();
            currentVersion = fileService.getCurrentVersion();
        }
        return needUpdate;
    }

    private Servant findServantById(long svtId) {
        return servantDataList.stream().filter(svt -> svtId == svt.getId()).findFirst().get();
    }

    public Servant findServantByFormattedName(String name) {
        return servantDataList.stream().filter(
                svt -> name.equalsIgnoreCase(String.format(NAME_FORMAT, svt.getName(), svt.getClassName()))).findFirst().orElse(
                null);
    }

    private UserServantView findUserServantById(long svtId) {
        return userServantList.stream().filter(svt -> svtId == svt.getSvtId().longValue()).findFirst().get();
    }

    public UserServantView findUserServantByFormattedName(String name) {
        return userServantList.stream()
                .filter(svt -> svt.getBaseServant().getValue() != null)
                .filter(svt -> name.equalsIgnoreCase(String.format(NAME_FORMAT, svt.getBaseServant().getValue().getName(),
                        svt.getBaseServant().getValue().getClassName())))
                .findFirst().orElse(null);
    }

    public List<String> importUserServantsFromCsv(File sourceFile) {
        List<ManagerServant> managerLookup = fileService.loadManagerLookupTable();
        List<String[]> importedData = fileService.importRosterCsv(sourceFile);
        List<UserServant> importedServants = importedData.stream().map(
                csvLine -> buildUserServantFromStringArray(csvLine, managerLookup)).collect(
                Collectors.toList());
        List<String> notFoundNames = importedServants.stream()
                .filter(svt -> svt.getBaseServant() != null && svt.getSvtId() == 0)
                .map(svt -> svt.getBaseServant().getName())
                .collect(Collectors.toList());
        importedServants = importedServants.stream().filter(
                svt -> svt.getBaseServant() == null || svt.getSvtId() != 0).collect(Collectors.toList());
        List<UserServantView> transformedServants = userServantToViewTransformer.transformAllToViews(importedServants);
        transformedServants = clearUnnecessaryEmptyUserRows(transformedServants);
        userServantList.setAll(transformedServants);
        return notFoundNames;
    }

    private Servant findServantFromManager(String name, List<ManagerServant> managerLookup) {
        ManagerServant managerServant = managerLookup.stream().filter(
                svt -> name.equalsIgnoreCase(svt.getName())).findFirst().get();
        return servantDataList.stream().filter(
                svt -> svt.getCollectionNo() == managerServant.getCollectionNo()).findFirst().orElse(new Servant());
    }

    public void eraseUserServant(UserServantView servant) {
        userServantList.set(userServantList.indexOf(servant), new UserServantView());
    }

    public void erasePlannerServant(PlannerServantView servant, PlannerType plannerType) {
        switch (plannerType) {
            case REGULAR:
                plannerServantList.set(plannerServantList.indexOf(servant), new PlannerServantView());
                break;
            case PRIORITY:
                priorityPlannerServantList.set(priorityPlannerServantList.indexOf(servant), new PlannerServantView());
        }
    }

    public void removeUserServant(UserServantView servant) {
        userServantList.remove(servant);
    }

    public void removePlannerServant(PlannerServantView servant, PlannerType plannerType) {
        switch (plannerType) {
            case REGULAR:
                plannerServantList.remove(servant);
                break;
            case PRIORITY:
                priorityPlannerServantList.remove(servant);
        }
    }

    public void savePlannerServant(PlannerServantView plannerServantView, PlannerType plannerType) {
        switch (plannerType) {
            case REGULAR:
                plannerServantList.add(plannerServantView);
                break;
            case PRIORITY:
                priorityPlannerServantList.add(plannerServantView);
                break;
        }
    }

    public void savePlannerServant(int index, PlannerServantView plannerServantView, PlannerType plannerType) {
        switch (plannerType) {
            case REGULAR:
                plannerServantList.add(index, plannerServantView);
                break;
            case PRIORITY:
                priorityPlannerServantList.add(index, plannerServantView);
                break;
        }
    }

    public void saveUserServant(UserServantView servant) {
        userServantList.add(servant);
    }

    public void saveUserServant(int index, UserServantView servant) {
        userServantList.add(index, servant);
    }

    public void replaceBaseServantInRow(int index, UserServantView servant, String newServantName) {
        Servant newBaseServant = findServantByFormattedName(newServantName);
        if (newBaseServant != null) {
            if (servant.getBaseServant() == null || servant.getBaseServant().getValue() == null) {
                userServantList.set(index, userServantToViewTransformer.transform(
                        new UserServantFactory().createUserServantFromBaseServant(newBaseServant)));
            } else {
                servant.getSvtId().set(newBaseServant.getId());
                servant.getRarity().set(newBaseServant.getRarity());
                servant.getBaseServant().set(newBaseServant);
                userServantList.set(index, servant);
            }
        }
    }

    public void replaceBaseServantInPlannerRow(int index, PlannerServantView servant, String newServantName,
                                               PlannerType plannerType) {
        UserServantView newBaseServant = findUserServantByFormattedName(newServantName);
        if (newBaseServant != null) {
            PlannerServantView fromUserServant;
            if (servant.getBaseServant().getValue() == null) {
                fromUserServant = new PlannerServantViewFactory().createFromUserServant(newBaseServant);
            } else {
                servant.getSvtId().set(newBaseServant.getSvtId().longValue());
                servant.getBaseServant().set(newBaseServant);
                fromUserServant = servant;
            }
            switch (plannerType) {
                case REGULAR:
                    plannerServantList.set(index, fromUserServant);
                    break;
                case PRIORITY:
                    priorityPlannerServantList.set(index, fromUserServant);
                    break;
            }
        }
    }

    public void saveUserState() {
        List<UserServantView> clearedUserList = clearUnnecessaryEmptyUserRows(userServantList);
        List<PlannerServantView> clearedPlannerList = clearUnnecessaryEmptyPlannerRows(plannerServantList);
        List<PlannerServantView> clearedPriorityList = clearUnnecessaryEmptyPlannerRows(priorityPlannerServantList);
        fileService.saveUserServants(userServantToViewTransformer.transformAll(clearedUserList));
        fileService.saveInventory(inventoryToViewTransformer.transform(inventory));
        fileService.savePlannerServants(plannerServantToViewTransformer.transformAllFromViews(clearedPlannerList));
        fileService.savePriorityServants(plannerServantToViewTransformer.transformAllFromViews(clearedPriorityList));
        fileService.saveDarkMode(darkMode.getValue());
    }

    private List<UserServantView> clearUnnecessaryEmptyUserRows(List<UserServantView> servantList) {
        List<UserServantView> newList = new ArrayList<>(servantList);
        int index = newList.size() - 1;
        while (!newList.isEmpty() && newList.get(index).getBaseServant().getValue() == null) {
            newList.remove(index--);
        }
        return newList;
    }

    private List<PlannerServantView> clearUnnecessaryEmptyPlannerRows(List<PlannerServantView> servantList) {
        List<PlannerServantView> newList = new ArrayList<>(servantList);
        int index = newList.size() - 1;
        while (!newList.isEmpty() && newList.get(index).getBaseServant().getValue() == null) {
            newList.remove(index--);
        }
        return newList;
    }

    public List<String> importInventoryFromCsv(File sourceFile) {
        Map<String, Integer> importedData = fileService.importInventoryCsv(sourceFile);
        Map<String, Integer> processedData = new HashMap<>();
        List<String> notFoundNames = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : importedData.entrySet()) {
            String matName = findMaterialName(entry.getKey());
            if (matName.isEmpty()) {
                notFoundNames.add(entry.getKey());
            } else {
                processedData.put(matName, entry.getValue());
            }
        }
        for (UpgradeMaterialCostView mat : inventory.getInventory()) {
            Integer amount = processedData.get(mat.getItem().getValue().getName());
            if (amount != null) {
                mat.getAmount().set(amount);
            }
        }
        return notFoundNames;
    }

    private String findMaterialName(String matName) {
        String[] normalizedMatName = normalizeMatName(matName);
        return materials.stream()
                .map(UpgradeMaterial::getName)
                .filter(name -> containsAll(normalizedMatName, name))
                .findFirst().orElse("");
    }

    private boolean containsAll(String[] matName, String name) {
        boolean valid = true;
        for (String word : matName) {
            valid = valid && name.toLowerCase().contains(word.toLowerCase());
        }
        return valid;
    }

    private String[] normalizeMatName(String matName) {
        String processedName = matName.replaceAll("([a-z])([A-Z])", "$1 $2");
        String firstWord = processedName.split(" ")[0];
        if (MAT_NAME_TRANSLATE_MAP.containsKey(firstWord)) {
            processedName = processedName.replace(firstWord, MAT_NAME_TRANSLATE_MAP.get(firstWord));
        }
        return processedName.split(" ");
    }

    public List<String> importPlannerServantsFromCsv(File sourceFile, PlannerType plannerType) {
        List<ManagerServant> managerLookup = fileService.loadManagerLookupTable();
        List<String[]> importedData = fileService.importPlannerCsv(sourceFile);
        List<PlannerServantView> importedServants = importedData.stream().map(
                csvLine -> buildPlannerServantFromStringArray(csvLine, managerLookup)).collect(
                Collectors.toList());
        List<String> notFoundNames = importedServants.stream()
                .filter(svt -> svt.getBaseServant().getValue() != null && svt.getSvtId().getValue() == 0)
                .map(svt -> svt.getBaseServant().getValue().getBaseServant().getValue().getName())
                .collect(Collectors.toList());
        importedServants = importedServants.stream().filter(
                svt -> svt.getBaseServant().getValue() == null || svt.getSvtId().getValue() != 0).collect(Collectors.toList());
        importedServants = clearUnnecessaryEmptyPlannerRows(importedServants);
        switch (plannerType) {
            case REGULAR:
                plannerServantList.setAll(importedServants);
                break;
            case PRIORITY:
                priorityPlannerServantList.setAll(importedServants);
                break;
        }
        return notFoundNames;
    }

    private PlannerServantView buildPlannerServantFromStringArray(String[] importedData, List<ManagerServant> managerLookup) {
        String servantName = importedData[4];
        PlannerServantView servant = new PlannerServantView();
        if (!servantName.isEmpty()) {
            Servant baseServant = findServantFromManager(servantName, managerLookup);
            UserServantView baseUserServant = null;
            if (baseServant.getName() != null && !baseServant.getName().isEmpty()) {
                baseUserServant = findUserServantByFormattedName(
                        String.format(NAME_FORMAT, baseServant.getName(), baseServant.getClassName()));
            }
            if (baseUserServant == null) {
                baseUserServant = userServantToViewTransformer.transform(new UserServant());
                baseServant = new Servant();
                baseServant.setName(servantName);
                baseUserServant.getBaseServant().set(baseServant);
                servant = new PlannerServantView();
                servant.getBaseServant().set(baseUserServant);
            } else {
                servant = new PlannerServantViewFactory().createFromUserServant(baseUserServant);
                servant.getDesLevel().set(Math.max(Math.min(convertToInt(importedData[9]), 100), 1));
                servant.getDesSkill1().set(Math.max(Math.min(convertToInt(importedData[10]), 10), 1));
                servant.getDesSkill2().set(Math.max(Math.min(convertToInt(importedData[11]), 10), 1));
                servant.getDesSkill3().set(Math.max(Math.min(convertToInt(importedData[12]), 10), 1));
            }
        }
        return servant;
    }

    private UserServant buildUserServantFromStringArray(String[] importedData, List<ManagerServant> managerLookup) {
        UserServant servant = new UserServant();
        String servantName = importedData[ROSTER_IMPORT_INDEX_MAP.get("name")];
        if (!servantName.isEmpty()) {
            Servant baseServant = findServantFromManager(servantName, managerLookup);
            if (!(baseServant.getName() == null || baseServant.getName().isEmpty())) {
                servant = createValidUserServant(importedData, baseServant);
            } else {
                servant = createBlankUserServantEntry(servantName);
            }
        }
        return servant;
    }

    private UserServant createBlankUserServantEntry(String servantName) {
        Servant baseServant;
        UserServant servant;
        baseServant = new Servant();
        baseServant.setName(servantName);
        servant = new UserServant();
        servant.setBaseServant(baseServant);
        return servant;
    }

    private UserServant createValidUserServant(String[] importedData, Servant baseServant) {
        UserServant servant;
        servant = new UserServantFactory().createUserServantFromBaseServant(baseServant);
        servant.setNpLevel(getValueFromImportedRosterData(importedData, "npLevel", 1, 5));
        servant.setLevel(getValueFromImportedRosterData(importedData, "level", 1, 100));
        servant.setSkillLevel1(getValueFromImportedRosterData(importedData, "skill1", 1, 10));
        servant.setSkillLevel2(getValueFromImportedRosterData(importedData, "skill2", 1, 10));
        servant.setSkillLevel3(getValueFromImportedRosterData(importedData, "skill3", 1, 10));
        servant.setFouHp(getValueFromImportedRosterData(importedData, "fouHp", 0, 2000));
        servant.setFouAtk(getValueFromImportedRosterData(importedData, "fouAtk", 0, 2000));
        servant.setBondLevel(getValueFromImportedRosterData(importedData, "bond", 0, 15));
        servant.setNotes(importedData[ROSTER_IMPORT_INDEX_MAP.get("notes")]);
        return servant;
    }

    private int getValueFromImportedRosterData(String[] importedData, String propertyName, int min, int max) {
        String stringValue = importedData[ROSTER_IMPORT_INDEX_MAP.get(propertyName)];
        if ("level".equalsIgnoreCase(propertyName)) {
            stringValue = stringValue.isEmpty() ? stringValue : stringValue.substring(4);
        }
        if ("npLevel".equalsIgnoreCase(propertyName)) {
            stringValue = stringValue.isEmpty() ? stringValue : stringValue.substring(2);
        }
        return Math.max(Math.min(convertToInt(stringValue), max), min);
    }

    private int convertToInt(String data) {
        return !(data == null || data.isEmpty()) ? Integer.parseInt(data) : 0;
    }

    public void invalidateCache() {
        for (Map.Entry<String, VersionDTO> entry : currentVersion.entrySet()) {
            entry.getValue().setTimestamp(0);
        }
        fileService.saveNewVersion(currentVersion);
    }
}
