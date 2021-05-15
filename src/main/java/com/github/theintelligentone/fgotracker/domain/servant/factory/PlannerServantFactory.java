package com.github.theintelligentone.fgotracker.domain.servant.factory;

import com.github.theintelligentone.fgotracker.domain.servant.PlannerServant;
import com.github.theintelligentone.fgotracker.domain.servant.UserServant;
import com.github.theintelligentone.fgotracker.domain.servant.propertyobjects.UpgradeCost;
import com.github.theintelligentone.fgotracker.service.DataManagementService;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlannerServantFactory {

    private static final int MAX_SKILL_LEVEL = 10;

    public List<PlannerServant> createForLTPlanner(List<UserServant> servants) {
        return servants.stream().map(this::createFromUserServantForLTPlanner).collect(Collectors.toList());
    }

    public List<PlannerServant> createFormEachUserServant(List<UserServant> servants) {
        return servants.stream().map(this::createFromUserServant).collect(Collectors.toList());
    }

    public PlannerServant createFromUserServant(List<UserServant> servants, int index) {
        return createFromUserServant(servants.get(index));
    }

    private PlannerServant createFromUserServant(UserServant servant) {
        PlannerServant result = new PlannerServant();
        if (servant.getBaseServant() != null) {
            result = PlannerServant.builder()
                    .svtId(servant.getSvtId())
                    .desLevel(servant.getLevel())
                    .desSkill1(servant.getSkillLevel1())
                    .desSkill2(servant.getSkillLevel2())
                    .desSkill3(servant.getSkillLevel3())
                    .ascensionMaterials(convertMaterialMapToList(servant.getBaseServant().getAscensionMaterials()))
                    .skillMaterials(convertMaterialMapToList(servant.getBaseServant().getSkillMaterials()))
                    .build();
        }
        return result;
    }

    private PlannerServant createFromUserServantForLTPlanner(UserServant servant) {
        PlannerServant result = new PlannerServant();
        if (servant.getBaseServant() != null) {
            result = PlannerServant.builder()
                    .baseServant(servant)
                    .desLevel(DataManagementService.MAX_LEVELS[servant.getBaseServant().getRarity()])
                    .desSkill1(MAX_SKILL_LEVEL)
                    .desSkill2(MAX_SKILL_LEVEL)
                    .desSkill3(MAX_SKILL_LEVEL)
                    .ascensionMaterials(convertMaterialMapToList(servant.getBaseServant().getAscensionMaterials()))
                    .skillMaterials(convertMaterialMapToList(servant.getBaseServant().getSkillMaterials()))
                    .build();
        }
        return result;
    }

    private List<UpgradeCost> convertMaterialMapToList(Map<Integer, UpgradeCost> materials) {
        return materials.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey)).map(Map.Entry::getValue).collect(Collectors.toList());
    }
}
