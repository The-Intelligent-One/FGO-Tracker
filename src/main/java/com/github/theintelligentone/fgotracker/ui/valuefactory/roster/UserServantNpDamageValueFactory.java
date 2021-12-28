package com.github.theintelligentone.fgotracker.ui.valuefactory.roster;

import com.github.theintelligentone.fgotracker.domain.servant.UserServant;
import com.github.theintelligentone.fgotracker.domain.servant.propertyobjects.FgoFunction;
import com.github.theintelligentone.fgotracker.domain.servant.propertyobjects.NoblePhantasm;
import com.github.theintelligentone.fgotracker.service.datamanagement.cache.ServantManagementService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class UserServantNpDamageValueFactory implements Callback<TableColumn.CellDataFeatures<UserServant, Number>, ObservableValue<Number>> {

    private static final double PERCANTAGE_SCALE = 1000;
    private static final double BASE_DAMAGE_MULTIPLIER = 0.23;

    @Override
    public ObservableValue<Number> call(TableColumn.CellDataFeatures<UserServant, Number> param) {
        SimpleIntegerProperty damage = new SimpleIntegerProperty();
        if (param.getValue().getSvtId() != 0) {
            damage.set(calculateNpDamage(param.getValue()));
        }
        return damage;
    }

    private int calculateNpDamage(UserServant servant) {
        int damage = 0;
        NoblePhantasm np = servant.getBaseServant().getNoblePhantasms().get(
                servant.getBaseServant().getNoblePhantasms().size() - 1);
        FgoFunction dmgFnc = findDamagingNpFunction(np);
        if (dmgFnc != null) {
            damage = calculateDamage(servant, dmgFnc, np.getCard());
        }
        return damage;
    }

    private int calculateDamage(UserServant servant, FgoFunction np, String card) {
        int svtAtk = calculateBaseAtk(servant);
        return Math.toIntExact(Math.round(
                svtAtk * getNpMultiplier(servant.getNpLevel(), np) * getClassAttackMultipler(servant) * getCardMultiplier(
                        card) * BASE_DAMAGE_MULTIPLIER));
    }

    private double getCardMultiplier(String card) {
        return ServantManagementService.CARD_DATA.get(card).get(1).getAdjustAtk() / PERCANTAGE_SCALE;
    }

    private double getClassAttackMultipler(UserServant servant) {
        return ServantManagementService.CLASS_ATTACK_MULTIPLIER.get(
                servant.getBaseServant().getClassName()) / PERCANTAGE_SCALE;
    }

    private double getNpMultiplier(int npLevel, FgoFunction np) {
        return np.getSvals().get(npLevel - 1).getValue() / PERCANTAGE_SCALE;
    }

    private int calculateBaseAtk(UserServant servant) {
        return servant.getFouAtk() + servant.getBaseServant().getAtkGrowth().get(servant.getLevel() - 1);
    }

    private FgoFunction findDamagingNpFunction(NoblePhantasm noblePhantasm) {
        return noblePhantasm.getFunctions().stream()
                .filter(fnc -> fnc.getFuncType().startsWith("damageNp")).findFirst().orElse(null);
    }
}
