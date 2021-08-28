package com.github.theintelligentone.fgotracker.service.datamanagement;

import com.github.theintelligentone.fgotracker.domain.other.PlannerType;
import com.github.theintelligentone.fgotracker.domain.servant.PlannerServant;
import com.github.theintelligentone.fgotracker.domain.servant.factory.PlannerServantViewFactory;
import com.github.theintelligentone.fgotracker.domain.view.PlannerServantView;
import com.github.theintelligentone.fgotracker.domain.view.UserServantView;
import com.github.theintelligentone.fgotracker.service.transformer.PlannerServantToViewTransformer;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static com.github.theintelligentone.fgotracker.service.datamanagement.DataManagementServiceFacade.MIN_TABLE_SIZE;

public class PlannerManagementService {
    private final PlannerServantToViewTransformer plannerServantToViewTransformer;
    private ObservableList<PlannerServantView> plannerServantList;
    private ObservableList<PlannerServantView> priorityPlannerServantList;

    public PlannerManagementService() {plannerServantToViewTransformer = new PlannerServantToViewTransformer();}

    public ObservableList<PlannerServantView> getPaddedPlannerServantList(PlannerType plannerType) {
        ObservableList<PlannerServantView> sourceList = getPlannerServantList(plannerType);
        if (sourceList.size() < MIN_TABLE_SIZE) {
            IntStream.range(0, MIN_TABLE_SIZE - sourceList.size()).forEach(
                    i -> sourceList.add(new PlannerServantView()));
        }
        return sourceList;
    }

    private ObservableList<PlannerServantView> getPlannerServantList(PlannerType plannerType) {
        ObservableList<PlannerServantView> chosenPlannerList;
        switch (plannerType) {
            case REGULAR:
                chosenPlannerList = plannerServantList;
                break;
            case PRIORITY:
                chosenPlannerList = priorityPlannerServantList;
                break;
            default:
                chosenPlannerList = FXCollections.observableArrayList();
        }
        return chosenPlannerList;
    }

    public void initDataLists() {
        plannerServantList = FXCollections.observableArrayList(
                param -> new Observable[]{param.baseServantProperty(), param.desLevelProperty(), param.desSkill1Property(), param.desSkill2Property(), param.desSkill3Property()});
        priorityPlannerServantList = FXCollections.observableArrayList(
                param -> new Observable[]{param.baseServantProperty(), param.desLevelProperty(), param.desSkill1Property(), param.desSkill2Property(), param.desSkill3Property()});
    }

    private List<PlannerServantView> createAssociatedPlannerServantList(List<PlannerServant> servants,
                                                                        ObservableList<UserServantView> userServants) {
        List<PlannerServantView> plannerServants = plannerServantToViewTransformer.transformAll(
                servants);
        plannerServants.forEach(svt -> {
            if (svt.svtIdProperty().longValue() != 0L) {
                svt.baseServantProperty().set(findUserServantById(svt.svtIdProperty().longValue(), userServants));
            }
        });
        return plannerServants;
    }

    public UserServantView findUserServantById(long svtId, List<UserServantView> userServantList) {
        return userServantList.stream().filter(
                svt -> svtId == svt.svtIdProperty().longValue()).findFirst().get();
    }

    public void refreshPlannerServants(PlannerType plannerType, List<PlannerServant> plannerServants,
                                       ObservableList<UserServantView> userServants) {
        getPlannerServantList(plannerType).addAll(createAssociatedPlannerServantList(plannerServants, userServants));
    }

    public void erasePlannerServant(PlannerServantView servant, PlannerType plannerType) {
        ObservableList<PlannerServantView> plannerList = getPlannerServantList(plannerType);
        plannerList.set(plannerList.indexOf(servant), new PlannerServantView());
    }

    public void removePlannerServant(PlannerServantView servant, PlannerType plannerType) {
        getPlannerServantList(plannerType).remove(servant);
    }

    public void savePlannerServant(PlannerServantView plannerServantView, PlannerType plannerType) {
        getPlannerServantList(plannerType).add(plannerServantView);
    }

    public void savePlannerServant(int index, PlannerServantView plannerServantView, PlannerType plannerType) {
        getPlannerServantList(plannerType).add(index, plannerServantView);
    }

    public void replaceBaseServantInPlannerRow(int index, PlannerServantView servant,
                                               UserServantView newBaseServant, PlannerType plannerType) {
        if (newBaseServant != null) {
            PlannerServantView fromUserServant;
            if (servant.baseServantProperty().getValue() == null) {
                fromUserServant = new PlannerServantViewFactory().createFromUserServant(newBaseServant);
            } else {
                fromUserServant = new PlannerServantViewFactory().createFromPreviousUserServant(newBaseServant, servant);
            }
            getPlannerServantList(plannerType).set(index, fromUserServant);
        }
    }

    public List<PlannerServant> getClearedPlannerServantList(PlannerType plannerType) {
        return plannerServantToViewTransformer.transformAllFromViews(
                clearUnnecessaryEmptyPlannerRows(getPlannerServantList(plannerType)));
    }

    private List<PlannerServantView> clearUnnecessaryEmptyPlannerRows(List<PlannerServantView> servantList) {
        List<PlannerServantView> newList = new ArrayList<>(servantList);
        int index = newList.size() - 1;
        while (!newList.isEmpty() && newList.get(index).baseServantProperty().getValue() == null) {
            newList.remove(index--);
        }
        return newList;
    }

    public void saveImportedPlannerServants(PlannerType plannerType,
                                            List<PlannerServantView> importedServants) {
        getPlannerServantList(plannerType).setAll(clearUnnecessaryEmptyPlannerRows(importedServants));
    }
}
