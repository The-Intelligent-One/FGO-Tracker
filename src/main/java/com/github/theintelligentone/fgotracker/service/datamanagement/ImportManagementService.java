package com.github.theintelligentone.fgotracker.service.datamanagement;

import com.github.theintelligentone.fgotracker.domain.item.Inventory;
import com.github.theintelligentone.fgotracker.domain.item.UpgradeMaterial;
import com.github.theintelligentone.fgotracker.domain.item.UpgradeMaterialCost;
import com.github.theintelligentone.fgotracker.domain.servant.BasicServant;
import com.github.theintelligentone.fgotracker.domain.servant.ManagerServant;
import com.github.theintelligentone.fgotracker.domain.servant.Servant;
import com.github.theintelligentone.fgotracker.domain.servant.UserServant;
import com.github.theintelligentone.fgotracker.domain.servant.factory.UserServantFactory;
import com.github.theintelligentone.fgotracker.service.filemanagement.FileManagementServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ImportManagementService {
    private static final Map<String, Integer> ROSTER_IMPORT_INDEX_MAP = Map.of("name", 0, "npLevel", 14, "level", 15, "skill1", 16, "skill2", 17, "skill3", 18, "fouHp", 19, "fouAtk", 20, "bond", 21, "notes", 23);
    private static final Map<String, Integer> PLANNER_IMPORT_INDEX_MAP = Map.of("name", 4, "desLevel", 9, "desSkill1", 10, "desSkill2", 11, "desSkill3", 12);
    private static Map<String, String> MAT_NAME_TRANSLATE_MAP;

    @Autowired
    private FileManagementServiceFacade fileServiceFacade;

    @Autowired
    public ImportManagementService() {
        setupMatTranslateMap();
    }

    private void setupMatTranslateMap() {
        MAT_NAME_TRANSLATE_MAP = new HashMap<>();
        MAT_NAME_TRANSLATE_MAP.put("blue", "gem");
        MAT_NAME_TRANSLATE_MAP.put("red", "magic gem");
        MAT_NAME_TRANSLATE_MAP.put("gold", "secret gem");
        MAT_NAME_TRANSLATE_MAP.put("Permafrost", "ice");
        MAT_NAME_TRANSLATE_MAP.put("Seashell", "shell");
        MAT_NAME_TRANSLATE_MAP.put("Stinger", "needle");
        MAT_NAME_TRANSLATE_MAP.put("Shard", "twinkling");
        MAT_NAME_TRANSLATE_MAP.put("Vein", "leyline");
        MAT_NAME_TRANSLATE_MAP.put("Tiny Bell", "small bell");
        MAT_NAME_TRANSLATE_MAP.put("Thread", "虹の糸玉");
        MAT_NAME_TRANSLATE_MAP.put("Flame", "虹の糸玉");
    }

    public List<String> createInventoryFromCsvLines(File sourceFile, List<UpgradeMaterial> materials, Inventory inventory) {
        Map<String, Integer> importedData = fileServiceFacade.importInventoryCsv(sourceFile);
        List<String> notFoundNames = convertMaterialNamesToOnesFromDb(importedData, materials);
        setCurrentAmountForMaterialsInInventory(importedData, inventory);
        return notFoundNames;
    }

    public List<String> createPlannerServantListFromCsvLines(List<UserServant> importedServants, List<BasicServant> basicServants, File sourceFile) {
        List<ManagerServant> managerLookup = fileServiceFacade.loadManagerLookupTable();
        List<String[]> lines = fileServiceFacade.importPlannerCsv(sourceFile);
        List<UserServant> unprocessedServants = lines.stream()
                .map(csvLine -> buildPlannerServantFromStringArray(csvLine, managerLookup, basicServants))
                .collect(Collectors.toList());
        List<String> notFoundNames = unprocessedServants.stream()
                .filter(svt -> svt.getSvtId() == 0 && svt.getBaseServant() != null)
                .map(svt -> svt.getBaseServant().getName())
                .collect(Collectors.toList());
        importedServants.addAll(unprocessedServants.stream()
                .filter(svt -> svt.getBaseServant() == null || svt.getSvtId() != 0)
                .collect(Collectors.toList()));
        return notFoundNames;
    }

    private void setCurrentAmountForMaterialsInInventory(Map<String, Integer> importedData, Inventory inventory) {
        for (UpgradeMaterialCost mat : inventory.getInventory()) {
            Integer amount = importedData.get(mat.getItem().getName());
            if (amount != null) {
                mat.setAmount(amount);
            }
        }
    }

    private List<String> convertMaterialNamesToOnesFromDb(Map<String, Integer> importedData, List<UpgradeMaterial> materials) {
        List<String> notFoundNames = new ArrayList<>();
        Map<String, Integer> tempMap = new HashMap<>();
        importedData.forEach((s, integer) -> {
            String matName = findMaterialName(s, materials);
            if (matName.isEmpty()) {
                notFoundNames.add(s);
            } else {
                tempMap.put(matName, integer);
            }
        });
        importedData.clear();
        importedData.putAll(tempMap);
        return notFoundNames;
    }

    private UserServant buildPlannerServantFromStringArray(String[] importedData, List<ManagerServant> managerLookup, List<BasicServant> basicServants) {
        String servantName = importedData[PLANNER_IMPORT_INDEX_MAP.get("name")];
        UserServant servant = UserServantFactory.createBlankUserServant();
        if (!servantName.isEmpty()) {
            Servant baseServant = findServantFromManager(servantName, managerLookup, basicServants);
            if (baseServant.getId() == 0) {
                servant.setBaseServant(baseServant);
            } else {
                servant = createValidPlannerServant(importedData, baseServant);
            }
        }
        return servant;
    }

    private UserServant createValidPlannerServant(String[] importedData, Servant baseServant) {
        UserServant servant;
        servant = UserServantFactory.createBlankUserServant();
        servant.setSvtId(baseServant.getId());
        servant.setDesLevel(Math.max(Math.min(convertToInt(importedData[PLANNER_IMPORT_INDEX_MAP.get("desLevel")]), 120), 1));
        servant.setDesSkill1(Math.max(Math.min(convertToInt(importedData[PLANNER_IMPORT_INDEX_MAP.get("desSkill1")]), 10), 1));
        servant.setDesSkill2(Math.max(Math.min(convertToInt(importedData[PLANNER_IMPORT_INDEX_MAP.get("desSkill2")]), 10), 1));
        servant.setDesSkill3(Math.max(Math.min(convertToInt(importedData[PLANNER_IMPORT_INDEX_MAP.get("desSkill3")]), 10), 1));
        return servant;
    }

    private int convertToInt(String data) {
        return !(data == null || data.isEmpty()) ? Integer.parseInt(data) : 0;
    }

    private Servant findServantFromManager(String name, List<ManagerServant> managerLookup, List<BasicServant> servantDataList) {
        ManagerServant managerServant = managerLookup.stream()
                .filter(svt -> name.equalsIgnoreCase(svt.getName()))
                .findFirst()
                .get();
        BasicServant basicServant = servantDataList.stream()
                .filter(svt -> svt.getCollectionNo() == managerServant.getCollectionNo())
                .findFirst()
                .orElseGet(BasicServant::new);
        Servant tempServant = new Servant();
        tempServant.setId(basicServant.getId());
        tempServant.setName(basicServant.getName());
        return tempServant;
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

    private String findMaterialName(String matName, List<UpgradeMaterial> materials) {
        String[] normalizedMatName = normalizeMatName(matName);
        return materials.stream()
                .map(UpgradeMaterial::getName)
                .filter(name -> containsAll(normalizedMatName, name))
                .findFirst()
                .orElse(materials.stream()
                        .map(UpgradeMaterial::getName)
                        .filter(name -> name.contains(matName))
                        .findFirst()
                        .orElse(""));
    }

    public List<String> importUserServantsFromCsv(File sourceFile, List<UserServant> importedServants, List<BasicServant> servantDataList) {
        List<ManagerServant> managerLookup = fileServiceFacade.loadManagerLookupTable();
        List<String[]> importedData = fileServiceFacade.importRosterCsv(sourceFile);
        List<UserServant> unprocessedServants = importedData.stream()
                .map(csvLine -> buildUserServantFromStringArray(csvLine, managerLookup, servantDataList))
                .collect(Collectors.toList());
        List<String> notFoundNames = unprocessedServants.stream()
                .filter(svt -> svt.getSvtId() == 0 && svt.getBaseServant() != null)
                .map(svt -> svt.getBaseServant().getName())
                .collect(Collectors.toList());
        importedServants.addAll(unprocessedServants.stream()
                .filter(svt -> svt.getBaseServant() == null || svt.getSvtId() != 0)
                .collect(Collectors.toList()));
        return notFoundNames;
    }

    private UserServant buildUserServantFromStringArray(String[] importedData, List<ManagerServant> managerLookup, List<BasicServant> servantDataList) {
        UserServant servant = UserServantFactory.createBlankUserServant();
        String servantName = importedData[ROSTER_IMPORT_INDEX_MAP.get("name")];
        if (!servantName.isEmpty()) {
            Servant baseServant = findServantFromManager(servantName, managerLookup, servantDataList);
            if (baseServant.getId() == 0) {
                servant.setBaseServant(baseServant);
            } else {
                servant = createValidRosterServant(importedData, baseServant);
            }
        }
        return servant;
    }

    private UserServant createValidRosterServant(String[] importedData, Servant baseServant) {
        UserServant servant;
        servant = UserServantFactory.createBlankUserServant();
        servant.setSvtId(baseServant.getId());
        servant.setNpLevel(getValueFromImportedRosterData(importedData, "npLevel", 1, 5));
        servant.setLevel(getValueFromImportedRosterData(importedData, "level", 1, 120));
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
}
