package com.github.theintelligentone.fgotracker.domain.servant.factory;

import com.github.theintelligentone.fgotracker.domain.item.UpgradeCost;
import com.github.theintelligentone.fgotracker.domain.view.UserServantView;
import com.github.theintelligentone.fgotracker.service.datamanagement.DataManagementServiceFacade;
import javafx.beans.Observable;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlannerServantViewFactory {

    private static final int MAX_SKILL_LEVEL = 10;

    public ObservableList<UserServantView> createForLTPlanner(ObservableList<UserServantView> servants) {
        ObservableList<UserServantView> result = FXCollections.observableArrayList(
                param -> new Observable[]{param.baseServantProperty(), param.desLevelProperty(), param.desSkill1Property(), param.desSkill2Property(), param.desSkill3Property()});
        result.addAll(servants.stream().map(this::createFromUserServantForLTPlanner).collect(Collectors.toList()));
        servants.addListener((ListChangeListener<? super UserServantView>) c -> {
            result.clear();
            result.addAll(c.getList().stream().map(this::createFromUserServantForLTPlanner).collect(Collectors.toList()));
        });
        return result;
    }

    public UserServantView createFromUserServant(UserServantView servant) {
        UserServantView result = new UserServantView();
        if (servant.baseServantProperty() != null && servant.baseServantProperty().getValue() != null) {
            result = UserServantView.builder()
                    .svtId(servant.svtIdProperty())
                    .desLevel(new SimpleIntegerProperty(servant.levelProperty().intValue()))
                    .desSkill1(new SimpleIntegerProperty(servant.skillLevel1Property().intValue()))
                    .desSkill2(new SimpleIntegerProperty(servant.skillLevel2Property().intValue()))
                    .desSkill3(new SimpleIntegerProperty(servant.skillLevel3Property().intValue()))
                    .ascensionMaterials(
                            convertMaterialMapToList(servant.baseServantProperty().getValue().getAscensionMaterials()))
                    .skillMaterials(convertMaterialMapToList(servant.baseServantProperty().getValue().getSkillMaterials()))
                    .build();
        }
        return result;
    }

    private UserServantView createFromUserServantForLTPlanner(UserServantView servant) {
        UserServantView result = new UserServantView();
        if (servant.baseServantProperty() != null && servant.baseServantProperty().getValue() != null) {
            result = UserServantView.builder()
                    .svtId(servant.svtIdProperty())
                    .desLevel(new SimpleIntegerProperty(
                            DataManagementServiceFacade.MAX_LEVELS[servant.baseServantProperty().getValue().getRarity()]))
                    .desSkill1(new SimpleIntegerProperty(MAX_SKILL_LEVEL))
                    .desSkill2(new SimpleIntegerProperty(MAX_SKILL_LEVEL))
                    .desSkill3(new SimpleIntegerProperty(MAX_SKILL_LEVEL))
                    .ascensionMaterials(
                            convertMaterialMapToList(servant.baseServantProperty().getValue().getAscensionMaterials()))
                    .skillMaterials(convertMaterialMapToList(servant.baseServantProperty().getValue().getSkillMaterials()))
                    .build();
        }
        return result;
    }

    private List<UpgradeCost> convertMaterialMapToList(Map<Integer, UpgradeCost> materials) {
        return materials.entrySet().stream().sorted(Map.Entry.comparingByKey()).map(Map.Entry::getValue).collect(
                Collectors.toList());
    }

    public UserServantView createFromPreviousUserServant(UserServantView servant,
                                                         UserServantView previousServant) {
        UserServantView result = new UserServantView();
        if (servant.baseServantProperty() != null && servant.baseServantProperty().getValue() != null) {
            result = UserServantView.builder()
                    .svtId(servant.svtIdProperty())
                    .desLevel(new SimpleIntegerProperty(previousServant.desLevelProperty().intValue()))
                    .desSkill1(new SimpleIntegerProperty(previousServant.desSkill1Property().intValue()))
                    .desSkill2(new SimpleIntegerProperty(previousServant.desSkill1Property().intValue()))
                    .desSkill3(new SimpleIntegerProperty(previousServant.desSkill1Property().intValue()))
                    .ascensionMaterials(
                            convertMaterialMapToList(servant.baseServantProperty().getValue().getAscensionMaterials()))
                    .skillMaterials(convertMaterialMapToList(servant.baseServantProperty().getValue().getSkillMaterials()))
                    .build();
        }
        return result;
    }
}
