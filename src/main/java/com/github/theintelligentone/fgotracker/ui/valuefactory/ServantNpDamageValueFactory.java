package com.github.theintelligentone.fgotracker.ui.valuefactory;

import com.github.theintelligentone.fgotracker.domain.servant.ServantOfUser;
import com.github.theintelligentone.fgotracker.domain.servant.propertyobjects.FgoFunction;
import com.github.theintelligentone.fgotracker.domain.servant.propertyobjects.NoblePhantasm;
import com.github.theintelligentone.fgotracker.service.DataManagementService;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class ServantNpDamageValueFactory implements Callback<TableColumn.CellDataFeatures<ServantOfUser, Number>, ObservableValue<Number>> {

    private static final int PERCANTAGE_SCALE = 1000;

    @Override
    public ObservableValue<Number> call(TableColumn.CellDataFeatures<ServantOfUser, Number> param) {
        SimpleLongProperty damage = new SimpleLongProperty();
        if (param.getValue() != null) {
            damage.set(calculateNpDamage(param.getValue()));
        }
        return damage;
    }

    private long calculateNpDamage(ServantOfUser servant) {
        long damage = 0;
        NoblePhantasm np = servant.getBaseServant().getNoblePhantasms().get(servant.getBaseServant().getNoblePhantasms().size() - 1);
        FgoFunction dmgFnc = findDamagingNpFunction(np);
        if (dmgFnc != null) {
            damage = calculateDamage(servant, dmgFnc, np.getCard());
        }
        return damage;
    }

    private long calculateDamage(ServantOfUser servant, FgoFunction np, String card) {
        int svtAtk = calculateBaseAtk(servant);
        return Math.round(svtAtk * getNpMultiplier(servant.getNpLevel(), np) * getCardMultiplier(card));
    }

    private double getCardMultiplier(String card) {
        return DataManagementService.CARD_DATA.get(card).get(1).getAdjustAtk() / PERCANTAGE_SCALE;
    }

    private double getNpMultiplier(int npLevel, FgoFunction np) {
        return np.getSvals().get(npLevel).getValue() / PERCANTAGE_SCALE;
    }

    private int calculateBaseAtk(ServantOfUser servant) {
        return servant.getBaseServant().getAtkGrowth().get(servant.getLevel() - 1) + servant.getFouAtk();
    }

    private FgoFunction findDamagingNpFunction(NoblePhantasm noblePhantasm) {
        return noblePhantasm.getFunctions().stream()
                .filter(fnc -> fnc.getFuncType().startsWith("damageNp")).findFirst().orElse(null);
    }
}
