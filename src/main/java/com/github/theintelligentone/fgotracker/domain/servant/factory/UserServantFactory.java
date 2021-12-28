package com.github.theintelligentone.fgotracker.domain.servant.factory;

import com.github.theintelligentone.fgotracker.domain.item.UpgradeCost;
import com.github.theintelligentone.fgotracker.domain.servant.Servant;
import com.github.theintelligentone.fgotracker.domain.servant.UserServant;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserServantFactory {
    public static UserServant createUserServantFromBaseServant(Servant baseServant) {
        return UserServant.builder()
                .svtId(baseServant.getId())
                .baseServant(baseServant)
                .rarity(baseServant.getRarity())
                .svtClass(baseServant.getClassName())
                .level(1)
                .npLevel(1)
                .skillLevel1(1)
                .skillLevel2(1)
                .skillLevel3(1)
                .desLevel(1)
                .desSkill1(1)
                .desSkill2(1)
                .desSkill3(1)
                .notes("")
                .ascensionMaterials(convertMaterialMapToList(baseServant.getAscensionMaterials()))
                .skillMaterials(convertMaterialMapToList(baseServant.getSkillMaterials()))
                .build();
    }

    public static UserServant updateBaseServant(UserServant userServant, Servant baseServant) {
        return userServant.toBuilder()
                .baseServant(baseServant)
                .svtId(baseServant.getId())
                .svtClass(baseServant.getClassName())
                .rarity(baseServant.getRarity())
                .ascensionMaterials(convertMaterialMapToList(baseServant.getAscensionMaterials()))
                .skillMaterials(convertMaterialMapToList(baseServant.getSkillMaterials()))
                .build();
    }

    private static List<UpgradeCost> convertMaterialMapToList(Map<Integer, UpgradeCost> materials) {
        return materials.entrySet()
                .stream().sorted(Map.Entry.comparingByKey()).map(Map.Entry::getValue).collect(Collectors.toList());
    }
}
