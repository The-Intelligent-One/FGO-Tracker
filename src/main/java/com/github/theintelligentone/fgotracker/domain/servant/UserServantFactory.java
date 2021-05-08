package com.github.theintelligentone.fgotracker.domain.servant;

import com.github.theintelligentone.fgotracker.domain.servant.propertyobjects.FgoFunction;

public class UserServantFactory {
    public ServantOfUser createUserServantFromBaseServant(Servant baseServant) {
        return ServantOfUser.builder()
                .svtId(baseServant.getId())
                .baseServant(baseServant)
                .rarity(baseServant.getRarity())
                .fouAtk(0)
                .fouHp(0)
                .ascension(0)
                .bondLevel(0)
                .level(1)
                .npLevel(1)
                .skillLevel1(1)
                .skillLevel2(1)
                .skillLevel3(1)
                .npType(determineNpCard(baseServant))
                .npTarget(determineNpTarget(baseServant))
                .build();
    }

    private String determineNpCard(Servant baseServant) {
        String card = baseServant.getNoblePhantasms().get(baseServant.getNoblePhantasms().size() - 1).getCard();
        return card.substring(0, 1).toUpperCase() + card.substring(1);
    }

    private String determineNpTarget(Servant baseServant) {
        String target = "Support";
        FgoFunction damageNp = findDamagingNpFunction(baseServant);
        if (damageNp != null) {
            target = damageIsAoE(damageNp) ? "AoE" : "ST";
        }
        return target;
    }

    private boolean damageIsAoE(FgoFunction damageNp) {
        return "enemyAll".equalsIgnoreCase(damageNp.getFuncTargetType());
    }

    private FgoFunction findDamagingNpFunction(Servant baseServant) {
        return baseServant.getNoblePhantasms().get(baseServant.getNoblePhantasms().size() - 1).getFunctions().stream()
                .filter(fnc -> fnc.getFuncType().startsWith("damageNp")).findFirst().orElse(null);
    }
}
