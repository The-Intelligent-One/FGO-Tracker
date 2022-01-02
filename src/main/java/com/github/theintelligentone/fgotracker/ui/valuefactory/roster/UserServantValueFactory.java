package com.github.theintelligentone.fgotracker.ui.valuefactory.roster;

import com.github.theintelligentone.fgotracker.domain.servant.UserServant;
import javafx.beans.NamedArg;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

public class UserServantValueFactory<T> extends PropertyValueFactory<UserServant, T> {

    /**
     * Creates a default PropertyValueFactory to extract the value from a given
     * TableView row item reflectively, using the given property name.
     *
     * @param property The name of the property with which to attempt to
     *                 reflectively extract a corresponding value for in a given object.
     */
    public UserServantValueFactory(@NamedArg("property") String property) {
        super(property);
    }

    @Override
    public ObservableValue<T> call(TableColumn.CellDataFeatures<UserServant, T> param) {
        ObservableValue<T> call;
        if (param.getValue().getSvtId() == 0) {
            call = super.call(new TableColumn.CellDataFeatures<>(param.getTableView(), param.getTableColumn(), null));
        } else {
            call = super.call(param);
        }
        return call;
    }
}
