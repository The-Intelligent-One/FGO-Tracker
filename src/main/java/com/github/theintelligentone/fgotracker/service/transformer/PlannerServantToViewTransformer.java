package com.github.theintelligentone.fgotracker.service.transformer;

import com.github.theintelligentone.fgotracker.domain.servant.PlannerServant;
import com.github.theintelligentone.fgotracker.domain.servant.UserServant;
import com.github.theintelligentone.fgotracker.domain.view.PlannerServantView;
import com.github.theintelligentone.fgotracker.domain.view.UserServantView;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.List;
import java.util.stream.Collectors;

public class PlannerServantToViewTransformer {
    private UserServantToViewTransformer userServantToViewTransformer;

    public PlannerServantToViewTransformer() {
        this.userServantToViewTransformer = new UserServantToViewTransformer();
    }

    public PlannerServantView transform(PlannerServant servant) {
        ObjectProperty<UserServantView> baseProperty = new SimpleObjectProperty<>(userServantToViewTransformer.transform(servant.getBaseServant()));
        IntegerProperty dLevel = new SimpleIntegerProperty(servant.getDesLevel());
        dLevel.addListener((observable, oldValue, newValue) -> servant.setDesLevel(newValue.intValue()));
        IntegerProperty dSkill1 = new SimpleIntegerProperty(servant.getDesSkill1());
        dSkill1.addListener((observable, oldValue, newValue) -> servant.setDesSkill1(newValue.intValue()));
        IntegerProperty dSkill2 = new SimpleIntegerProperty(servant.getDesSkill2());
        dSkill2.addListener((observable, oldValue, newValue) -> servant.setDesSkill2(newValue.intValue()));
        IntegerProperty dSkill3 = new SimpleIntegerProperty(servant.getDesSkill3());
        dSkill3.addListener((observable, oldValue, newValue) -> servant.setDesSkill3(newValue.intValue()));
        return PlannerServantView.builder()
                .baseServant(baseProperty)
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

    public PlannerServant transform(PlannerServantView servant) {
        UserServant transformedUserServant = servant.getBaseServant().getValue() != null
                ? userServantToViewTransformer.transform(servant.getBaseServant().getValue())
                : null;
        return PlannerServant.builder()
                .baseServant(transformedUserServant)
                .desLevel(servant.getDesLevel().intValue())
                .desSkill1(servant.getDesSkill1().intValue())
                .desSkill2(servant.getDesSkill2().intValue())
                .desSkill3(servant.getDesSkill3().intValue())
                .skillMaterials(servant.getSkillMaterials())
                .ascensionMaterials(servant.getAscensionMaterials())
                .build();
    }

    public List<PlannerServant> transformAllFromViews(List<PlannerServantView> servants) {
        return servants.stream().map(this::transform).collect(Collectors.toList());
    }
}
