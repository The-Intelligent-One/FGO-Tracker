package com.github.theintelligentone.fgotracker.domain.servant.factory;

import com.github.theintelligentone.fgotracker.domain.item.UpgradeCost;
import com.github.theintelligentone.fgotracker.domain.servant.PlannerServant;
import com.github.theintelligentone.fgotracker.domain.servant.UserServant;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlannerServantFactory {
    public static PlannerServant createBlankPlannerServant() {
        return PlannerServant.builder()
                .baseServant(UserServantFactory.createBlankUserServant())
                .desLevel(1)
                .desSkill1(1)
                .desSkill2(1)
                .desSkill3(1)
                .desAppendSkill1(1)
                .desAppendSkill2(1)
                .desAppendSkill3(1)
                .build();
    }

    public static PlannerServant createPlannerServantFromBaseServant(UserServant baseServant) {
        return PlannerServant.builder()
                .svtId(baseServant.getSvtId())
                .baseServant(baseServant)
                .desLevel(1)
                .desSkill1(1)
                .desSkill2(1)
                .desSkill3(1)
                .desAppendSkill1(1)
                .desAppendSkill2(1)
                .desAppendSkill3(1)
                .ascensionMaterials(convertMaterialMapToList(baseServant.getBaseServant().getAscensionMaterials()))
                .skillMaterials(convertMaterialMapToList(baseServant.getBaseServant().getSkillMaterials()))
                .appendSkillMaterials(convertMaterialMapToList(baseServant.getBaseServant().getAppendSkillMaterials()))
                .build();
    }

    public static PlannerServant copyWithNewBaseServant(PlannerServant userServant, UserServant baseServant) {
        return userServant.toBuilder()
                .baseServant(baseServant)
                .svtId(baseServant.getSvtId())
                .ascensionMaterials(convertMaterialMapToList(baseServant.getBaseServant().getAscensionMaterials()))
                .skillMaterials(convertMaterialMapToList(baseServant.getBaseServant().getSkillMaterials()))
                .appendSkillMaterials(convertMaterialMapToList(baseServant.getBaseServant().getAppendSkillMaterials()))
                .build();
    }

    public static void updateBaseServant(PlannerServant userServant, UserServant baseServant) {
        userServant.setBaseServant(baseServant);
        userServant.setSvtId(baseServant.getSvtId());
        userServant.setAscensionMaterials(convertMaterialMapToList(baseServant.getBaseServant().getAscensionMaterials()));
        userServant.setSkillMaterials(convertMaterialMapToList(baseServant.getBaseServant().getSkillMaterials()));
        userServant.setAppendSkillMaterials(convertMaterialMapToList(baseServant.getBaseServant().getAppendSkillMaterials()));
    }

    private static List<UpgradeCost> convertMaterialMapToList(Map<Integer, UpgradeCost> materials) {
        return materials.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }
}
