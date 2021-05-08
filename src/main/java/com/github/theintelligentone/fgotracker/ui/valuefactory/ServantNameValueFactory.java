package com.github.theintelligentone.fgotracker.ui.valuefactory;

import com.github.theintelligentone.fgotracker.domain.servant.ServantOfUser;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class ServantNameValueFactory implements Callback<TableColumn.CellDataFeatures<ServantOfUser, String>, ObservableValue<String>> {
    @Override
    public ObservableValue<String> call(TableColumn.CellDataFeatures<ServantOfUser, String> param) {
        SimpleStringProperty name = new SimpleStringProperty();
        if (param.getValue() != null) {
            name.set(param.getValue().getBaseServant().getName());
        }
        return name;
    }
}
