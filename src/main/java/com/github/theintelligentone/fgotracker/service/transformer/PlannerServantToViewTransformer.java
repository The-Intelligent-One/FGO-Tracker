package com.github.theintelligentone.fgotracker.service.transformer;

import com.github.theintelligentone.fgotracker.domain.servant.PlannerServant;
import com.github.theintelligentone.fgotracker.domain.view.PlannerServantView;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.List;
import java.util.stream.Collectors;

public class PlannerServantToViewTransformer {
    public PlannerServantView transform(PlannerServant servant) {
//        ObservableObjectValue<UserServantView> baseProperty = new SimpleObjectProperty<>(servant.getBaseServant());
        SimpleIntegerProperty dLevel = new SimpleIntegerProperty(servant.getDesLevel());
        dLevel.addListener((observable, oldValue, newValue) -> servant.setDesLevel(newValue.intValue()));
        SimpleIntegerProperty dSkill1 = new SimpleIntegerProperty(servant.getDesSkill1());
        dSkill1.addListener((observable, oldValue, newValue) -> servant.setDesSkill1(newValue.intValue()));
        SimpleIntegerProperty dSkill2 = new SimpleIntegerProperty(servant.getDesSkill2());
        dSkill2.addListener((observable, oldValue, newValue) -> servant.setDesSkill2(newValue.intValue()));
        SimpleIntegerProperty dSkill3 = new SimpleIntegerProperty(servant.getDesSkill3());
        dSkill3.addListener((observable, oldValue, newValue) -> servant.setDesSkill3(newValue.intValue()));
        return PlannerServantView.builder()
//                .baseServant(baseProperty)
                .desLevel(dLevel)
                .desSkill1(dSkill1)
                .desSkill2(dSkill2)
                .desSkill3(dSkill3)
                .skillMaterials(servant.getSkillMaterials())
                .ascensionMaterials(servant.getAscensionMaterials())
                .build();
    }

    public List<PlannerServantView> transformAll(List<PlannerServant> servants) {
        return servants.stream().map(this::transform).collect(Collectors.toList());
    }
}
