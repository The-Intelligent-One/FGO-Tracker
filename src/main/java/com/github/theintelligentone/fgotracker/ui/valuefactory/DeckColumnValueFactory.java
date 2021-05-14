package com.github.theintelligentone.fgotracker.ui.valuefactory;

import com.github.theintelligentone.fgotracker.domain.servant.UserServant;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class DeckColumnValueFactory implements Callback<TableColumn.CellDataFeatures<UserServant, String>, ObservableValue<String>> {

    @FXML
    private int cardNo;

    public DeckColumnValueFactory(int cardNo) {
        this.cardNo = cardNo;
    }

    public static DeckColumnValueFactory valueOf(String value) {
        return new DeckColumnValueFactory(Integer.parseInt(value));
    }

    @Override
    public ObservableValue<String> call(TableColumn.CellDataFeatures<UserServant, String> param) {
        SimpleStringProperty card = new SimpleStringProperty();
        if (param.getValue() != null && param.getValue().getBaseServant().getCards().size() > 0) {
            String cardValue = param.getValue().getBaseServant().getCards().get(cardNo - 1).substring(0, 1).toUpperCase();
            card.set(cardValue);
        }
        return card;
    }
}
