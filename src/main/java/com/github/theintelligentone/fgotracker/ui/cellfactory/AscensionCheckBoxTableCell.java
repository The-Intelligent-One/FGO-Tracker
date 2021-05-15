package com.github.theintelligentone.fgotracker.ui.cellfactory;

import com.github.theintelligentone.fgotracker.domain.servant.UserServant;
import com.github.theintelligentone.fgotracker.service.AscensionUtils;
import javafx.scene.control.cell.CheckBoxTableCell;

import java.util.List;

public class AscensionCheckBoxTableCell extends CheckBoxTableCell<UserServant, Boolean> {

    @Override
    public void updateItem(Boolean item, boolean empty) {
        super.updateItem(item, empty);
        UserServant servant = getTableRow().getItem();
        if (servant == null || !servantIsAtLevelWithAscension(servant)) {
            setText(null);
            setGraphic(null);
        }
    }

    private boolean servantIsAtLevelWithAscension(UserServant servant) {
        boolean result = false;
        if (servant.getBaseServant() != null) {
            List<Integer> levelsWithAscension = AscensionUtils.createListOfAscensionLevels(servant.getBaseServant().getRarity());
            result = levelsWithAscension.contains(servant.getLevel());
        }
        return result;
    }
}
