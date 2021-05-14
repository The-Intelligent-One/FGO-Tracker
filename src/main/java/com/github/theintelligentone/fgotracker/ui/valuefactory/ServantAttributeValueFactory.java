package com.github.theintelligentone.fgotracker.ui.valuefactory;

import com.github.theintelligentone.fgotracker.domain.servant.UserServant;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class ServantAttributeValueFactory implements Callback<TableColumn.CellDataFeatures<UserServant, String>, ObservableValue<String>> {
    @Override
    public ObservableValue<String> call(TableColumn.CellDataFeatures<UserServant, String> param) {
        SimpleStringProperty name = new SimpleStringProperty();
        if (param.getValue() != null) {
            name.set(capitalizeFirstLetter(param.getValue().getBaseServant().getAttribute()));
        }
        return name;
    }

    private String capitalizeFirstLetter(String attribute) {
        return attribute.substring(0, 1).toUpperCase() + attribute.substring(1);
    }
}
