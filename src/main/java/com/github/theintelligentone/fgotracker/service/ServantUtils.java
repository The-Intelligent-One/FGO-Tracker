package com.github.theintelligentone.fgotracker.service;

import com.github.theintelligentone.fgotracker.domain.item.UpgradeMaterialCost;
import com.github.theintelligentone.fgotracker.domain.servant.Servant;
import com.github.theintelligentone.fgotracker.domain.servant.propertyobjects.FgoFunction;
import com.github.theintelligentone.fgotracker.ui.view.PlannerServantView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ServantUtils {
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

    public int getPlannedMatUse(PlannerServantView servant, long matId) {
        int matSum = 0;
        matSum += sumNeededAscensionMats(servant, matId);
        matSum += sumAllNeededSkillMats(servant, matId);
        return matSum;
    }

    private int sumAllNeededSkillMats(PlannerServantView servant, long matId) {
        int result = 0;
        result += sumNeededSkillMats(servant, servant.getBaseServant().getValue().getSkillLevel1().intValue(), servant.getDesSkill1().intValue(), matId);
        result += sumNeededSkillMats(servant, servant.getBaseServant().getValue().getSkillLevel2().intValue(), servant.getDesSkill2().intValue(), matId);
        result += sumNeededSkillMats(servant, servant.getBaseServant().getValue().getSkillLevel3().intValue(), servant.getDesSkill3().intValue(), matId);
        return result;
    }

    private int sumNeededSkillMats(PlannerServantView servant, int currentLevel, int desiredLevel, long matId) {
        return servant.getSkillMaterials().stream()
                .skip(currentLevel - 1).limit(desiredLevel - currentLevel)
                .flatMap(mat -> mat.getItems().stream())
                .filter(mat -> matId == mat.getItem().getId())
                .mapToInt(UpgradeMaterialCost::getAmount)
                .reduce(Integer::sum).orElse(0);
    }

    private int sumNeededAscensionMats(PlannerServantView servant, long matId) {
        int currentAscensionLevel = getAscensionFromRarityAndLevel(servant.getBaseServant().getValue().getLevel().intValue(), servant.getBaseServant().getValue().getRarity().getValue());
        int desiredAscensionLevel = getAscensionFromRarityAndLevel(servant.getDesLevel().intValue(), servant.getBaseServant().getValue().getRarity().intValue());
        return servant.getAscensionMaterials().stream()
                .skip(currentAscensionLevel).limit(Math.max(desiredAscensionLevel - currentAscensionLevel, 0))
                .flatMap(mat -> mat.getItems().stream())
                .filter(mat -> matId == mat.getItem().getId())
                .mapToInt(UpgradeMaterialCost::getAmount)
                .reduce(Integer::sum).orElse(0);
    }

    public int sumNeededAscensionGrails(PlannerServantView servant) {
        int currentAscensionLevel = getAscensionFromRarityAndLevel(servant.getBaseServant().getValue().getLevel().intValue(), servant.getBaseServant().getValue().getRarity().intValue());
        int desiredAscensionLevel = getAscensionFromRarityAndLevel(servant.getDesLevel().intValue(), servant.getBaseServant().getValue().getRarity().intValue());
        return Math.min(desiredAscensionLevel - currentAscensionLevel - 4, 0);
    }

    public String determineNpCard(Servant baseServant) {
        String card = baseServant.getNoblePhantasms().get(baseServant.getNoblePhantasms().size() - 1).getCard();
        return card.substring(0, 1).toUpperCase() + card.substring(1);
    }

    public String determineNpTarget(Servant baseServant) {
        String target = "Support";
        FgoFunction damageNp = findDamagingNpFunction(baseServant);
        if (damageNp != null) {
            target = damageIsAoE(damageNp) ? "AoE" : "ST";
        }
        return target;
    }

    private boolean damageIsAoE(FgoFunction damageNp) {
        return "enemyAll".equalsIgnoreCase(damageNp.getFuncTargetType());
    }

    private FgoFunction findDamagingNpFunction(Servant baseServant) {
        return baseServant.getNoblePhantasms().get(baseServant.getNoblePhantasms().size() - 1).getFunctions().stream()
                .filter(fnc -> fnc.getFuncType().startsWith("damageNp")).findFirst().orElse(null);
    }
}
