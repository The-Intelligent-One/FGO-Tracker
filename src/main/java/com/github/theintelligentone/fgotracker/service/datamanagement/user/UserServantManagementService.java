package com.github.theintelligentone.fgotracker.service.datamanagement.user;

import com.github.theintelligentone.fgotracker.domain.other.PlannerType;
import com.github.theintelligentone.fgotracker.domain.servant.PlannerServant;
import com.github.theintelligentone.fgotracker.domain.servant.Servant;
import com.github.theintelligentone.fgotracker.domain.servant.UserServant;
import com.github.theintelligentone.fgotracker.domain.servant.factory.PlannerServantFactory;
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
    private ObservableList<PlannerServant> plannerServantList;
    private ObservableList<PlannerServant> priorityPlannerServantList;
    private ObservableList<PlannerServant> longTermPlannerServantList;


    public void initDataLists() {
        userServantList = new HashSet<>();
        rosterServantList = FXCollections.observableArrayList();
        plannerServantList = FXCollections.observableArrayList();
        priorityPlannerServantList = FXCollections.observableArrayList();
        longTermPlannerServantList = FXCollections.observableArrayList();
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
                rosterServantList.set(index, newServant);
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
                rosterServantList.set(index, newServant);
            }
        }
    }

    public void eraseUserServant(int index) {
        rosterServantList.set(index, new UserServant());
    }

    public ObservableList<UserServant> getPaddedUserServantList() {
        padUserServantList(rosterServantList);
        return rosterServantList;
    }

    public ObservableList<PlannerServant> getPaddedPlannerServantList(PlannerType plannerType) {
        ObservableList<PlannerServant> sourceList = getPlannerServantList(plannerType);
        padPlannerServantList(sourceList);
        return sourceList;
    }

    private void padPlannerServantList(ObservableList<PlannerServant> sourceList) {
        if (sourceList.size() < MIN_TABLE_SIZE) {
            IntStream.range(0, MIN_TABLE_SIZE - sourceList.size()).forEach(i -> sourceList.add(new PlannerServant()));
        }
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
        List<UserServant> newRoster = createNewListWithUserServants(importedServants);
        rosterServantList.setAll(clearUnnecessaryEmptyUserRows(newRoster));
        padUserServantList(rosterServantList);
    }

    public void saveImportedPlannerServants(PlannerType plannerType,
                                            List<PlannerServant> importedServants) {
        List<PlannerServant> newPlanner = createNewListWithPlannerServants(importedServants);
        getPlannerServantList(plannerType).setAll(clearUnnecessaryEmptyPlannerRows(newPlanner));
        padPlannerServantList(getPlannerServantList(plannerType));
    }

    private List<UserServant> createNewListWithUserServants(List<UserServant> importedServants) {
        List<UserServant> newRoster = new ArrayList<>();
        importedServants.forEach(userServant -> {
                    Optional<UserServant> existingUserServant = userServantList.stream()
                            .filter(existingServant -> existingServant.getSvtId() == userServant.getSvtId())
                            .findFirst();
                    existingUserServant.ifPresentOrElse(oldServant -> {
                        copyNewValuesIfApplicable(oldServant, userServant);
                        newRoster.add(oldServant);
                    }, () -> newRoster.add(userServant));
                }
        );
        return newRoster;
    }

    private List<PlannerServant> createNewListWithPlannerServants(List<PlannerServant> importedServants) {
        List<PlannerServant> newPlanner = new ArrayList<>();
        importedServants.forEach(userServant -> {
                    Optional<UserServant> existingUserServant = userServantList.stream()
                            .filter(existingServant -> existingServant.getSvtId() == userServant.getSvtId())
                            .findFirst();
                    existingUserServant.ifPresentOrElse(oldServant -> {
                        copyNewValuesIfApplicable(oldServant, userServant.getBaseServant());
                        newPlanner.add(userServant);
                    }, () -> newPlanner.add(userServant));
                }
        );
        return newPlanner;
    }

    public void refreshUserServants(List<UserServant> userServants, List<Servant> servantList) {
        rosterServantList = FXCollections.observableArrayList();
        List<UserServant> associatedUserServantList = createAssociatedUserServantList(userServants, servantList);
        userServantList.addAll(associatedUserServantList.stream()
                .filter(userServant -> userServant.getSvtId() != 0)
                .collect(Collectors.toList()));
        rosterServantList.addAll(associatedUserServantList);
    }

    public void refreshPlannerServants(PlannerType plannerType, List<PlannerServant> plannerServants, List<Servant> servantList) {
        ObservableList<PlannerServant> plannerServantList = getPlannerServantList(plannerType);
        plannerServantList.clear();
        List<UserServant> associatedUserServantList = createAssociatedUserServantListForPlannerRefresh(plannerServants, servantList);
        userServantList.addAll(associatedUserServantList.stream()
                .filter(userServant -> userServant.getSvtId() != 0)
                .collect(Collectors.toList()));
        plannerServantList.setAll(plannerServants);
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
                result.add(UserServantFactory.createBlankUserServant());
            }
        });
        return result;
    }

    private List<UserServant> createAssociatedUserServantListForPlannerRefresh(List<PlannerServant> userServants, List<Servant> servantList) {
        List<UserServant> result = new ArrayList<>();
        userServants.forEach(svt -> {
            if (svt.getSvtId() != 0) {
                Optional<UserServant> optionalUserServant = userServantList.stream()
                        .filter(userServant -> userServant.getSvtId() == svt.getSvtId())
                        .findFirst();
                UserServant newUserServant = optionalUserServant.orElseGet(() -> UserServantFactory.createUserServantFromBaseServant(findServantById(svt.getSvtId(), servantList)));
                result.add(newUserServant);
                userServants.set(userServants.indexOf(svt), PlannerServantFactory.copyWithNewBaseServant(svt, newUserServant));
            } else {
                result.add(UserServantFactory.createBlankUserServant());
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
        if (svt.getAppendSkillLevel1() > 1) {
            oldSvt.setAppendSkillLevel1(svt.getAppendSkillLevel1());
        }
        if (svt.getAppendSkillLevel2() > 1) {
            oldSvt.setAppendSkillLevel2(svt.getAppendSkillLevel2());
        }
        if (svt.getAppendSkillLevel3() > 1) {
            oldSvt.setAppendSkillLevel3(svt.getAppendSkillLevel3());
        }
        if (!"".equals(svt.getNotes())) {
            oldSvt.setNotes(svt.getNotes());
        }
        if (svt.getNpLevel() > 1) {
            oldSvt.setNpLevel(svt.getNpLevel());
        }
    }

    private Servant findServantById(long svtId, List<Servant> servantList) {
        return servantList.stream().filter(svt -> svtId == svt.getId()).findFirst().orElseThrow();
    }

    public List<UserServant> getClearedUserServantList() {
        return clearUnnecessaryEmptyUserRows(rosterServantList);
    }

    private ObservableList<PlannerServant> getPlannerServantList(PlannerType plannerType) {
        ObservableList<PlannerServant> chosenPlannerList;
        switch (plannerType) {
            case REGULAR:
                chosenPlannerList = plannerServantList;
                break;
            case PRIORITY:
                chosenPlannerList = priorityPlannerServantList;
                break;
            case LT:
                chosenPlannerList = longTermPlannerServantList;
                break;
            default:
                chosenPlannerList = FXCollections.observableArrayList();
        }
        return chosenPlannerList;
    }

    public void erasePlannerServant(PlannerServant servant, PlannerType plannerType) {
        ObservableList<PlannerServant> plannerList = getPlannerServantList(plannerType);
        plannerList.set(plannerList.indexOf(servant), new PlannerServant());
    }

    public void removePlannerServant(PlannerServant servant, PlannerType plannerType) {
        getPlannerServantList(plannerType).remove(servant);
    }

    public void savePlannerServant(PlannerServant plannerServant, PlannerType plannerType) {
        getPlannerServantList(plannerType).add(plannerServant);
    }

    public void savePlannerServant(int index, PlannerServant plannerServant, PlannerType plannerType) {
        getPlannerServantList(plannerType).add(index, plannerServant);
    }

    public void replaceBaseServantInPlannerRow(int index, PlannerServant servant, Servant newBaseServant, PlannerType plannerType) {
        ObservableList<PlannerServant> listToUpdate = getPlannerServantList(plannerType);
        if (newBaseServant != null) {
            Optional<UserServant> optionalUserServant = userServantList.stream()
                    .filter(userServant -> userServant.getSvtId() == newBaseServant.getId())
                    .findFirst();
            if (servant.getSvtId() == 0) {
                UserServant actualBaseServant = optionalUserServant.orElseGet(() -> {
                    UserServant userServantFromBaseServant = UserServantFactory.createUserServantFromBaseServant(newBaseServant);
                    userServantList.add(userServantFromBaseServant);
                    return userServantFromBaseServant;
                });
                PlannerServant newServant = PlannerServantFactory.createPlannerServantFromBaseServant(actualBaseServant);
                listToUpdate.set(index, newServant);
            } else {
                PlannerServant newServant;
                if (optionalUserServant.isEmpty()) {
                    UserServant actualBaseServant = UserServantFactory.createUserServantFromBaseServant(newBaseServant);
                    userServantList.add(actualBaseServant);
                    newServant = servant.toBuilder()
                            .svtId(newBaseServant.getId())
                            .baseServant(actualBaseServant)
                            .build();
                } else {
                    newServant = PlannerServantFactory.createPlannerServantFromBaseServant(optionalUserServant.get());
                }
                listToUpdate.set(index, newServant);
            }
        }
    }

    public List<PlannerServant> getClearedPlannerServantList(PlannerType plannerType) {
        return clearUnnecessaryEmptyPlannerRows(getPlannerServantList(plannerType));
    }

    private List<PlannerServant> clearUnnecessaryEmptyPlannerRows(List<PlannerServant> servantList) {
        List<PlannerServant> newList = new ArrayList<>(servantList);
        int index = newList.size() - 1;
        while (!newList.isEmpty() && newList.get(index).getSvtId() == 0) {
            newList.remove(index--);
        }
        return newList;
    }
}
