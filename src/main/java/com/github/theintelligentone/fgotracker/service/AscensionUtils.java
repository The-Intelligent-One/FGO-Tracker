package com.github.theintelligentone.fgotracker.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AscensionUtils {
    private static final int[] MAX_LEVELS = {65, 60, 65, 70, 80, 90};

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
        return levelsWithAscension.stream().distinct().collect(Collectors.toList());
    }

    public static int getAscensionFromRarityAndLevel(int level, int rarity) {
        List<Integer> listOfAscensionLevels = createListOfAscensionLevels(rarity);
        int nextAscLvl = listOfAscensionLevels.stream().dropWhile(lvl -> lvl < level).findFirst().get();
        return listOfAscensionLevels.indexOf(nextAscLvl);
    }
}
