package com.github.theintelligentone.fgotracker.ui.valuefactory;

import com.github.theintelligentone.fgotracker.domain.servant.ServantOfUser;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class BasicServantNameFactory implements Callback<TableColumn.CellDataFeatures<ServantOfUser, String>, ObservableValue<String>> {
    @Override
    public ObservableValue<String> call(TableColumn.CellDataFeatures<ServantOfUser, String> cell) {
        return new SimpleStringProperty(cell.getValue().getServant().getName());
    }
}
