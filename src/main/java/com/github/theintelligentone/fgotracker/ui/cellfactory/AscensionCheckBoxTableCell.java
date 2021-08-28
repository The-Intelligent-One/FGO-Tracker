package com.github.theintelligentone.fgotracker.ui.cellfactory;

import com.github.theintelligentone.fgotracker.domain.view.UserServantView;
import com.github.theintelligentone.fgotracker.service.ServantUtils;
import javafx.scene.control.cell.CheckBoxTableCell;

import java.util.List;

public class AscensionCheckBoxTableCell extends CheckBoxTableCell<UserServantView, Boolean> {
    @Override
    public void updateItem(Boolean item, boolean empty) {
        super.updateItem(item, empty);
        UserServantView servant = getTableRow().getItem();
        if (servant != null && (servant.baseServantProperty().getValue() == null || !servantIsAtLevelWithAscension(servant))) {
            servant.ascensionProperty().set(false);
            setText(null);
            setGraphic(null);
        }
    }

    private boolean servantIsAtLevelWithAscension(UserServantView servant) {
        boolean result = false;
        if (servant.baseServantProperty() != null && servant.baseServantProperty().getValue() != null) {
            List<Integer> levelsWithAscension = new ServantUtils().createListOfAscensionLevels(
                    servant.baseServantProperty().getValue().getRarity());
            result = levelsWithAscension.contains(servant.levelProperty().intValue());
        }
        return result;
    }
}
