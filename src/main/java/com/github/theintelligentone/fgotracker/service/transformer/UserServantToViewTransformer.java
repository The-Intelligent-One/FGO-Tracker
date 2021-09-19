package com.github.theintelligentone.fgotracker.service.transformer;

import com.github.theintelligentone.fgotracker.domain.servant.UserServant;
import com.github.theintelligentone.fgotracker.domain.view.UserServantView;
import javafx.beans.property.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
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
                .svtId(servant.svtIdProperty().getValue())
                .ascension(servant.ascensionProperty().getValue())
                .rarity(servant.rarityProperty().getValue())
                .baseServant(servant.baseServantProperty().getValue())
                .bondLevel(servant.bondLevelProperty().getValue())
                .fouAtk(servant.fouAtkProperty().getValue())
                .fouHp(servant.fouHpProperty().getValue())
                .level(servant.levelProperty().getValue())
                .npLevel(servant.npLevelProperty().getValue())
                .skillLevel1(servant.skillLevel1Property().getValue())
                .skillLevel2(servant.skillLevel2Property().getValue())
                .skillLevel3(servant.skillLevel3Property().getValue())
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
