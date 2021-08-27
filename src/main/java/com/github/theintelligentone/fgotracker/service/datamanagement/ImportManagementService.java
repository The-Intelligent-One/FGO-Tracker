package com.github.theintelligentone.fgotracker.service.datamanagement;

import com.github.theintelligentone.fgotracker.domain.item.UpgradeMaterial;
import com.github.theintelligentone.fgotracker.domain.servant.ManagerServant;
import com.github.theintelligentone.fgotracker.domain.servant.Servant;
import com.github.theintelligentone.fgotracker.domain.servant.UserServant;
import com.github.theintelligentone.fgotracker.domain.servant.factory.PlannerServantViewFactory;
import com.github.theintelligentone.fgotracker.domain.servant.factory.UserServantFactory;
import com.github.theintelligentone.fgotracker.domain.view.InventoryView;
import com.github.theintelligentone.fgotracker.domain.view.PlannerServantView;
import com.github.theintelligentone.fgotracker.domain.view.UpgradeMaterialCostView;
import com.github.theintelligentone.fgotracker.domain.view.UserServantView;
import com.github.theintelligentone.fgotracker.service.filemanagement.FileManagementServiceFacade;
import com.github.theintelligentone.fgotracker.service.transformer.UserServantToViewTransformer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.theintelligentone.fgotracker.service.datamanagement.DataManagementServiceFacade.NAME_FORMAT;

public class ImportManagementService {
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
    private static Map<String, String> MAT_NAME_TRANSLATE_MAP;

    private final UserServantToViewTransformer userServantToViewTransformer;
    private final FileManagementServiceFacade fileServiceFacade;

    public ImportManagementService(
            FileManagementServiceFacade fileServiceFacade,
            UserServantToViewTransformer userServantToViewTransformer) {
        this.fileServiceFacade = fileServiceFacade;
        this.userServantToViewTransformer = userServantToViewTransformer;
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
        MAT_NAME_TRANSLATE_MAP.put("Crown", "光銀の冠");
        MAT_NAME_TRANSLATE_MAP.put("Shard", "煌星のカケラ");
        MAT_NAME_TRANSLATE_MAP.put("Vein", "神脈霊子");
        MAT_NAME_TRANSLATE_MAP.put("Fruit", "悠久の実");
        MAT_NAME_TRANSLATE_MAP.put("Thread", "虹の糸玉");
    }

    public List<String> createInventoryFromCsvLines(Map<String, Integer> importedData, List<UpgradeMaterial> materials,
                                                    InventoryView inventory) {
        List<String> notFoundNames = convertMaterialNamesToOnesFromDb(importedData, materials);
        setCurrentAmountForMaterialsInInventory(importedData, inventory);
        return notFoundNames;
    }

    public List<String> createPlannerServantListFromCsvLines(List<UserServantView> userServants, List<Servant> allServants,
                                                             List<PlannerServantView> importedServants, File sourceFile) {
        List<ManagerServant> managerLookup = fileServiceFacade.loadManagerLookupTable();
        List<String[]> lines = fileServiceFacade.importPlannerCsv(sourceFile);
        importedServants.addAll(lines.stream()
                .map(csvLine -> buildPlannerServantFromStringArray(csvLine, managerLookup, userServants, allServants))
                .collect(Collectors.toList()));
        List<String> notFoundNames = importedServants.stream()
                .filter(svt -> svt.baseServantProperty().getValue() != null && svt.svtIdProperty().getValue() == 0)
                .map(svt -> svt.baseServantProperty().getValue().baseServantProperty().getValue().getName())
                .collect(Collectors.toList());
        List<PlannerServantView> validServantList = importedServants.stream()
                .filter(svt -> svt.baseServantProperty().getValue() == null || svt.svtIdProperty().getValue() != 0)
                .collect(Collectors.toList());
        importedServants.clear();
        importedServants.addAll(validServantList);
        return notFoundNames;
    }

    private void setCurrentAmountForMaterialsInInventory(Map<String, Integer> importedData, InventoryView inventory) {
        for (UpgradeMaterialCostView mat : inventory.getInventory()) {
            Integer amount = importedData.get(mat.itemProperty().getValue().getName());
            if (amount != null) {
                mat.amountProperty().set(amount);
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

    private PlannerServantView buildPlannerServantFromStringArray(String[] importedData, List<ManagerServant> managerLookup,
                                                                  List<UserServantView> userServants, List<Servant> allServants) {
        String servantName = importedData[4];
        PlannerServantView servant = new PlannerServantView();
        if (!servantName.isEmpty()) {
            servant = loadDataForPotentialPlannerServant(importedData, managerLookup, userServants, allServants, servantName);
        }
        return servant;
    }

    private PlannerServantView loadDataForPotentialPlannerServant(String[] importedData, List<ManagerServant> managerLookup,
                                                                  List<UserServantView> userServants, List<Servant> allServants,
                                                                  String servantName) {
        PlannerServantView servant;
        Servant baseServant = findServantFromManager(servantName, managerLookup, allServants);
        UserServantView baseUserServant = findUserServantForPlannerImport(userServants,
                baseServant);
        servant = createPlannerServantWithFoundData(importedData, servantName, baseUserServant);
        return servant;
    }

    private PlannerServantView createPlannerServantWithFoundData(String[] importedData, String servantName,
                                                                 UserServantView baseUserServant) {
        PlannerServantView servant;
        if (baseUserServant == null) {
            servant = createBlankPlannerServantWithName(servantName);
        } else {
            servant = setupImportedPlannerServant(importedData, baseUserServant);
        }
        return servant;
    }

    private UserServantView findUserServantForPlannerImport(List<UserServantView> userServants, Servant baseServant) {
        UserServantView baseUserServant = null;
        if (baseServant.getName() != null && !baseServant.getName().isEmpty()) {
            baseUserServant = findUserServantByFormattedName(
                    String.format(NAME_FORMAT, baseServant.getName(), baseServant.getClassName()), userServants);
        }
        return baseUserServant;
    }

    private PlannerServantView setupImportedPlannerServant(String[] importedData, UserServantView baseUserServant) {
        PlannerServantView servant;
        servant = new PlannerServantViewFactory().createFromUserServant(baseUserServant);
        servant.desLevelProperty().set(Math.max(Math.min(convertToInt(importedData[9]), 100), 1));
        servant.desSkill1Property().set(Math.max(Math.min(convertToInt(importedData[10]), 10), 1));
        servant.desSkill2Property().set(Math.max(Math.min(convertToInt(importedData[11]), 10), 1));
        servant.desSkill3Property().set(Math.max(Math.min(convertToInt(importedData[12]), 10), 1));
        return servant;
    }

    private PlannerServantView createBlankPlannerServantWithName(String servantName) {
        Servant baseServant;
        PlannerServantView servant;
        UserServantView baseUserServant;
        baseUserServant = userServantToViewTransformer.transform(new UserServant());
        baseServant = new Servant();
        baseServant.setName(servantName);
        baseUserServant.baseServantProperty().set(baseServant);
        servant = new PlannerServantView();
        servant.baseServantProperty().set(baseUserServant);
        return servant;
    }

    private int convertToInt(String data) {
        return !(data == null || data.isEmpty()) ? Integer.parseInt(data) : 0;
    }

    private Servant findServantFromManager(String name, List<ManagerServant> managerLookup, List<Servant> servantDataList) {
        ManagerServant managerServant = managerLookup.stream().filter(
                svt -> name.equalsIgnoreCase(svt.getName())).findFirst().get();
        return servantDataList.stream().filter(
                svt -> svt.getCollectionNo() == managerServant.getCollectionNo()).findFirst().orElse(new Servant());
    }

    private UserServantView findUserServantByFormattedName(String name, List<UserServantView> userServantList) {
        return userServantList.stream()
                .filter(svt -> svt.baseServantProperty().getValue() != null)
                .filter(svt -> name.equalsIgnoreCase(String.format(NAME_FORMAT, svt.baseServantProperty().getValue().getName(),
                        svt.baseServantProperty().getValue().getClassName())))
                .findFirst().orElse(null);
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
                .findFirst().orElse("");
    }

    public List<String> importUserServantsFromCsv(File sourceFile, List<UserServantView> importedServants,
                                                  List<Servant> servantDataList) {
        List<ManagerServant> managerLookup = fileServiceFacade.loadManagerLookupTable();
        List<String[]> importedData = fileServiceFacade.importRosterCsv(sourceFile);
        List<UserServant> unprocessedServants = importedData.stream().map(
                csvLine -> buildUserServantFromStringArray(csvLine, managerLookup, servantDataList)).collect(
                Collectors.toList());
        List<String> notFoundNames = unprocessedServants.stream()
                .filter(svt -> svt.getBaseServant() != null && svt.getSvtId() == 0)
                .map(svt -> svt.getBaseServant().getName())
                .collect(Collectors.toList());
        importedServants.addAll(userServantToViewTransformer.transformAllToViews(unprocessedServants.stream().filter(
                svt -> svt.getBaseServant() == null || svt.getSvtId() != 0).collect(Collectors.toList())));
        return notFoundNames;
    }

    private UserServant buildUserServantFromStringArray(String[] importedData, List<ManagerServant> managerLookup,
                                                        List<Servant> servantDataList) {
        UserServant servant = new UserServant();
        String servantName = importedData[ROSTER_IMPORT_INDEX_MAP.get("name")];
        if (!servantName.isEmpty()) {
            Servant baseServant = findServantFromManager(servantName, managerLookup, servantDataList);
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
}
