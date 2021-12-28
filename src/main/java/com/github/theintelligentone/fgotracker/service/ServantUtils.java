package com.github.theintelligentone.fgotracker.service;

import com.github.theintelligentone.fgotracker.domain.item.UpgradeMaterialCost;
import com.github.theintelligentone.fgotracker.domain.servant.Servant;
import com.github.theintelligentone.fgotracker.domain.servant.UserServant;
import com.github.theintelligentone.fgotracker.domain.servant.propertyobjects.FgoFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ServantUtils {
    private static final int[] MAX_LEVELS = {65, 60, 65, 70, 80, 90};

    private ServantUtils() {}

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

    public static int getDefaultValueIfInvalid(int value, int min, int max, int defaultValue) {
        int result = defaultValue;
        if (value <= max && value >= min) {
            result = value;
        }
        return result;
    }

    public static int getAscensionFromRarityAndLevel(int level, int rarity) {
        List<Integer> listOfAscensionLevels = createListOfAscensionLevels(rarity);
        int nextAscLvl = listOfAscensionLevels.stream().dropWhile(lvl -> lvl < level).findFirst().get();
        return listOfAscensionLevels.indexOf(nextAscLvl);
    }

    public static int getPlannedMatUse(UserServant servant, long matId) {
        return sumNeededAscensionMats(servant, matId) + sumAllNeededSkillMats(servant, matId);
    }

    private static int sumAllNeededSkillMats(UserServant servant, long matId) {
        return calculateSkillMat(servant, servant.getSkillLevel1(), servant.getDesSkill1(), matId) + calculateSkillMat(servant, servant.getSkillLevel2(), servant.getDesSkill2(), matId) + calculateSkillMat(servant, servant.getSkillLevel3(), servant.getDesSkill3(), matId);
    }

    private static int calculateSkillMat(UserServant servant, int currentLevel, int desiredLevel, long matId) {
        return servant.getSkillMaterials()
                .stream()
                .skip(Math.max(currentLevel - 1, 0))
                .limit(Math.max(desiredLevel - currentLevel, 0))
                .flatMap(mat -> mat.getItems().stream())
                .filter(mat -> matId == mat.getItem().getId())
                .mapToInt(UpgradeMaterialCost::getAmount)
                .reduce(Integer::sum)
                .orElse(0);
    }

    private static int sumNeededAscensionMats(UserServant servant, long matId) {
        int currentAscensionLevel = getAscensionFromRarityAndLevel(servant.getLevel(), servant.getBaseServant()
                .getRarity());
        int desiredAscensionLevel = getAscensionFromRarityAndLevel(servant.getDesLevel(), servant.getBaseServant()
                .getRarity());
        return calculateAscMats(servant, matId, currentAscensionLevel, desiredAscensionLevel);
    }

    private static int calculateAscMats(UserServant servant, long matId, int currentAscLevel, int desiredAscLevel) {
        return servant.getAscensionMaterials()
                .stream()
                .skip(currentAscLevel)
                .limit(Math.max(desiredAscLevel - currentAscLevel, 0))
                .flatMap(mat -> mat.getItems().stream())
                .filter(mat -> matId == mat.getItem().getId())
                .mapToInt(UpgradeMaterialCost::getAmount)
                .reduce(Integer::sum)
                .orElse(0);
    }

    public static int sumNeededAscensionGrails(UserServant servant) {
        return calculateNeededGrails(servant, servant.getLevel(), servant.getDesLevel());
    }

    private static int calculateNeededGrails(UserServant servant, int currentLevel, int desiredLevel) {
        int currentAscLevel = Math.max(getAscensionFromRarityAndLevel(currentLevel, servant.getBaseServant()
                .getRarity()) - 4, 0);
        if (servant.isAscension()) {
            currentAscLevel++;
        }
        int desiredAscLevel = Math.max(getAscensionFromRarityAndLevel(desiredLevel, servant.getBaseServant()
                .getRarity()) - 4, 0);
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
        return baseServant.getNoblePhantasms()
                .get(baseServant.getNoblePhantasms().size() - 1)
                .getFunctions()
                .stream()
                .filter(fnc -> fnc.getFuncType().startsWith("damageNp"))
                .findFirst()
                .orElse(null);
    }
}
