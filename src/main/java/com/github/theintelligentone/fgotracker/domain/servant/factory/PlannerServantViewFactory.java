package com.github.theintelligentone.fgotracker.domain.servant.factory;

import com.github.theintelligentone.fgotracker.domain.item.UpgradeCost;
import com.github.theintelligentone.fgotracker.domain.view.PlannerServantView;
import com.github.theintelligentone.fgotracker.domain.view.UserServantView;
import com.github.theintelligentone.fgotracker.service.DataManagementService;
import javafx.beans.Observable;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlannerServantViewFactory {

    private static final int MAX_SKILL_LEVEL = 10;

    public ObservableList<PlannerServantView> createForLTPlanner(ObservableList<UserServantView> servants) {
        ObservableList<PlannerServantView> result = FXCollections.observableArrayList(param -> new Observable[]{param.getBaseServant(), param.getDesLevel(), param.getDesSkill1(), param.getDesSkill2(), param.getDesSkill3()});
        result.addAll(servants.stream().map(this::createFromUserServantForLTPlanner).collect(Collectors.toList()));
        servants.addListener((ListChangeListener<? super UserServantView>) c -> {
            result.clear();
            result.addAll(c.getList().stream().map(this::createFromUserServantForLTPlanner).collect(Collectors.toList()));
        });
        return result;
    }

    public List<PlannerServantView> createFromEachUserServant(List<UserServantView> servants) {
        return servants.stream().map(this::createFromUserServant).collect(Collectors.toList());
    }

    public PlannerServantView createFromUserServant(List<UserServantView> servants, int index) {
        return createFromUserServant(servants.get(index));
    }

    private PlannerServantView createFromUserServant(UserServantView servant) {
        PlannerServantView result = new PlannerServantView();
        if (servant.getBaseServant() != null && servant.getBaseServant().getValue() != null) {
            result = PlannerServantView.builder()
                    .svtId(servant.getSvtId())
                    .baseServant(new SimpleObjectProperty<>(servant))
                    .desLevel(new SimpleIntegerProperty(servant.getLevel().intValue()))
                    .desSkill1(new SimpleIntegerProperty(servant.getSkillLevel1().intValue()))
                    .desSkill2(new SimpleIntegerProperty(servant.getSkillLevel2().intValue()))
                    .desSkill3(new SimpleIntegerProperty(servant.getSkillLevel3().intValue()))
                    .ascensionMaterials(convertMaterialMapToList(servant.getBaseServant().getValue().getAscensionMaterials()))
                    .skillMaterials(convertMaterialMapToList(servant.getBaseServant().getValue().getSkillMaterials()))
                    .build();
        }
        return result;
    }

    private PlannerServantView createFromUserServantForLTPlanner(UserServantView servant) {
        PlannerServantView result = new PlannerServantView();
        if (servant.getBaseServant() != null && servant.getBaseServant().getValue() != null) {
            result = PlannerServantView.builder()
                    .svtId(servant.getSvtId())
                    .baseServant(new SimpleObjectProperty<>(servant))
                    .desLevel(new SimpleIntegerProperty(DataManagementService.MAX_LEVELS[servant.getBaseServant().getValue().getRarity()]))
                    .desSkill1(new SimpleIntegerProperty(MAX_SKILL_LEVEL))
                    .desSkill2(new SimpleIntegerProperty(MAX_SKILL_LEVEL))
                    .desSkill3(new SimpleIntegerProperty(MAX_SKILL_LEVEL))
                    .ascensionMaterials(convertMaterialMapToList(servant.getBaseServant().getValue().getAscensionMaterials()))
                    .skillMaterials(convertMaterialMapToList(servant.getBaseServant().getValue().getSkillMaterials()))
                    .build();
        }
        return result;
    }

    private List<UpgradeCost> convertMaterialMapToList(Map<Integer, UpgradeCost> materials) {
        return materials.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey)).map(Map.Entry::getValue).collect(Collectors.toList());
    }
}
