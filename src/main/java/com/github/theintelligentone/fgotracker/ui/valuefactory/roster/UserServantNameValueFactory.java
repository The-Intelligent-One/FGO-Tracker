package com.github.theintelligentone.fgotracker.ui.valuefactory.roster;

import com.github.theintelligentone.fgotracker.domain.servant.UserServant;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class UserServantNameValueFactory implements Callback<TableColumn.CellDataFeatures<UserServant, String>, ObservableValue<String>> {
    @Override
    public ObservableValue<String> call(TableColumn.CellDataFeatures<UserServant, String> param) {
        SimpleStringProperty name = new SimpleStringProperty();
        if (param.getValue().getSvtId() != 0) {
            name.set(param.getValue().getBaseServant().getName());
        }
        return name;
    }
}
