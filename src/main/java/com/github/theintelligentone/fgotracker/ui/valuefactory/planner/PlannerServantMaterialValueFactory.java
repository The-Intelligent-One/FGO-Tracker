package com.github.theintelligentone.fgotracker.ui.valuefactory.planner;

import com.github.theintelligentone.fgotracker.domain.servant.PlannerServant;
import com.github.theintelligentone.fgotracker.service.AscensionUtils;
import javafx.beans.NamedArg;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class PlannerServantMaterialValueFactory implements Callback<TableColumn.CellDataFeatures<PlannerServant, Number>, ObservableValue<Number>> {
    private long matId;

    public PlannerServantMaterialValueFactory(@NamedArg("matId") long matId) {
        this.matId = matId;
    }

    @Override
    public ObservableValue<Number> call(TableColumn.CellDataFeatures<PlannerServant, Number> param) {
        SimpleIntegerProperty result = null;
        if (param.getValue().getBaseServant() != null) {
            int matSum = 0;
            matSum += sumNeededAscensionMats(param.getValue());
            matSum += sumAllNeededSkillMats(param.getValue());
            if (matSum > 0) {
                result = new SimpleIntegerProperty(matSum);
            }
        }
        return result;
    }

    private int sumAllNeededSkillMats(PlannerServant servant) {
        int result = 0;
        result += sumNeededSkillMats(servant, servant.getBaseServant().getSkillLevel1(), servant.getDesSkill1());
        result += sumNeededSkillMats(servant, servant.getBaseServant().getSkillLevel2(), servant.getDesSkill2());
        result += sumNeededSkillMats(servant, servant.getBaseServant().getSkillLevel3(), servant.getDesSkill3());
        return result;
    }

    private int sumNeededSkillMats(PlannerServant servant, int currentLevel, int desiredLevel) {
        return servant.getSkillMaterials().stream()
                .skip(currentLevel - 1).limit(desiredLevel - currentLevel)
                .flatMap(mat -> mat.getItems().stream())
                .filter(mat -> matId == mat.getItem().getId())
                .mapToInt(mat -> mat.getAmount())
                .reduce((amount1, amount2) -> amount1 + amount2).orElse(0);
    }

    private int sumNeededAscensionMats(PlannerServant servant) {
        int currentAscensionLevel = AscensionUtils.getAscensionFromRarityAndLevel(servant.getBaseServant().getLevel(), servant.getBaseServant().getRarity());
        int desiredAscensionLevel = AscensionUtils.getAscensionFromRarityAndLevel(servant.getDesLevel(), servant.getBaseServant().getRarity());
        return servant.getAscensionMaterials().stream()
                .skip(currentAscensionLevel).limit(Math.max(desiredAscensionLevel - currentAscensionLevel, 0))
                .flatMap(mat -> mat.getItems().stream())
                .filter(mat -> matId == mat.getItem().getId())
                .mapToInt(mat -> mat.getAmount())
                .reduce((amount1, amount2) -> amount1 + amount2).orElse(0);
    }
}
