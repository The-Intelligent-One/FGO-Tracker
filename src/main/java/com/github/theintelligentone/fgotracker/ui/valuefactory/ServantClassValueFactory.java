package com.github.theintelligentone.fgotracker.ui.valuefactory;

import com.github.theintelligentone.fgotracker.domain.servant.ServantOfUser;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class ServantClassValueFactory implements Callback<TableColumn.CellDataFeatures<ServantOfUser, String>, ObservableValue<String>> {
    @Override
    public ObservableValue<String> call(TableColumn.CellDataFeatures<ServantOfUser, String> param) {
        SimpleStringProperty name = new SimpleStringProperty();
        if (param.getValue() != null) {
            name.set(capitalizeFirstLetter(param.getValue().getBaseServant().getClassName()));
        }
        return name;
    }

    private String capitalizeFirstLetter(String className) {
        return className.substring(0, 1).toUpperCase() + className.substring(1);
    }
}
