package com.github.theintelligentone.fgotracker.service.datamanagement.user;

import com.github.theintelligentone.fgotracker.domain.servant.Servant;
import com.github.theintelligentone.fgotracker.domain.servant.UserServant;
import com.github.theintelligentone.fgotracker.domain.servant.factory.UserServantFactory;
import com.github.theintelligentone.fgotracker.service.transformer.UserServantToViewTransformer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static com.github.theintelligentone.fgotracker.service.datamanagement.DataManagementServiceFacade.MIN_TABLE_SIZE;
import static com.github.theintelligentone.fgotracker.service.datamanagement.DataManagementServiceFacade.NAME_FORMAT;

@Component
public class UserServantManagementService {
    @Autowired
    private UserServantToViewTransformer userServantToViewTransformer;
    private ObservableList<UserServant> userServantList;
//    @Getter
//    private ObservableList<String> userServantNameList;


    public void initDataLists() {
        userServantList = FXCollections.observableArrayList();
//        addListenersForUpdatingNameList();
//        userServantNameList = FXCollections.observableArrayList();
    }

//    private void addListenersForUpdatingNameList() {
//        userServantList.addListener((ListChangeListener.Change<? extends UserServantView> c) -> {
//            userServantNameList.clear();
//            userServantNameList.addAll(c.getList().stream()
//                    .filter(svt -> svt.baseServantProperty().getValue() != null)
//                    .map(svt -> String.format(NAME_FORMAT, svt.baseServantProperty().getValue().getName(),
//                            svt.baseServantProperty().getValue().getRarity(),
//                            svt.baseServantProperty().getValue().getClassName()))
//                    .collect(Collectors.toList()));
//        });
//    }

    private List<UserServant> clearUnnecessaryEmptyUserRows(List<UserServant> servantList) {
        List<UserServant> newList = new ArrayList<>(servantList);
        int index = newList.size() - 1;
        while (!newList.isEmpty() && newList.get(index).getSvtId() == 0) {
            newList.remove(index--);
        }
        return newList;
    }

    public void saveUserServant(UserServant servant) {
        userServantList.add(servant);
    }

    public void saveUserServant(int index, UserServant servant) {
        userServantList.add(index, servant);
    }

    public void replaceBaseServantInRow(int index, UserServant servant, Servant newBaseServant) {
        if (newBaseServant != null) {
            if (servant.getSvtId() == 0) {
                userServantList.set(index,
                        new UserServantFactory().createUserServantFromBaseServant(newBaseServant));
            } else {
                servant.setSvtId(newBaseServant.getId());
                servant.setRarity(newBaseServant.getRarity());
                servant.setBaseServant(newBaseServant);
                servant.setSvtClass(newBaseServant.getClassName());
                userServantList.set(index, servant);
            }
        }
    }

    public void eraseUserServant(UserServant servant) {
        userServantList.set(userServantList.indexOf(servant), new UserServant());
    }

    public UserServant findUserServantByFormattedName(String name) {
        return userServantList.stream()
                .filter(svt -> svt.getSvtId() != 0)
                .filter(svt -> name.equalsIgnoreCase(String.format(NAME_FORMAT, svt.getBaseServant().getName(),
                        svt.getBaseServant().getRarity(),
                        svt.getBaseServant().getClassName())))
                .findFirst().orElse(null);
    }

    public ObservableList<UserServant> getPaddedUserServantList() {
        if (userServantList.size() < MIN_TABLE_SIZE) {
            IntStream.range(0, MIN_TABLE_SIZE - userServantList.size()).forEach(
                    i -> saveUserServant(new UserServant()));
        }
        return userServantList;
    }

    public void removeUserServant(UserServant servant) {
        userServantList.remove(servant);
    }

    public void saveImportedUserServants(List<UserServant> importedServants) {
        userServantList.setAll(clearUnnecessaryEmptyUserRows(importedServants));
    }

    public void refreshUserServants(List<UserServant> userServants, List<Servant> servantList) {
        userServantList.addAll(createAssociatedUserServantList(userServants, servantList));
    }

    private List<UserServant> createAssociatedUserServantList(
            List<UserServant> userServants,
            List<Servant> servantList) {
        userServants.forEach(svt -> {
            if (svt.getSvtId() != 0L) {
                svt.setBaseServant(findServantById(svt.getSvtId(), servantList));
            }
        });
        return userServants;
    }

    private Servant findServantById(long svtId, List<Servant> servantList) {
        return servantList.stream().filter(svt -> svtId == svt.getId()).findFirst().get();
    }

    public List<UserServant> getClearedUserServantList() {
        return clearUnnecessaryEmptyUserRows(userServantList);
    }
}
