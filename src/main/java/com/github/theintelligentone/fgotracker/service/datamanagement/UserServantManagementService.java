package com.github.theintelligentone.fgotracker.service.datamanagement;

import com.github.theintelligentone.fgotracker.domain.servant.Servant;
import com.github.theintelligentone.fgotracker.domain.servant.UserServant;
import com.github.theintelligentone.fgotracker.domain.servant.factory.UserServantFactory;
import com.github.theintelligentone.fgotracker.domain.view.PlannerServantView;
import com.github.theintelligentone.fgotracker.domain.view.UserServantView;
import com.github.theintelligentone.fgotracker.service.transformer.UserServantToViewTransformer;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.theintelligentone.fgotracker.service.datamanagement.DataManagementServiceFacade.MIN_TABLE_SIZE;
import static com.github.theintelligentone.fgotracker.service.datamanagement.DataManagementServiceFacade.NAME_FORMAT;

public class UserServantManagementService {
    private final UserServantToViewTransformer userServantToViewTransformer;
    private ObservableList<UserServantView> userServantList;
    @Getter
    private ObservableList<String> userServantNameList;

    public UserServantManagementService(
            UserServantToViewTransformer userServantToViewTransformer) {this.userServantToViewTransformer = userServantToViewTransformer;}

    public void initDataLists(List<PlannerServantView> plannerServantList,
                              List<PlannerServantView> priorityPlannerServantList) {
        userServantList = FXCollections.observableArrayList(
                param -> new Observable[]{param.svtIdProperty(), param.levelProperty(), param.skillLevel1Property(), param.skillLevel2Property(), param.skillLevel3Property(), param.ascensionProperty()});
        addListenersForRemovingFromPlanners(plannerServantList, priorityPlannerServantList);
        addListenersForUpdatingNameList();
        userServantNameList = FXCollections.observableArrayList();
    }

    private void addListenersForUpdatingNameList() {
        userServantList.addListener((ListChangeListener.Change<? extends UserServantView> c) -> {
            userServantNameList.clear();
            userServantNameList.addAll(c.getList().stream()
                    .filter(svt -> svt.baseServantProperty().getValue() != null)
                    .map(svt -> String.format(NAME_FORMAT, svt.baseServantProperty().getValue().getName(),
                            svt.baseServantProperty().getValue().getRarity(),
                            svt.baseServantProperty().getValue().getClassName()))
                    .collect(Collectors.toList()));
        });
    }

    private void addListenersForRemovingFromPlanners(List<PlannerServantView> plannerServantList,
                                                     List<PlannerServantView> priorityPlannerServantList) {
        userServantList.addListener((ListChangeListener.Change<? extends UserServantView> c) -> {
            List<Long> ids = c.getList().stream().map(svt -> svt.svtIdProperty().get()).collect(Collectors.toList());
            plannerServantList.removeIf(
                    svt -> svt.baseServantProperty().getValue() != null && !ids.contains(svt.svtIdProperty().longValue()));
            forcePlannerListUpdates(plannerServantList, priorityPlannerServantList);
        });
    }

    private void forcePlannerListUpdates(List<PlannerServantView> plannerServantList,
                                         List<PlannerServantView> priorityPlannerServantList) {
        PlannerServantView dummy = new PlannerServantView();
        plannerServantList.add(dummy);
        plannerServantList.remove(dummy);
        priorityPlannerServantList.add(dummy);
        priorityPlannerServantList.remove(dummy);
    }

    private List<UserServantView> clearUnnecessaryEmptyUserRows(List<UserServantView> servantList) {
        List<UserServantView> newList = new ArrayList<>(servantList);
        int index = newList.size() - 1;
        while (!newList.isEmpty() && newList.get(index).baseServantProperty().getValue() == null) {
            newList.remove(index--);
        }
        return newList;
    }

    public void saveUserServant(UserServantView servant) {
        userServantList.add(servant);
    }

    public void saveUserServant(int index, UserServantView servant) {
        userServantList.add(index, servant);
    }

    public void replaceBaseServantInRow(int index, UserServantView servant, Servant newBaseServant) {
        if (newBaseServant != null) {
            if (servant.baseServantProperty() == null || servant.baseServantProperty().getValue() == null) {
                userServantList.set(index, userServantToViewTransformer.transform(
                        new UserServantFactory().createUserServantFromBaseServant(newBaseServant)));
            } else {
                servant.svtIdProperty().set(newBaseServant.getId());
                servant.rarityProperty().set(newBaseServant.getRarity());
                servant.baseServantProperty().set(newBaseServant);
                userServantList.set(index, servant);
            }
        }
    }

    public void eraseUserServant(UserServantView servant) {
        userServantList.set(userServantList.indexOf(servant), new UserServantView());
    }

    public UserServantView findUserServantByFormattedName(String name) {
        return userServantList.stream()
                .filter(svt -> svt.baseServantProperty().getValue() != null)
                .filter(svt -> name.equalsIgnoreCase(String.format(NAME_FORMAT, svt.baseServantProperty().getValue().getName(),
                        svt.baseServantProperty().getValue().getRarity(),
                        svt.baseServantProperty().getValue().getClassName())))
                .findFirst().orElse(null);
    }

    public ObservableList<UserServantView> getPaddedUserServantList() {
        if (userServantList.size() < MIN_TABLE_SIZE) {
            IntStream.range(0, MIN_TABLE_SIZE - userServantList.size()).forEach(
                    i -> saveUserServant(new UserServantView()));
        }
        return userServantList;
    }

    public void removeUserServant(UserServantView servant) {
        userServantList.remove(servant);
    }

    public void saveImportedUserServants(List<UserServantView> importedServants) {
        userServantList.setAll(clearUnnecessaryEmptyUserRows(importedServants));
    }

    public void refreshUserServants(List<UserServant> userServants, List<Servant> servantList) {
        userServantList.addAll(createAssociatedUserServantList(userServants, servantList));
    }

    private List<UserServantView> createAssociatedUserServantList(
            List<UserServant> userServants,
            List<Servant> servantList) {
        userServants.forEach(svt -> {
            if (svt.getSvtId() != 0L) {
                svt.setBaseServant(findServantById(svt.getSvtId(), servantList));
            }
        });
        return userServantToViewTransformer.transformAllToViews(userServants);
    }

    private Servant findServantById(long svtId, List<Servant> servantList) {
        return servantList.stream().filter(svt -> svtId == svt.getId()).findFirst().get();
    }

    public List<UserServant> getClearedUserServantList() {
        return userServantToViewTransformer.transformAll(clearUnnecessaryEmptyUserRows(userServantList));
    }
}
