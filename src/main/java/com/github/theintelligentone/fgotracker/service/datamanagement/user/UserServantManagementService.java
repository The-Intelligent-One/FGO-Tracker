package com.github.theintelligentone.fgotracker.service.datamanagement.user;

import com.github.theintelligentone.fgotracker.domain.other.PlannerType;
import com.github.theintelligentone.fgotracker.domain.servant.Servant;
import com.github.theintelligentone.fgotracker.domain.servant.UserServant;
import com.github.theintelligentone.fgotracker.domain.servant.factory.UserServantFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.theintelligentone.fgotracker.service.datamanagement.DataManagementServiceFacade.MIN_TABLE_SIZE;

@Component
public class UserServantManagementService {
    private Set<UserServant> userServantList;
    private ObservableList<UserServant> rosterServantList;
    private ObservableList<UserServant> plannerServantList;
    private ObservableList<UserServant> priorityPlannerServantList;


    public void initDataLists() {
        userServantList = new HashSet<>();
        rosterServantList = FXCollections.observableArrayList();
        plannerServantList = FXCollections.observableArrayList();
        priorityPlannerServantList = FXCollections.observableArrayList();
    }

    private List<UserServant> clearUnnecessaryEmptyUserRows(List<UserServant> servantList) {
        List<UserServant> newList = new ArrayList<>(servantList);
        int index = newList.size() - 1;
        while (!newList.isEmpty() && newList.get(index).getSvtId() == 0) {
            newList.remove(index--);
        }
        return newList;
    }

    public void saveUserServant(UserServant servant) {
        rosterServantList.add(servant);
    }

    public void saveUserServant(int index, UserServant servant) {
        rosterServantList.add(index, servant);
    }

    public void replaceBaseServantInRosterRow(int index, UserServant servant, Servant newBaseServant) {
        replaceBaseServantInRow(index, servant, newBaseServant, rosterServantList);
    }

    public void eraseUserServant(int index) {
        rosterServantList.set(index, new UserServant());
    }

    public ObservableList<UserServant> getPaddedUserServantList() {
        padUserServantList(rosterServantList);
        return rosterServantList;
    }

    public ObservableList<UserServant> getPaddedPlannerServantList(PlannerType plannerType) {
        ObservableList<UserServant> sourceList = getPlannerServantList(plannerType);
        padUserServantList(sourceList);
        return sourceList;
    }

    private void padUserServantList(ObservableList<UserServant> sourceList) {
        if (sourceList.size() < MIN_TABLE_SIZE) {
            IntStream.range(0, MIN_TABLE_SIZE - sourceList.size()).forEach(i -> sourceList.add(new UserServant()));
        }
    }

    public void removeUserServant(int index) {
        rosterServantList.remove(index);
    }

    public void saveImportedUserServants(List<UserServant> importedServants) {
        List<UserServant> newRoster = new ArrayList<>();
        importedServants.forEach(userServant -> {
                    Optional<UserServant> existingUserServant = userServantList.stream().filter(existingServant -> existingServant.getSvtId() == userServant.getSvtId()).findFirst();
                    existingUserServant.ifPresentOrElse(oldServant -> {
                        oldServant.setBondLevel(userServant.getBondLevel());
                        oldServant.setNpLevel(userServant.getNpLevel());
                        oldServant.setLevel(userServant.getLevel());
                        oldServant.setNotes(userServant.getNotes());
                        oldServant.setFouHp(userServant.getFouHp());
                        oldServant.setFouAtk(userServant.getFouAtk());
                        oldServant.setSkillLevel1(userServant.getSkillLevel1());
                        oldServant.setSkillLevel2(userServant.getSkillLevel2());
                        oldServant.setSkillLevel3(userServant.getSkillLevel3());
                        newRoster.add(oldServant);
                    }, () -> newRoster.add(userServant));
                }
        );
        rosterServantList.setAll(clearUnnecessaryEmptyUserRows(newRoster));
        padUserServantList(rosterServantList);
    }

    public void refreshUserServants(List<UserServant> userServants, List<Servant> servantList) {
        rosterServantList = FXCollections.observableArrayList();
        List<UserServant> associatedUserServantList = createAssociatedUserServantList(userServants, servantList);
        userServantList.addAll(associatedUserServantList.stream()
                .filter(userServant -> userServant.getSvtId() != 0)
                .collect(Collectors.toList()));
        rosterServantList.addAll(associatedUserServantList);
    }

    public void refreshPlannerServants(PlannerType plannerType, List<UserServant> plannerServants, List<Servant> servantList) {
        ObservableList<UserServant> plannerServantList = getPlannerServantList(plannerType);
        plannerServantList.clear();
        List<UserServant> associatedUserServantList = createAssociatedUserServantList(plannerServants, servantList);
        userServantList.addAll(associatedUserServantList.stream()
                .filter(userServant -> userServant.getSvtId() != 0)
                .collect(Collectors.toList()));
        plannerServantList.setAll(associatedUserServantList);
    }

    private List<UserServant> createAssociatedUserServantList(List<UserServant> userServants, List<Servant> servantList) {
        List<UserServant> result = new ArrayList<>();
        userServants.forEach(svt -> {
            if (svt.getSvtId() != 0) {
                Optional<UserServant> optionalUserServant = userServantList.stream()
                        .filter(userServant -> userServant.getSvtId() == svt.getSvtId())
                        .findFirst();
                if (optionalUserServant.isPresent()) {
                    UserServant oldSvt = optionalUserServant.get();
                    copyNewValuesIfApplicable(oldSvt, svt);
                }
                result.add(optionalUserServant.orElseGet(() -> UserServantFactory.copyWithNewBaseServant(svt, findServantById(svt.getSvtId(), servantList))));
            } else {
                result.add(svt);
            }
        });
        return result;
    }

    private void copyNewValuesIfApplicable(UserServant oldSvt, UserServant svt) {
        if (svt.getFouAtk() > 0) {
            oldSvt.setFouAtk(svt.getFouAtk());
        }
        if (svt.getFouHp() > 0) {
            oldSvt.setFouHp(svt.getFouHp());
        }
        if (svt.getBondLevel() > 0) {
            oldSvt.setBondLevel(svt.getBondLevel());
        }
        if (svt.getDesLevel() > 1) {
            oldSvt.setDesLevel(svt.getDesLevel());
        }
        if (svt.getDesSkill1() > 1) {
            oldSvt.setDesSkill1(svt.getDesSkill1());
        }
        if (svt.getDesSkill2() > 1) {
            oldSvt.setDesSkill2(svt.getDesSkill2());
        }
        if (svt.getDesSkill3() > 1) {
            oldSvt.setDesSkill3(svt.getDesSkill3());
        }
        if (svt.getLevel() > 1) {
            oldSvt.setLevel(svt.getLevel());
        }
        if (svt.getSkillLevel1() > 1) {
            oldSvt.setSkillLevel1(svt.getSkillLevel1());
        }
        if (svt.getSkillLevel2() > 1) {
            oldSvt.setSkillLevel2(svt.getSkillLevel2());
        }
        if (svt.getSkillLevel3() > 1) {
            oldSvt.setSkillLevel3(svt.getSkillLevel3());
        }
        if (!"".equals(svt.getNotes())) {
            oldSvt.setNotes(svt.getNotes());
        }
        if (svt.getNpLevel() > 1) {
            oldSvt.setNpLevel(svt.getNpLevel());
        }
    }

    private Servant findServantById(long svtId, List<Servant> servantList) {
        return servantList.stream().filter(svt -> svtId == svt.getId()).findFirst().get();
    }

    public List<UserServant> getClearedUserServantList() {
        return clearUnnecessaryEmptyUserRows(rosterServantList);
    }

    private ObservableList<UserServant> getPlannerServantList(PlannerType plannerType) {
        ObservableList<UserServant> chosenPlannerList;
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

    public void erasePlannerServant(UserServant servant, PlannerType plannerType) {
        ObservableList<UserServant> plannerList = getPlannerServantList(plannerType);
        plannerList.set(plannerList.indexOf(servant), new UserServant());
    }

    public void removePlannerServant(UserServant servant, PlannerType plannerType) {
        getPlannerServantList(plannerType).remove(servant);
    }

    public void savePlannerServant(UserServant plannerServantView, PlannerType plannerType) {
        getPlannerServantList(plannerType).add(plannerServantView);
    }

    public void savePlannerServant(int index, UserServant plannerServantView, PlannerType plannerType) {
        getPlannerServantList(plannerType).add(index, plannerServantView);
    }

    public void replaceBaseServantInPlannerRow(int index, UserServant servant, Servant newBaseServant, PlannerType plannerType) {
        ObservableList<UserServant> listToUpdate = getPlannerServantList(plannerType);
        replaceBaseServantInRow(index, servant, newBaseServant, listToUpdate);
    }

    private void replaceBaseServantInRow(int index, UserServant servant, Servant newBaseServant, ObservableList<UserServant> listToUpdate) {
        if (newBaseServant != null) {
            Optional<UserServant> optionalUserServant = userServantList.stream()
                    .filter(userServant -> userServant.getSvtId() == newBaseServant.getId())
                    .findFirst();
            if (servant.getSvtId() == 0) {
                UserServant newServant = optionalUserServant.orElseGet(() -> {
                    UserServant userServantFromBaseServant = UserServantFactory.createUserServantFromBaseServant(newBaseServant);
                    userServantList.add(userServantFromBaseServant);
                    return userServantFromBaseServant;
                });
                listToUpdate.set(index, newServant);
            } else {
                UserServant newServant;
                if (optionalUserServant.isEmpty()) {
                    newServant = servant.toBuilder()
                            .svtId(newBaseServant.getId())
                            .rarity(newBaseServant.getRarity())
                            .svtClass(newBaseServant.getClassName())
                            .baseServant(newBaseServant)
                            .build();
                    userServantList.add(newServant);
                } else {
                    newServant = optionalUserServant.get();
                }
                listToUpdate.set(index, newServant);
            }
        }
    }

    public List<UserServant> getClearedPlannerServantList(PlannerType plannerType) {
        return clearUnnecessaryEmptyPlannerRows(getPlannerServantList(plannerType));
    }

    private List<UserServant> clearUnnecessaryEmptyPlannerRows(List<UserServant> servantList) {
        List<UserServant> newList = new ArrayList<>(servantList);
        int index = newList.size() - 1;
        while (!newList.isEmpty() && newList.get(index).getSvtId() == 0) {
            newList.remove(index--);
        }
        return newList;
    }

    public void saveImportedPlannerServants(PlannerType plannerType,
                                            List<UserServant> importedServants) {
        getPlannerServantList(plannerType).setAll(clearUnnecessaryEmptyPlannerRows(importedServants));
    }
}
