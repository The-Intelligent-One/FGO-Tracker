package com.github.theintelligentone.fgotracker.service.datamanagement.user;

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
import static com.github.theintelligentone.fgotracker.service.datamanagement.DataManagementServiceFacade.NAME_FORMAT;

@Component
public class UserServantManagementService {
    private Set<UserServant> userServantList;
    private ObservableList<UserServant> rosterServantList;


    public void initDataLists() {
        userServantList = new HashSet<>();
        rosterServantList = FXCollections.observableArrayList();
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

    public void replaceBaseServantInRow(int index, UserServant servant, Servant newBaseServant) {
        if (newBaseServant != null) {
            Optional<UserServant> optionalUserServant = userServantList.stream().filter(userServant -> userServant.getSvtId() == newBaseServant.getId()).findFirst();
            if (servant.getSvtId() == 0) {
                UserServant newServant = optionalUserServant.orElseGet(() -> {
                    UserServant userServantFromBaseServant = new UserServantFactory().createUserServantFromBaseServant(newBaseServant);
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
                            .baseServant(newBaseServant).build();
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

    public UserServant findUserServantByFormattedName(String name) {
        return rosterServantList.stream().filter(svt -> svt.getSvtId() != 0).filter(svt -> name.equalsIgnoreCase(String.format(NAME_FORMAT, svt.getBaseServant().getName(), svt.getBaseServant().getRarity(), svt.getBaseServant().getClassName()))).findFirst().orElse(null);
    }

    public ObservableList<UserServant> getPaddedUserServantList() {
        if (rosterServantList.size() < MIN_TABLE_SIZE) {
            IntStream.range(0, MIN_TABLE_SIZE - rosterServantList.size()).forEach(i -> saveUserServant(new UserServant()));
        }
        return rosterServantList;
    }

    public void removeUserServant(int index) {
        rosterServantList.remove(index);
    }

    public void saveImportedUserServants(List<UserServant> importedServants) {
        rosterServantList.setAll(clearUnnecessaryEmptyUserRows(importedServants));
    }

    public void refreshUserServants(List<UserServant> userServants, List<Servant> servantList) {
        initDataLists();
        List<UserServant> associatedUserServantList = createAssociatedUserServantList(userServants, servantList);
        userServantList.addAll(associatedUserServantList.stream().filter(userServant -> userServant.getSvtId() != 0).collect(Collectors.toList()));
        rosterServantList.addAll(associatedUserServantList);
    }

    private List<UserServant> createAssociatedUserServantList(List<UserServant> userServants, List<Servant> servantList) {
        userServants.forEach(svt -> {
            if (svt.getSvtId() != 0) {
                svt.setBaseServant(findServantById(svt.getSvtId(), servantList));
            }
        });
        return userServants;
    }

    private Servant findServantById(long svtId, List<Servant> servantList) {
        return servantList.stream().filter(svt -> svtId == svt.getId()).findFirst().get();
    }

    public List<UserServant> getClearedUserServantList() {
        return clearUnnecessaryEmptyUserRows(rosterServantList);
    }
}
