package com.github.theintelligentone.fgotracker.service;

import com.github.theintelligentone.fgotracker.domain.servant.PlannerServant;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AscensionUtils {
    private static final int[] MAX_LEVELS = {65, 60, 65, 70, 80, 90};

    public List<Integer> createListOfAscensionLevels(int rarity) {
        int maxLevel = MAX_LEVELS[rarity];
        List<Integer> levelsWithAscension = new ArrayList<>();
        IntStream.range(1, 5).forEach(index -> levelsWithAscension.add(maxLevel - index * 10));
        for (int ascLevel = maxLevel; ascLevel <= 90; ascLevel += 5) {
            levelsWithAscension.add(ascLevel);
        }
        for (int ascLevel = 90; ascLevel <= 100; ascLevel += 2) {
            levelsWithAscension.add(ascLevel);
        }
        return levelsWithAscension.stream().distinct().collect(Collectors.toList());
    }

    public int getAscensionFromRarityAndLevel(int level, int rarity) {
        List<Integer> listOfAscensionLevels = createListOfAscensionLevels(rarity);
        int nextAscLvl = listOfAscensionLevels.stream().dropWhile(lvl -> lvl < level).findFirst().get();
        return listOfAscensionLevels.indexOf(nextAscLvl);
    }

    public int getPlannedMatUse(PlannerServant servant, long matId) {
        int matSum = 0;
        matSum += sumNeededAscensionMats(servant, matId);
        matSum += sumAllNeededSkillMats(servant, matId);
        return matSum;
    }

    private int sumAllNeededSkillMats(PlannerServant servant, long matId) {
        int result = 0;
        result += sumNeededSkillMats(servant, servant.getBaseServant().getSkillLevel1(), servant.getDesSkill1(), matId);
        result += sumNeededSkillMats(servant, servant.getBaseServant().getSkillLevel2(), servant.getDesSkill2(), matId);
        result += sumNeededSkillMats(servant, servant.getBaseServant().getSkillLevel3(), servant.getDesSkill3(), matId);
        return result;
    }

    private int sumNeededSkillMats(PlannerServant servant, int currentLevel, int desiredLevel, long matId) {
        return servant.getSkillMaterials().stream()
                .skip(currentLevel - 1).limit(desiredLevel - currentLevel)
                .flatMap(mat -> mat.getItems().stream())
                .filter(mat -> matId == mat.getItem().getId())
                .mapToInt(mat -> mat.getAmount())
                .reduce((amount1, amount2) -> amount1 + amount2).orElse(0);
    }

    private int sumNeededAscensionMats(PlannerServant servant, long matId) {
        int currentAscensionLevel = getAscensionFromRarityAndLevel(servant.getBaseServant().getLevel(), servant.getBaseServant().getRarity());
        int desiredAscensionLevel = getAscensionFromRarityAndLevel(servant.getDesLevel(), servant.getBaseServant().getRarity());
        return servant.getAscensionMaterials().stream()
                .skip(currentAscensionLevel).limit(Math.max(desiredAscensionLevel - currentAscensionLevel, 0))
                .flatMap(mat -> mat.getItems().stream())
                .filter(mat -> matId == mat.getItem().getId())
                .mapToInt(mat -> mat.getAmount())
                .reduce((amount1, amount2) -> amount1 + amount2).orElse(0);
    }

    public int sumNeededAscensionGrails(PlannerServant servant) {
        int currentAscensionLevel = getAscensionFromRarityAndLevel(servant.getBaseServant().getLevel(), servant.getBaseServant().getRarity());
        int desiredAscensionLevel = getAscensionFromRarityAndLevel(servant.getDesLevel(), servant.getBaseServant().getRarity());
        return Math.min(desiredAscensionLevel - currentAscensionLevel - 4, 0);
    }
}
