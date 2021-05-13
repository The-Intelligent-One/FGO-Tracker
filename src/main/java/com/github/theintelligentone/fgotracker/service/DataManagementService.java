package com.github.theintelligentone.fgotracker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.theintelligentone.fgotracker.domain.other.CardPlacementData;
import com.github.theintelligentone.fgotracker.domain.servant.Servant;
import com.github.theintelligentone.fgotracker.domain.servant.ServantFromManager;
import com.github.theintelligentone.fgotracker.domain.servant.ServantOfUser;
import com.github.theintelligentone.fgotracker.domain.servant.UserServantFactory;
import com.github.theintelligentone.fgotracker.domain.servant.propertyobjects.UpgradeMaterial;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataManagementService {
    public static final int[] MAX_LEVELS = {65, 60, 65, 70, 80, 90};
    public static Map<String, Integer> CLASS_ATTACK_MULTIPLIER;
    public static Map<String, Map<Integer, CardPlacementData>> CARD_DATA;
    private final DataRequestService requestService;
    private final FileManagementService fileService;
    private ObservableList<String> servantNameList = FXCollections.observableArrayList();
    private List<Servant> servantDataList;
    private List<UpgradeMaterial> materials;
    private List<ServantFromManager> managerLookup;
    @Getter
    private ObservableList<ServantOfUser> userServantList = FXCollections.observableArrayList();
    private long currentVersion;

    public DataManagementService() {
        ObjectMapper objectMapper = new ObjectMapper();
        this.requestService = new DataRequestService(objectMapper);
        this.fileService = new FileManagementService(objectMapper);
    }

    public ObservableList<String> getServantNameList() {
        return servantNameList;
    }

    public List<String> importFromCsv(File sourceFile) {
        managerLookup = fileService.loadManagerLookupTable();
        List<String[]> importedData = fileService.importCsv(sourceFile);
        List<ServantOfUser> importedServants = importedData.stream().map(this::buildUserServantFromStringArray).collect(Collectors.toList());
        List<String> notFoundNames = importedServants.stream()
                .filter(svt -> svt != null && svt.getNpTarget() == null)
                .map(svt -> svt.getBaseServant().getName())
                .collect(Collectors.toList());
        importedServants = importedServants.stream().filter(svt -> svt == null || svt.getNpTarget() != null).collect(Collectors.toList());
        userServantList.addAll(importedServants);
        return notFoundNames;
    }

    private ServantOfUser buildUserServantFromStringArray(String[] importedData) {
        Map<String, String> filteredData = filterData(importedData);
        ServantOfUser servant = null;
        if (!filteredData.get("name").isEmpty()) {
            Servant baseServant = findServantFromManager(filteredData.get("name"));
            if (baseServant.getName() != null && !baseServant.getName().isEmpty()) {
                servant = new UserServantFactory().createUserServantFromBaseServant(baseServant);
                if (!filteredData.get("npLevel").isEmpty()) {
                    servant.setNpLevel(convertToInt(filteredData.get("npLevel").substring(2)));
                }
                if (!filteredData.get("level").isEmpty()) {
                    servant.setLevel(convertToInt(filteredData.get("level").substring(4)));
                }
                servant.setSkillLevel1(convertToInt(filteredData.get("skill1")));
                servant.setSkillLevel2(convertToInt(filteredData.get("skill2")));
                servant.setSkillLevel3(convertToInt(filteredData.get("skill3")));
                servant.setFouHp(convertToInt(filteredData.get("fouHp")));
                servant.setFouAtk(convertToInt(filteredData.get("fouAtk")));
                servant.setBondLevel(convertToInt(filteredData.get("bond")));
            } else {
                baseServant = new Servant();
                baseServant.setName(importedData[0]);
                servant = new ServantOfUser();
                servant.setBaseServant(baseServant);
            }
        }
        return servant;
    }

    private int convertToInt(String data) {
        int result = 0;
        if (data != null && !data.isEmpty()) {
            result = Integer.parseInt(data);
        }
        return result;
    }

    private Servant findServantFromManager(String name) {
        ServantFromManager servantFromManager = managerLookup.stream().filter(svt -> name.equalsIgnoreCase(svt.getName())).findFirst().get();
        return servantDataList.stream().filter(svt -> svt.getCollectionNo() == servantFromManager.getCollectionNo()).findFirst().orElse(new Servant());
    }

    public boolean isDataLoaded() {
        return servantDataList != null && !servantDataList.isEmpty();
    }

    private Map<String, String> filterData(String[] importedData) {
        Map<String, String> filteredData = new HashMap<>();
        filteredData.put("name", importedData[0]);
        filteredData.put("npLevel", importedData[14]);
        filteredData.put("level", importedData[15]);
        filteredData.put("skill1", importedData[16]);
        filteredData.put("skill2", importedData[17]);
        filteredData.put("skill3", importedData[18]);
        filteredData.put("fouHp", importedData[19]);
        filteredData.put("fouAtk", importedData[20]);
        filteredData.put("bond", importedData[21]);
        return filteredData;
    }

    public void saveUserServant(ServantOfUser servant, int index) {
        while (userServantList.size() <= index) {
            userServantList.add(null);
        }
        userServantList.set(index, servant);
    }

    public void saveUserServant(ServantOfUser servant) {
        userServantList.add(servant);
    }

    public void saveUserState() {
        clearUnnecessaryEmptyRows();
        fileService.saveUserServants(userServantList);
    }

    private void clearUnnecessaryEmptyRows() {
        int index = userServantList.size() - 1;
        while (!userServantList.isEmpty() && userServantList.get(index) == null) {
            userServantList.remove(index--);
        }
    }

    public ServantOfUser tempLoad() {
        Servant baseServant = servantDataList.stream().sorted((svt1, svt2) -> Comparator.<Integer>reverseOrder().compare(svt1.getName().length(), svt2.getName().length())).findFirst().get();
        return new UserServantFactory().createUserServantFromBaseServant(baseServant);
    }

    public void initApp() {
        userServantList = FXCollections.observableArrayList();
        refreshAllData();
//            saveUserServant(tempLoad(),3);
    }

    public void refreshAllData() {
        if (newVersionAvailable()) {
            refreshCache();
        } else {
            loadFromCache();
        }
        userServantList.addAll(createAssociatedUserServantList());
        servantNameList.addAll(servantDataList.stream().map(Servant::getName).collect(Collectors.toList()));
    }

    private List<ServantOfUser> createAssociatedUserServantList() {
        List<ServantOfUser> userServants = fileService.loadUserData();
        userServants.forEach(svt -> {
            if (svt != null) {
                svt.setBaseServant(findServantById(svt.getSvtId()));
            }
        });
        return userServants;
    }

    private Servant findServantById(long svtId) {
        return servantDataList.stream().filter(svt -> svtId == svt.getId()).findFirst().get();
    }

    private void loadFromCache() {
        servantDataList = fileService.loadFullServantData();
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
        fileService.saveMaterialData(materials);
        fileService.saveCardData(CARD_DATA);
        fileService.saveNewVersion(currentVersion);
    }

    private void downloadNewData() {
        servantDataList = requestService.getAllServantData();
        materials = requestService.getAllMaterialData();
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

    public List<String> getAllServantNames() {
        return servantNameList;
    }

    public Servant getServantByName(String name) {
        return servantDataList.stream().filter(svt -> name.equalsIgnoreCase(svt.getName())).findFirst().orElse(null);
    }
}
