package com.github.theintelligentone.fgotracker.domain.servant.factory;

import com.github.theintelligentone.fgotracker.domain.servant.Servant;
import com.github.theintelligentone.fgotracker.domain.servant.UserServant;

public class UserServantFactory {
    public UserServant createUserServantFromBaseServant(Servant baseServant) {
        return UserServant.builder()
                .svtId(baseServant.getId())
                .baseServant(baseServant)
                .rarity(baseServant.getRarity())
                .fouAtk(0)
                .fouHp(0)
                .ascension(false)
                .bondLevel(0)
                .level(1)
                .npLevel(1)
                .skillLevel1(1)
                .skillLevel2(1)
                .skillLevel3(1)
                .notes("")
                .build();
    }
}
