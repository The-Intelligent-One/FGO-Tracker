package com.github.theintelligentone.fgotracker.ui.cellfactory;

import com.github.theintelligentone.fgotracker.domain.view.UserServantView;
import com.github.theintelligentone.fgotracker.service.ServantUtils;
import javafx.scene.control.cell.CheckBoxTableCell;

import java.util.List;

public class AscensionCheckBoxTableCell extends CheckBoxTableCell<UserServantView, Boolean> {
    public AscensionCheckBoxTableCell() {
        super();
    }

    @Override
    public void updateItem(Boolean item, boolean empty) {
        super.updateItem(item, empty);
        UserServantView servant = getTableRow().getItem();
        if (servant != null && (servant.getBaseServant().getValue() == null || !servantIsAtLevelWithAscension(servant))) {
            servant.getAscension().set(false);
            setText(null);
            setGraphic(null);
        }
    }

    private boolean servantIsAtLevelWithAscension(UserServantView servant) {
        boolean result = false;
        if (servant.getBaseServant() != null && servant.getBaseServant().getValue() != null) {
            List<Integer> levelsWithAscension = new ServantUtils().createListOfAscensionLevels(
                    servant.getBaseServant().getValue().getRarity());
            result = levelsWithAscension.contains(servant.getLevel().intValue());
        }
        return result;
    }
}
