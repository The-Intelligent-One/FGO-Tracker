package com.github.theintelligentone.fgotracker.service.transformer;

import com.github.theintelligentone.fgotracker.domain.servant.UserServant;
import com.github.theintelligentone.fgotracker.domain.view.UserServantView;
import javafx.beans.property.*;

import java.util.List;
import java.util.stream.Collectors;

public class UserServantToViewTransformer {
    public UserServantView transform(UserServant servant) {
        UserServantView result = new UserServantView();
        result.setBaseServant(new SimpleObjectProperty<>(servant.getBaseServant()));
        result.setAscension(new SimpleBooleanProperty(servant.isAscension()));
        result.setFouAtk(new SimpleIntegerProperty(servant.getFouAtk()));
        result.setFouHp(new SimpleIntegerProperty(servant.getFouHp()));
        result.setNpLevel(new SimpleIntegerProperty(servant.getNpLevel()));
        result.setLevel(new SimpleIntegerProperty(servant.getLevel()));
        result.setBondLevel(new SimpleIntegerProperty(servant.getBondLevel()));
        result.setSkillLevel1(new SimpleIntegerProperty(servant.getSkillLevel1()));
        result.setSkillLevel2(new SimpleIntegerProperty(servant.getSkillLevel2()));
        result.setSkillLevel3(new SimpleIntegerProperty(servant.getSkillLevel3()));
        result.setRarity(new SimpleIntegerProperty(servant.getRarity()));
        result.setSvtId(new SimpleLongProperty(servant.getSvtId()));
        result.setNotes(new SimpleStringProperty(servant.getNotes()));
        return result;
    }

    public UserServant transform(UserServantView servant) {
        return UserServant.builder()
                .svtId(servant.getSvtId().getValue())
                .ascension(servant.getAscension().getValue())
                .rarity(servant.getRarity().getValue())
                .baseServant(servant.getBaseServant().getValue())
                .bondLevel(servant.getBondLevel().getValue())
                .fouAtk(servant.getFouAtk().getValue())
                .fouHp(servant.getFouHp().getValue())
                .level(servant.getLevel().getValue())
                .npLevel(servant.getNpLevel().getValue())
                .skillLevel1(servant.getSkillLevel1().getValue())
                .skillLevel2(servant.getSkillLevel2().getValue())
                .skillLevel3(servant.getSkillLevel3().getValue())
                .notes(servant.notesProperty().getValue())
                .build();
    }

    public List<UserServantView> transformAllToViews(List<UserServant> servants) {
        return servants.stream().map(this::transform).collect(Collectors.toList());
    }

    public List<UserServant> transformAll(List<UserServantView> servants) {
        return servants.stream().map(this::transform).collect(Collectors.toList());
    }
}
