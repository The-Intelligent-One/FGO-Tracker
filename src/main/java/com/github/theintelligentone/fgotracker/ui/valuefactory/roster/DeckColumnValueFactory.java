package com.github.theintelligentone.fgotracker.ui.valuefactory.roster;

import com.github.theintelligentone.fgotracker.domain.view.UserServantView;
import javafx.beans.NamedArg;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class DeckColumnValueFactory implements Callback<TableColumn.CellDataFeatures<UserServantView, String>, ObservableValue<String>> {
    private final int cardNo;

    public DeckColumnValueFactory(@NamedArg("cardNo") int cardNo) {
        this.cardNo = cardNo;
    }

    @Override
    public ObservableValue<String> call(TableColumn.CellDataFeatures<UserServantView, String> param) {
        SimpleStringProperty card = new SimpleStringProperty();
        if (param.getValue().getBaseServant() != null && param.getValue().getBaseServant().getValue() != null) {
            String cardValue = param.getValue().getBaseServant().getValue().getCards().get(cardNo - 1).substring(0,
                    1).toUpperCase();
            card.set(cardValue);
        }
        return card;
    }
}
