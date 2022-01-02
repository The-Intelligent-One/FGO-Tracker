package com.github.theintelligentone.fgotracker.ui.cellfactory;

import com.github.theintelligentone.fgotracker.domain.servant.UserServant;
import com.github.theintelligentone.fgotracker.service.ServantUtils;
import javafx.scene.control.cell.CheckBoxTableCell;

import java.util.List;

public class AscensionCheckBoxTableCell extends CheckBoxTableCell<UserServant, Boolean> {
    @Override
    public void updateItem(Boolean item, boolean empty) {
        super.updateItem(item, empty);
        UserServant servant = getTableRow().getItem();
        if (servant != null && (servant.getSvtId() == 0 || !servantIsAtLevelWithAscension(servant))) {
            servant.setAscension(false);
            setText(null);
            setGraphic(null);
        }
    }

    private boolean servantIsAtLevelWithAscension(UserServant servant) {
        boolean result = false;
        if (servant.getSvtId() != 0) {
            List<Integer> levelsWithAscension = ServantUtils.createListOfAscensionLevels(
                    servant.getBaseServant().getRarity());
            result = levelsWithAscension.contains(servant.getLevel());
        }
        return result;
    }
}
