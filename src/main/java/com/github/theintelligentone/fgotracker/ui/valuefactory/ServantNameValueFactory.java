package com.github.theintelligentone.fgotracker.ui.valuefactory;

import com.github.theintelligentone.fgotracker.domain.servant.UserServant;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class ServantNameValueFactory implements Callback<TableColumn.CellDataFeatures<UserServant, String>, ObservableValue<String>> {
    @Override
    public ObservableValue<String> call(TableColumn.CellDataFeatures<UserServant, String> param) {
        SimpleStringProperty name = new SimpleStringProperty();
        if (param.getValue().getBaseServant() != null) {
            name.set(param.getValue().getBaseServant().getName());
        }
        return name;
    }
}
