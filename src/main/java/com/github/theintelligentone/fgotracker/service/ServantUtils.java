package com.github.theintelligentone.fgotracker.service;

import com.github.theintelligentone.fgotracker.domain.item.UpgradeMaterialCost;
import com.github.theintelligentone.fgotracker.domain.servant.Servant;
import com.github.theintelligentone.fgotracker.domain.servant.propertyobjects.FgoFunction;
import com.github.theintelligentone.fgotracker.domain.view.PlannerServantView;
import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableIntegerValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ServantUtils {
    private static final int[] MAX_LEVELS = {65, 60, 65, 70, 80, 90};

    private ServantUtils() {    }

    public static List<Integer> createListOfAscensionLevels(int rarity) {
        int maxLevel = MAX_LEVELS[rarity];
        List<Integer> levelsWithAscension = new ArrayList<>();
        IntStream.range(1, 5).forEach(index -> levelsWithAscension.add(maxLevel - index * 10));
        for (int ascLevel = maxLevel; ascLevel <= 90; ascLevel += 5) {
            levelsWithAscension.add(ascLevel);
        }
        for (int ascLevel = 90; ascLevel <= 100; ascLevel += 2) {
            levelsWithAscension.add(ascLevel);
        }
        return levelsWithAscension.stream().distinct().sorted().collect(Collectors.toList());
    }

    public static ObservableIntegerValue getAscensionFromRarityAndLevel(ObservableIntegerValue level, int rarity) {
        List<Integer> listOfAscensionLevels = createListOfAscensionLevels(rarity);
        int nextAscLvl = listOfAscensionLevels.stream().dropWhile(lvl -> lvl < level.intValue()).findFirst().get();
        IntegerProperty ascLevel = new SimpleIntegerProperty(listOfAscensionLevels.indexOf(nextAscLvl));
        level.addListener((observable, oldValue, newValue) -> {
            int nextAscensionLvl = listOfAscensionLevels.stream().dropWhile(lvl -> lvl < level.intValue()).findFirst().get();
            ascLevel.set(listOfAscensionLevels.indexOf(nextAscensionLvl));
        });
        return ascLevel;
    }

    public static ObservableIntegerValue getPlannedMatUse(PlannerServantView servant, long matId) {
        ObservableIntegerValue ascMats = sumNeededAscensionMats(servant, matId);
        ObservableIntegerValue skillMats = sumAllNeededSkillMats(servant, matId);
        IntegerProperty matSum = new SimpleIntegerProperty(ascMats.intValue() + skillMats.intValue());
        ObservableList<ObservableIntegerValue> neededValues = FXCollections.observableArrayList(param -> new Observable[]{param});
        neededValues.add(ascMats);
        neededValues.add(skillMats);
        neededValues.addListener(
                (ListChangeListener<? super ObservableIntegerValue>) c -> matSum.set(ascMats.intValue() + skillMats.intValue()));
        return matSum;
    }

    private static ObservableIntegerValue sumAllNeededSkillMats(PlannerServantView servant, long matId) {
        IntegerProperty result = new SimpleIntegerProperty(0);
        return (ObservableIntegerValue) result.add(
                sumNeededSkillMats(servant, servant.baseServantProperty().getValue().skillLevel1Property(),
                        servant.desSkill1Property(), matId))
                .add(sumNeededSkillMats(servant, servant.baseServantProperty().getValue().skillLevel2Property(),
                        servant.desSkill2Property(),
                        matId))
                .add(sumNeededSkillMats(servant, servant.baseServantProperty().getValue().skillLevel3Property(),
                        servant.desSkill3Property(),
                        matId));
    }

    private static ObservableIntegerValue sumNeededSkillMats(PlannerServantView servant, ObservableIntegerValue currentLevel,
                                                      ObservableIntegerValue desiredLevel, long matId) {
        ObservableList<ObservableIntegerValue> neededValues = FXCollections.observableArrayList(param -> new Observable[]{param});
        neededValues.add(currentLevel);
        neededValues.add(desiredLevel);
        IntegerProperty skillMatSum = new SimpleIntegerProperty(
                calculateSkillMat(servant, matId, currentLevel.intValue(), desiredLevel.intValue()));
        neededValues.addListener((ListChangeListener<? super ObservableIntegerValue>) c -> skillMatSum.set(
                calculateSkillMat(servant, matId, c.getList().get(0).intValue(), c.getList().get(1).intValue())));
        return skillMatSum;
    }

    private static int calculateSkillMat(PlannerServantView servant, long matId, int currentLevel, int desiredLevel) {
        return servant.getSkillMaterials().stream()
                .skip(Math.max(currentLevel - 1, 0)).limit(Math.max(desiredLevel - currentLevel, 0))
                .flatMap(mat -> mat.getItems().stream())
                .filter(mat -> matId == mat.getItem().getId())
                .mapToInt(UpgradeMaterialCost::getAmount)
                .reduce(Integer::sum).orElse(0);
    }

    private static ObservableIntegerValue sumNeededAscensionMats(PlannerServantView servant, long matId) {
        ObservableIntegerValue currentAscensionLevel = getAscensionFromRarityAndLevel(
                servant.baseServantProperty().getValue().levelProperty(),
                servant.baseServantProperty().getValue().baseServantProperty().getValue().getRarity());
        ObservableIntegerValue desiredAscensionLevel = getAscensionFromRarityAndLevel(servant.desLevelProperty(),
                servant.baseServantProperty().getValue().baseServantProperty().getValue().getRarity());
        ObservableList<ObservableIntegerValue> neededValues = FXCollections.observableArrayList(param -> new Observable[]{param});
        neededValues.add(currentAscensionLevel);
        neededValues.add(desiredAscensionLevel);
        IntegerProperty ascMatSum = new SimpleIntegerProperty(
                calculateAscMats(servant, matId, currentAscensionLevel.intValue(), desiredAscensionLevel.intValue()));
        neededValues.addListener((ListChangeListener<? super ObservableIntegerValue>) c -> ascMatSum.set(
                calculateAscMats(servant, matId, c.getList().get(0).intValue(), c.getList().get(1).intValue())));
        return ascMatSum;
    }

    private static int calculateAscMats(PlannerServantView servant, long matId, int currentAscLevel, int desiredAscLevel) {
        return servant.getAscensionMaterials().stream()
                .skip(currentAscLevel).limit(Math.max(desiredAscLevel - currentAscLevel, 0))
                .flatMap(mat -> mat.getItems().stream())
                .filter(mat -> matId == mat.getItem().getId())
                .mapToInt(UpgradeMaterialCost::getAmount)
                .reduce(Integer::sum).orElse(0);
    }

    public static ObservableIntegerValue sumNeededAscensionGrails(PlannerServantView servant) {
        IntegerProperty currentLevel = servant.baseServantProperty().getValue().levelProperty();
        IntegerProperty desiredLevel = servant.desLevelProperty();
        IntegerProperty neededGrails = new SimpleIntegerProperty(
                calculateNeededGrails(servant, servant.baseServantProperty().getValue().levelProperty(),
                        servant.desLevelProperty()));
        ObservableList<Property<?>> neededValues = FXCollections.observableArrayList(param -> new Observable[]{param});
        neededValues.add(currentLevel);
        neededValues.add(desiredLevel);
        neededValues.add(servant.baseServantProperty().getValue().ascensionProperty());
        neededValues.addListener((ListChangeListener<? super Property<?>>) observable -> {
            int plannedGrails = calculateNeededGrails(servant, currentLevel, desiredLevel);
            neededGrails.set(plannedGrails);
        });
        return neededGrails;
    }

    private static int calculateNeededGrails(PlannerServantView servant, ObservableIntegerValue currentLevel,
                                      ObservableIntegerValue desiredLevel) {
        int currentAscLevel = Math.max(getAscensionFromRarityAndLevel(currentLevel,
                servant.baseServantProperty().getValue().baseServantProperty().getValue().getRarity()).intValue() - 4, 0);
        if (servant.baseServantProperty().getValue().ascensionProperty().getValue()) {
            currentAscLevel++;
        }
        int desiredAscLevel = Math.max(getAscensionFromRarityAndLevel(desiredLevel,
                servant.baseServantProperty().getValue().baseServantProperty().getValue().getRarity()).intValue() - 4, 0);
        return Math.max(desiredAscLevel - currentAscLevel, 0);
    }

    public static String determineNpCard(Servant baseServant) {
        String card = baseServant.getNoblePhantasms().get(baseServant.getNoblePhantasms().size() - 1).getCard();
        return card.substring(0, 1).toUpperCase() + card.substring(1);
    }

    public static String determineNpTarget(Servant baseServant) {
        String target = "Support";
        FgoFunction damageNp = findDamagingNpFunction(baseServant);
        if (damageNp != null) {
            target = damageIsAoE(damageNp) ? "AoE" : "ST";
        }
        return target;
    }

    private static boolean damageIsAoE(FgoFunction damageNp) {
        return "enemyAll".equalsIgnoreCase(damageNp.getFuncTargetType());
    }

    private static FgoFunction findDamagingNpFunction(Servant baseServant) {
        return baseServant.getNoblePhantasms().get(baseServant.getNoblePhantasms().size() - 1).getFunctions().stream()
                .filter(fnc -> fnc.getFuncType().startsWith("damageNp")).findFirst().orElse(null);
    }
}
