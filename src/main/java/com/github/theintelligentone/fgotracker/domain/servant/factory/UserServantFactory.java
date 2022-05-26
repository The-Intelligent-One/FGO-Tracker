package com.github.theintelligentone.fgotracker.domain.servant.factory;

import com.github.theintelligentone.fgotracker.domain.servant.Servant;
import com.github.theintelligentone.fgotracker.domain.servant.UserServant;

public class UserServantFactory {
    public static UserServant createBlankUserServant() {
        return UserServant.builder()
                .level(1)
                .skillLevel1(1)
                .skillLevel2(1)
                .skillLevel3(1)
                .appendSkillLevel1(1)
                .appendSkillLevel2(1)
                .appendSkillLevel3(1)
                .npLevel(1)
                .build();
    }

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
                .appendSkillLevel1(1)
                .appendSkillLevel2(1)
                .appendSkillLevel3(1)
                .notes("")
                .build();
    }

    public static UserServant copyWithNewBaseServant(UserServant userServant, Servant baseServant) {
        return userServant.toBuilder()
                .baseServant(baseServant)
                .svtId(baseServant.getId())
                .svtClass(baseServant.getClassName())
                .rarity(baseServant.getRarity())
                .build();
    }

    public static void updateBaseServant(UserServant userServant, Servant baseServant) {
        userServant.setBaseServant(baseServant);
        userServant.setSvtId(baseServant.getId());
        userServant.setSvtClass(baseServant.getClassName());
        userServant.setRarity(baseServant.getRarity());
    }
}
