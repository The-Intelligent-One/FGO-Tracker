package com.github.theintelligentone.fgotracker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.theintelligentone.fgotracker.domain.item.Inventory;
import com.github.theintelligentone.fgotracker.domain.item.UpgradeMaterial;
import com.github.theintelligentone.fgotracker.domain.item.UpgradeMaterialCost;
import com.github.theintelligentone.fgotracker.domain.other.CardPlacementData;
import com.github.theintelligentone.fgotracker.domain.servant.ManagerServant;
import com.github.theintelligentone.fgotracker.domain.servant.PlannerServant;
import com.github.theintelligentone.fgotracker.domain.servant.Servant;
import com.github.theintelligentone.fgotracker.domain.servant.UserServant;
import com.github.theintelligentone.fgotracker.domain.servant.factory.PlannerServantViewFactory;
import com.github.theintelligentone.fgotracker.domain.servant.factory.UserServantFactory;
import com.github.theintelligentone.fgotracker.domain.view.InventoryView;
import com.github.theintelligentone.fgotracker.domain.view.PlannerServantView;
import com.github.theintelligentone.fgotracker.domain.view.UserServantView;
import com.github.theintelligentone.fgotracker.service.transformer.InventoryToViewTransformer;
import com.github.theintelligentone.fgotracker.service.transformer.PlannerServantToViewTransformer;
import com.github.theintelligentone.fgotracker.service.transformer.UserServantToViewTransformer;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import lombok.Getter;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DataManagementService {
    public static final int[] MAX_LEVELS = {65, 60, 65, 70, 80, 90};
    private static final int MIN_TABLE_SIZE = 30;
    public static Map<String, Integer> CLASS_ATTACK_MULTIPLIER;
    public static Map<String, Map<Integer, CardPlacementData>> CARD_DATA;
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
    private boolean iconsResized = false;
    private ObservableList<UserServantView> userServantList;
    private ObservableList<PlannerServantView> plannerServantList;
    private List<Servant> servantDataList;
    private long currentVersion;

    public DataManagementService() {
        ObjectMapper objectMapper = new ObjectMapper();
        this.requestService = new DataRequestService(objectMapper);
        this.fileService = new FileManagementService(objectMapper);
        this.userServantToViewTransformer = new UserServantToViewTransformer();
        this.inventoryToViewTransformer = new InventoryToViewTransformer();
        this.plannerServantToViewTransformer = new PlannerServantToViewTransformer();
    }

    public ObservableList<PlannerServantView> getPlannerServantList() {
        if (plannerServantList.size() < MIN_TABLE_SIZE) {
            IntStream.range(0, MIN_TABLE_SIZE - plannerServantList.size()).forEach(i -> savePlannerServant(new PlannerServantView()));
        }
        return plannerServantList;
    }

    public ObservableList<UserServantView> getUserServantList() {
        if (userServantList.size() < MIN_TABLE_SIZE) {
            IntStream.range(0, MIN_TABLE_SIZE - userServantList.size()).forEach(i -> saveUserServant(new UserServantView()));
        }
        return userServantList;
    }

    public boolean isDataLoaded() {
        return servantDataList != null && !servantDataList.isEmpty();
    }

    public void initApp() {
        userServantList = FXCollections.observableArrayList();
        plannerServantList = FXCollections.observableArrayList();
        userServantList.addListener((ListChangeListener<? super UserServantView>) c -> {
            long[] ids = c.getList().stream().mapToLong(svt -> svt.getSvtId().get()).toArray();
            plannerServantList.stream()
                    .filter(svt -> svt.getBaseServant().getValue() != null)
                    .filter(svt -> !Arrays.asList(ids).contains(svt.getSvtId().longValue()))
                    .forEach(plannerServantList::remove);
        });
        userServantList.addListener((ListChangeListener<? super UserServantView>) c -> {
            userServantNameList.clear();
            userServantNameList.addAll(c.getList().stream()
                    .filter(svt -> svt.getBaseServant().getValue() != null)
                    .map(svt -> svt.getBaseServant().getValue().getName())
                    .collect(Collectors.toList()));
        });
        servantNameList = FXCollections.observableArrayList();
        userServantNameList = FXCollections.observableArrayList();
        refreshAllData();
    }

    public void refreshAllData() {
        if (newVersionAvailable()) {
            refreshCache();
        } else {
            loadFromCache();
        }
        userServantList.addAll(createAssociatedUserServantList());
        inventory = createInventoryWithAssociatedMatList();
        plannerServantList.addAll(createAssociatedPlannerServantList());
        servantNameList.addAll(servantDataList.stream().map(Servant::getName).collect(Collectors.toList()));
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
        return userServantToViewTransformer.transformAll(userServants);
    }

    private List<PlannerServantView> createAssociatedPlannerServantList() {
        List<PlannerServant> plannerServants = fileService.loadPlannedServantData();
        plannerServants.forEach(svt -> {
            if (svt.getSvtId() != 0L) {
                svt.setBaseServant(userServantToViewTransformer.transform(findUserServantById(svt.getSvtId())));
            }
        });
        return plannerServantToViewTransformer.transformAll(plannerServants);
    }

    private void loadFromCache() {
        servantDataList = fileService.loadFullServantData();
        materials = fileService.loadMaterialData();
        iconsResized = true;
        CLASS_ATTACK_MULTIPLIER = fileService.getClassAttackRate();
        CARD_DATA = fileService.getCardData();
    }

    private void refreshCache() {
        downloadNewData();
        saveNewDataToCache();
    }

    private void saveNewDataToCache() {
        fileService.saveFullServantData(servantDataList);
        fileService.saveClassAttackRate(CLASS_ATTACK_MULTIPLIER);
        fileService.saveCardData(CARD_DATA);
        fileService.saveNewVersion(currentVersion);
    }

    public void saveMaterialData() {
        fileService.saveMaterialData(materials);
    }

    private void downloadNewData() {
        servantDataList = requestService.getAllServantData();
        materials = requestService.getAllMaterialData();
        materials.forEach(material -> material.setIconImage(requestService.getImageForMaterial(material)));
        CLASS_ATTACK_MULTIPLIER = requestService.getClassAttackRate();
        CARD_DATA = requestService.getCardDetails();
    }

    private boolean newVersionAvailable() {
        currentVersion = fileService.getCurrentVersion();
        long onlineVersion = requestService.getOnlineVersion();
        boolean needUpdate = false;
        if (onlineVersion > currentVersion) {
            needUpdate = true;
            currentVersion = onlineVersion;
        }
        return needUpdate;
    }

    private Servant findServantById(long svtId) {
        return servantDataList.stream().filter(svt -> svtId == svt.getId()).findFirst().get();
    }

    public Servant findServantByName(String name) {
        return servantDataList.stream().filter(svt -> name.equalsIgnoreCase(svt.getName())).findFirst().orElse(null);
    }

    private UserServantView findUserServantById(long svtId) {
        return userServantList.stream().filter(svt -> svtId == svt.getSvtId().longValue()).findFirst().get();
    }

    public UserServantView findUserServantByName(String name) {
        return userServantList.stream()
                .filter(svt -> svt.getBaseServant().getValue() != null)
                .filter(svt -> name.equalsIgnoreCase(svt.getBaseServant().getValue().getName()))
                .findFirst().orElse(null);
    }

    public List<String> importUserServantsFromCsv(File sourceFile) {
        List<ManagerServant> managerLookup = fileService.loadManagerLookupTable();
        List<String[]> importedData = fileService.importCsv(sourceFile);
        List<UserServant> importedServants = importedData.stream().map(importedData1 -> buildUserServantFromStringArray(importedData1, managerLookup)).collect(Collectors.toList());
        List<String> notFoundNames = importedServants.stream()
                .filter(svt -> svt.getBaseServant() != null && svt.getSvtId() == 0)
                .map(svt -> svt.getBaseServant().getName())
                .collect(Collectors.toList());
        importedServants = importedServants.stream().filter(svt -> svt.getBaseServant() == null || svt.getSvtId() != 0).collect(Collectors.toList());
        ObservableList<UserServantView> trasnformedServants = userServantToViewTransformer.transformAll(importedServants);
        clearUnnecessaryEmptyUserRows(trasnformedServants);
        userServantList.setAll(trasnformedServants);
        return notFoundNames;
    }

    private UserServant buildUserServantFromStringArray(String[] importedData, List<ManagerServant> managerLookup) {
        Map<String, String> processedData = processedUserServantData(importedData);
        UserServant servant = new UserServant();
        if (!processedData.get("name").isEmpty()) {
            Servant baseServant = findServantFromManager(processedData.get("name"), managerLookup);
            if (baseServant.getName() != null && !baseServant.getName().isEmpty()) {
                servant = new UserServantFactory().createUserServantFromBaseServant(baseServant);
                if (!processedData.get("npLevel").isEmpty()) {
                    servant.setNpLevel(convertToInt(processedData.get("npLevel").substring(2)));
                }
                if (!processedData.get("level").isEmpty()) {
                    servant.setLevel(convertToInt(processedData.get("level").substring(4)));
                }
                servant.setSkillLevel1(Math.max(convertToInt(processedData.get("skill1")), 1));
                servant.setSkillLevel2(Math.max(convertToInt(processedData.get("skill2")), 1));
                servant.setSkillLevel3(Math.max(convertToInt(processedData.get("skill3")), 1));
                servant.setFouHp(convertToInt(processedData.get("fouHp")));
                servant.setFouAtk(convertToInt(processedData.get("fouAtk")));
                servant.setBondLevel(convertToInt(processedData.get("bond")));
            } else {
                baseServant = new Servant();
                baseServant.setName(importedData[0]);
                servant = new UserServant();
                servant.setBaseServant(baseServant);
            }
        }
        return servant;
    }

    private int convertToInt(String data) {
        return data != null && !data.isEmpty() ? Integer.parseInt(data) : 0;
    }

    private Servant findServantFromManager(String name, List<ManagerServant> managerLookup) {
        ManagerServant managerServant = managerLookup.stream().filter(svt -> name.equalsIgnoreCase(svt.getName())).findFirst().get();
        return servantDataList.stream().filter(svt -> svt.getCollectionNo() == managerServant.getCollectionNo()).findFirst().orElse(new Servant());
    }

    private Map<String, String> processedUserServantData(String[] importedData) {
        Map<String, String> processedData = new HashMap<>();
        processedData.put("name", importedData[0]);
        processedData.put("npLevel", importedData[14]);
        processedData.put("level", importedData[15]);
        processedData.put("skill1", importedData[16]);
        processedData.put("skill2", importedData[17]);
        processedData.put("skill3", importedData[18]);
        processedData.put("fouHp", importedData[19]);
        processedData.put("fouAtk", importedData[20]);
        processedData.put("bond", importedData[21]);
        return processedData;
    }

    public void eraseUserServant(UserServantView servant) {
        userServantList.set(userServantList.indexOf(servant), new UserServantView());
    }

    public void erasePlannerServant(PlannerServantView servant) {
        plannerServantList.set(plannerServantList.indexOf(servant), new PlannerServantView());
    }

    public void removeUserServant(UserServantView servant) {
        userServantList.remove(servant);
    }

    public void removePlannerServant(PlannerServantView servant) {
        plannerServantList.remove(servant);
    }

    public void savePlannerServant(PlannerServantView plannerServantView) {
        plannerServantList.add(plannerServantView);
    }

    public void savePlannerServant(int index, PlannerServantView plannerServantView) {
        plannerServantList.add(index, plannerServantView);
    }

    public void saveUserServant(UserServantView servant) {
        userServantList.add(servant);
    }

    public void saveUserServant(int index, UserServantView servant) {
        userServantList.add(index, servant);
    }

    public void replaceBaseServantInRow(int index, UserServantView servant, String newServantName) {
        Servant newBaseServant = findServantByName(newServantName);
        if (newBaseServant != null) {
            if (servant.getBaseServant() == null || servant.getBaseServant().getValue() == null) {
                userServantList.set(index, userServantToViewTransformer.transform(new UserServantFactory().createUserServantFromBaseServant(newBaseServant)));
            } else {
                servant.getSvtId().set(newBaseServant.getId());
                servant.getRarity().set(newBaseServant.getRarity());
                servant.getBaseServant().set(newBaseServant);
                userServantList.set(index, servant);
            }
        }
    }

    public void replaceBaseServantInPlannerRow(int index, PlannerServantView servant, String newServantName) {
        UserServantView newBaseServant = findUserServantByName(newServantName);
        if (newBaseServant != null) {
            if (servant.getBaseServant().getValue() == null) {
                plannerServantList.set(index, new PlannerServantViewFactory().createFromUserServant(newBaseServant));
            } else {
                servant.getSvtId().set(newBaseServant.getSvtId().longValue());
                servant.getBaseServant().set(newBaseServant);
                plannerServantList.set(index, servant);
            }
        }
    }

    public void saveUserState() {
        clearUnnecessaryEmptyUserRows(userServantList);
        clearUnnecessaryEmptyPlannerRows(plannerServantList);
        fileService.saveUserServants(userServantToViewTransformer.transformAll(userServantList));
        fileService.saveInventory(inventoryToViewTransformer.transform(inventory));
        fileService.savePlannerServants(plannerServantToViewTransformer.transformAllFromViews(plannerServantList));
    }

    private void clearUnnecessaryEmptyUserRows(List<UserServantView> servantList) {
        int index = servantList.size() - 1;
        while (!servantList.isEmpty() && servantList.get(index).getBaseServant().getValue() == null) {
            servantList.remove(index--);
        }
    }

    private void clearUnnecessaryEmptyPlannerRows(List<PlannerServantView> servantList) {
        int index = servantList.size() - 1;
        while (!servantList.isEmpty() && servantList.get(index).getBaseServant().getValue() == null) {
            servantList.remove(index--);
        }
    }
}
