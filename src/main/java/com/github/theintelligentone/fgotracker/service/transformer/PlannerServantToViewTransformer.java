package com.github.theintelligentone.fgotracker.service.transformer;

import com.github.theintelligentone.fgotracker.domain.servant.PlannerServant;
import com.github.theintelligentone.fgotracker.domain.servant.UserServant;
import com.github.theintelligentone.fgotracker.domain.view.PlannerServantView;
import com.github.theintelligentone.fgotracker.domain.view.UserServantView;
import javafx.beans.property.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PlannerServantToViewTransformer {
    @Autowired
    private UserServantToViewTransformer userServantToViewTransformer;

    public PlannerServantView transform(PlannerServant servant) {
        ObjectProperty<UserServantView> baseProperty = new SimpleObjectProperty<>();
        LongProperty svtId = new SimpleLongProperty(servant.getSvtId());
        svtId.addListener((observable, oldValue, newValue) -> servant.setSvtId(newValue.longValue()));
        IntegerProperty dLevel = new SimpleIntegerProperty(servant.getDesLevel());
        dLevel.addListener((observable, oldValue, newValue) -> servant.setDesLevel(newValue.intValue()));
        IntegerProperty dSkill1 = new SimpleIntegerProperty(servant.getDesSkill1());
        dSkill1.addListener((observable, oldValue, newValue) -> servant.setDesSkill1(newValue.intValue()));
        IntegerProperty dSkill2 = new SimpleIntegerProperty(servant.getDesSkill2());
        dSkill2.addListener((observable, oldValue, newValue) -> servant.setDesSkill2(newValue.intValue()));
        IntegerProperty dSkill3 = new SimpleIntegerProperty(servant.getDesSkill3());
        dSkill3.addListener((observable, oldValue, newValue) -> servant.setDesSkill3(newValue.intValue()));
        return PlannerServantView.builder()
                .svtId(svtId)
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
        UserServant transformedUserServant = servant.baseServantProperty().getValue() == null
                ? null
                : userServantToViewTransformer.transform(servant.baseServantProperty().getValue());
        return PlannerServant.builder()
                .svtId(servant.svtIdProperty().longValue())
                .baseServant(transformedUserServant)
                .desLevel(servant.desLevelProperty().intValue())
                .desSkill1(servant.desSkill1Property().intValue())
                .desSkill2(servant.desSkill2Property().intValue())
                .desSkill3(servant.desSkill3Property().intValue())
                .skillMaterials(servant.getSkillMaterials())
                .ascensionMaterials(servant.getAscensionMaterials())
                .build();
    }

    public List<PlannerServant> transformAllFromViews(List<PlannerServantView> servants) {
        return servants.stream().map(this::transform).collect(Collectors.toList());
    }
}
