package com.github.theintelligentone.fgotracker.service.transformer;

import com.github.theintelligentone.fgotracker.domain.servant.UserServant;
import com.github.theintelligentone.fgotracker.ui.view.UserServantView;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserServantToViewTransformer {
    public UserServantView transform(UserServant servant) {
        UserServantView result = new UserServantView();
        result.setBaseServant(new SimpleObjectProperty<>(servant.getBaseServant()));
        result.setAscension(new SimpleBooleanProperty(servant.isAscension()));
        result.setFouAtk(new SimpleIntegerProperty(servant.getFouAtk()));
        result.setNpLevel(new SimpleIntegerProperty(servant.getNpLevel()));
        result.setLevel(new SimpleIntegerProperty(servant.getLevel()));
        result.setBondLevel(new SimpleIntegerProperty(servant.getBondLevel()));
        result.setSkillLevel1(new SimpleIntegerProperty(servant.getSkillLevel1()));
        result.setSkillLevel2(new SimpleIntegerProperty(servant.getSkillLevel2()));
        result.setSkillLevel3(new SimpleIntegerProperty(servant.getSkillLevel3()));
        result.setRarity(new SimpleIntegerProperty(servant.getRarity()));
        result.setSvtId(new SimpleLongProperty(servant.getSvtId()));
        return result;
    }

    public List<UserServantView> transformAll(Collection<UserServant> servants) {
        return servants.stream().map(this::transform).collect(Collectors.toList());
    }
}
