package com.github.theintelligentone.fgotracker.ui.cellfactory;

import com.github.theintelligentone.fgotracker.domain.servant.ServantOfUser;
import javafx.scene.control.cell.CheckBoxTableCell;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

public class AscensionCheckBoxTableCell extends CheckBoxTableCell<ServantOfUser, Boolean> {
    private static final int[] MAX_LEVELS = {65, 60, 65, 70, 80, 90};

    @Override
    public void updateItem(Boolean item, boolean empty) {
        super.updateItem(item, empty);
        ServantOfUser servant = getTableRow().getItem();
        if (servant != null) {
            if (!servantIsAtLevelWithAscension(servant)) {
                setText(null);
                setGraphic(null);
            }
        }
    }

    private boolean servantIsAtLevelWithAscension(ServantOfUser servant) {
        boolean result = false;
        if (servant != null) {
            Set<Integer> levelsWithAscension = createSetOfAscensionLevels(MAX_LEVELS[servant.getRarity()]);
            result = levelsWithAscension.contains(servant.getLevel());
        }
        return result;
    }

    private Set<Integer> createSetOfAscensionLevels(int maxLevel) {
        Set<Integer> levelsWithAscension = new HashSet<>();
        IntStream.range(1, 5).forEach(index -> levelsWithAscension.add(maxLevel - index * 10));
        for (int ascLevel = maxLevel; ascLevel <= 90; ascLevel += 5) {
            levelsWithAscension.add(ascLevel);
        }
        for (int ascLevel = 90; ascLevel <= 100; ascLevel += 2) {
            levelsWithAscension.add(ascLevel);
        }
        return levelsWithAscension;
    }
}
