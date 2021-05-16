package com.github.theintelligentone.fgotracker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.theintelligentone.fgotracker.domain.item.UpgradeMaterial;
import com.github.theintelligentone.fgotracker.domain.other.CardPlacementData;
import com.github.theintelligentone.fgotracker.domain.servant.ManagerServant;
import com.github.theintelligentone.fgotracker.domain.servant.Servant;
import com.github.theintelligentone.fgotracker.domain.servant.UserServant;
import com.github.theintelligentone.fgotracker.domain.servant.factory.UserServantFactory;
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
    private boolean iconsResized = false;
    @Getter
    private ObservableList<UserServant> userServantList = FXCollections.observableArrayList();
    private long currentVersion;

    public DataManagementService() {
        ObjectMapper objectMapper = new ObjectMapper();
        this.requestService = new DataRequestService(objectMapper);
        this.fileService = new FileManagementService(objectMapper);
    }

    public List<String> getAllServantNames() {
        return servantNameList;
    }

    public Servant findServantByName(String name) {
        return servantDataList.stream().filter(svt -> name.equalsIgnoreCase(svt.getName())).findFirst().orElse(null);
    }

    public ObservableList<String> getServantNameList() {
        return servantNameList;
    }

    public List<UpgradeMaterial> getAllMaterials() {
        return materials;
    }

    public boolean isDataLoaded() {
        return servantDataList != null && !servantDataList.isEmpty();
    }

    public boolean isIconsResized() {
        return iconsResized;
    }

    public void initApp() {
        userServantList = FXCollections.observableArrayList();
        refreshAllData();
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

    private List<UserServant> createAssociatedUserServantList() {
        List<UserServant> userServants = fileService.loadUserData();
        userServants.forEach(svt -> {
            if (svt.getSvtId() != 0l) {
                svt.setBaseServant(findServantById(svt.getSvtId()));
            }
        });
        return userServants;
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

    public List<String> importFromCsv(File sourceFile) {
        List<ManagerServant> managerLookup = fileService.loadManagerLookupTable();
        List<String[]> importedData = fileService.importCsv(sourceFile);
        List<UserServant> importedServants = importedData.stream().map(importedData1 -> buildUserServantFromStringArray(importedData1, managerLookup)).collect(Collectors.toList());
        List<String> notFoundNames = importedServants.stream()
                .filter(svt -> svt.getBaseServant() != null && svt.getNpTarget() == null)
                .map(svt -> svt.getBaseServant().getName())
                .collect(Collectors.toList());
        importedServants = importedServants.stream().filter(svt -> svt.getBaseServant() == null || svt.getNpTarget() != null).collect(Collectors.toList());
        clearUnnecessaryEmptyRows(importedServants);
        userServantList.setAll(importedServants);
        return notFoundNames;
    }

    private UserServant buildUserServantFromStringArray(String[] importedData, List<ManagerServant> managerLookup) {
        Map<String, String> filteredData = filterData(importedData);
        UserServant servant = new UserServant();
        if (!filteredData.get("name").isEmpty()) {
            Servant baseServant = findServantFromManager(filteredData.get("name"), managerLookup);
            if (baseServant.getName() != null && !baseServant.getName().isEmpty()) {
                servant = new UserServantFactory().createUserServantFromBaseServant(baseServant);
                if (!filteredData.get("npLevel").isEmpty()) {
                    servant.setNpLevel(convertToInt(filteredData.get("npLevel").substring(2)));
                }
                if (!filteredData.get("level").isEmpty()) {
                    servant.setLevel(convertToInt(filteredData.get("level").substring(4)));
                }
                servant.setSkillLevel1(Math.max(convertToInt(filteredData.get("skill1")), 1));
                servant.setSkillLevel2(Math.max(convertToInt(filteredData.get("skill2")), 1));
                servant.setSkillLevel3(Math.max(convertToInt(filteredData.get("skill3")), 1));
                servant.setFouHp(convertToInt(filteredData.get("fouHp")));
                servant.setFouAtk(convertToInt(filteredData.get("fouAtk")));
                servant.setBondLevel(convertToInt(filteredData.get("bond")));
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
        int result = 0;
        if (data != null && !data.isEmpty()) {
            result = Integer.parseInt(data);
        }
        return result;
    }

    private Servant findServantFromManager(String name, List<ManagerServant> managerLookup) {
        ManagerServant managerServant = managerLookup.stream().filter(svt -> name.equalsIgnoreCase(svt.getName())).findFirst().get();
        return servantDataList.stream().filter(svt -> svt.getCollectionNo() == managerServant.getCollectionNo()).findFirst().orElse(new Servant());
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

    public void eraseUserServant(UserServant servant) {
        userServantList.set(userServantList.indexOf(servant), new UserServant());
    }

    public void replaceBaseServantInRow(int index, UserServant servant, String newServantName) {
        UserServant modifiedServant = new UserServantFactory().replaceBaseServant(servant, findServantByName(newServantName));
        userServantList.set(index, modifiedServant);
    }

    public void saveUserServant(UserServant servant) {
        userServantList.add(servant);
    }

    public void saveUserState() {
        clearUnnecessaryEmptyRows(userServantList);
        fileService.saveUserServants(userServantList);
    }

    private void clearUnnecessaryEmptyRows(List<UserServant> servantList) {
        int index = servantList.size() - 1;
        while (!servantList.isEmpty() && servantList.get(index).getBaseServant() == null) {
            servantList.remove(index--);
        }
    }
}
