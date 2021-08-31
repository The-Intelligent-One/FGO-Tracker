package com.github.theintelligentone.fgotracker.ui.valuefactory.roster;

import com.github.theintelligentone.fgotracker.domain.servant.propertyobjects.FgoFunction;
import com.github.theintelligentone.fgotracker.domain.servant.propertyobjects.NoblePhantasm;
import com.github.theintelligentone.fgotracker.domain.view.UserServantView;
import com.github.theintelligentone.fgotracker.service.datamanagement.cache.ServantManagementService;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class UserServantNpDamageValueFactory implements Callback<TableColumn.CellDataFeatures<UserServantView, Number>, ObservableValue<Number>> {

    private static final double PERCANTAGE_SCALE = 1000;
    private static final double BASE_DAMAGE_MULTIPLIER = 0.23;

    @Override
    public ObservableValue<Number> call(TableColumn.CellDataFeatures<UserServantView, Number> param) {
        SimpleIntegerProperty damage = new SimpleIntegerProperty();
        if (param.getValue().baseServantProperty().getValue() != null) {
            damage.set(calculateNpDamage(param.getValue()));
        }
        return damage;
    }

    private int calculateNpDamage(UserServantView servant) {
        int damage = 0;
        NoblePhantasm np = servant.baseServantProperty().getValue().getNoblePhantasms().get(
                servant.baseServantProperty().getValue().getNoblePhantasms().size() - 1);
        FgoFunction dmgFnc = findDamagingNpFunction(np);
        if (dmgFnc != null) {
            damage = calculateDamage(servant, dmgFnc, np.getCard());
        }
        return damage;
    }

    private int calculateDamage(UserServantView servant, FgoFunction np, String card) {
        int svtAtk = calculateBaseAtk(servant);
        return Math.toIntExact(Math.round(
                svtAtk * getNpMultiplier(servant.npLevelProperty(), np) * getClassAttackMultipler(servant) * getCardMultiplier(
                        card) * BASE_DAMAGE_MULTIPLIER));
    }

    private double getCardMultiplier(String card) {
        return ServantManagementService.CARD_DATA.get(card).get(1).getAdjustAtk() / PERCANTAGE_SCALE;
    }

    private double getClassAttackMultipler(UserServantView servant) {
        return ServantManagementService.CLASS_ATTACK_MULTIPLIER.get(
                servant.baseServantProperty().getValue().getClassName()) / PERCANTAGE_SCALE;
    }

    private double getNpMultiplier(IntegerProperty npLevel, FgoFunction np) {
        return np.getSvals().get(npLevel.subtract(1).get()).getValue() / PERCANTAGE_SCALE;
    }

    private int calculateBaseAtk(UserServantView servant) {
        return servant.fouAtkProperty().add(
                servant.baseServantProperty().getValue().getAtkGrowth().get(
                        servant.levelProperty().subtract(1).get())).intValue();
    }

    private FgoFunction findDamagingNpFunction(NoblePhantasm noblePhantasm) {
        return noblePhantasm.getFunctions().stream()
                .filter(fnc -> fnc.getFuncType().startsWith("damageNp")).findFirst().orElse(null);
    }
}
