package com.github.theintelligentone.fgotracker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.theintelligentone.fgotracker.domain.other.CardPlacementData;
import com.github.theintelligentone.fgotracker.domain.servant.Servant;
import com.github.theintelligentone.fgotracker.domain.servant.ServantOfUser;
import com.github.theintelligentone.fgotracker.domain.servant.UserServantFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataManagementService {
    public static Map<String, Integer> CLASS_ATTACK_MULTIPLIER;
    public static Map<String, Map<Integer, CardPlacementData>> CARD_DATA;
    private final DataRequestService requestService;
    private final FileManagementService fileService;

    private List<String> servantNameList;
    private List<Servant> servantDataList;
    @Getter
    private ObservableList<ServantOfUser> userServantList = FXCollections.observableArrayList();
    private long currentVersion;

    public DataManagementService() {
        ObjectMapper objectMapper = new ObjectMapper();
        this.requestService = new DataRequestService(objectMapper);
        this.fileService = new FileManagementService(objectMapper);
        initApp();
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
        fileService.saveUserServants(userServantList);
    }

    public ServantOfUser tempLoad() {
        Servant baseServant = servantDataList.stream().sorted((svt1, svt2) -> Comparator.<Integer>reverseOrder().compare(svt1.getName().length(), svt2.getName().length())).findFirst().get();
        return new UserServantFactory().createUserServantFromBaseServant(baseServant);
    }

    private void initApp() {
        userServantList = FXCollections.observableArrayList();
        Thread loadThread = new Thread(() -> {
            refreshAllData();
//            saveUserServant(tempLoad(),3);
        });
        loadThread.start();
    }

    public void refreshAllData() {
        if (newVersionAvailable()) {
            refreshCache();
        } else {
            loadFromCache();
        }
        userServantList.addAll(createAssociatedUserServantList());
        servantNameList = servantDataList.stream().map(Servant::getName).collect(Collectors.toList());
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
        servantDataList = requestService.getAllServantData();
        if (!servantDataList.isEmpty()) {
            fileService.saveFullServantData(servantDataList);
            fileService.saveNewVersion(currentVersion);
        }
        CLASS_ATTACK_MULTIPLIER = requestService.getClassAttackRate();
        CARD_DATA = requestService.getCardDetails();
        fileService.saveClassAttackRate(CLASS_ATTACK_MULTIPLIER);
        fileService.saveCardData(CARD_DATA);
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
