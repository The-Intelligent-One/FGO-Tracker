package com.github.theintelligentone.fgotracker.ui.cellfactory;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoCompleteTextFieldTableCell<S, T> extends TextFieldTableCell<S, T> {

    private final ObjectProperty<T> lastSelectedItem = new SimpleObjectProperty<>();
    private final ObservableList<T> entries;
    private ObservableList<T> filteredEntries = FXCollections.observableArrayList();
    private ContextMenu entriesPopup;
    private boolean caseSensitive = false;
    private boolean popupHidden = false;
    private String textOccurenceStyle = "-fx-font-weight: bold";
    private int maxEntries = 10;
    private TextField text;

    public AutoCompleteTextFieldTableCell(StringConverter<T> converter, ObservableList<T> entrySet) {
        super();
        getStyleClass().add("text-field-table-cell");
        setConverter(converter);
        this.entries = (entrySet == null ? FXCollections.observableArrayList() : entrySet);
        this.filteredEntries.addAll(entries);

        entriesPopup = new ContextMenu();
        getChildren().addListener((ListChangeListener.Change<? extends Node> change) ->
        {
            if (change.next()) {
                if (change.getAddedSubList().get(0) instanceof TextField) {
                    text = (TextField) change.getAddedSubList().get(0);

                    text.textProperty().addListener((ObservableValue<? extends String> observableValue, String s, String s2) ->
                    {
                        if (text.getText() == null || text.getText().length() == 0) {
                            filteredEntries.clear();
                            filteredEntries.addAll(entries);
                            entriesPopup.hide();
                        } else {
                            List<T> searchResult = new ArrayList<>();
                            //Check if the entered Text is part of some entry
                            String text1 = createNormalizedString(text.getText());
                            Pattern pattern;
                            if (isCaseSensitive()) {
                                pattern = Pattern.compile(".*" + text1 + ".*");
                            } else {
                                pattern = Pattern.compile(".*" + text1 + ".*", Pattern.CASE_INSENSITIVE);
                            }
                            for (T entry : entries) {
                                String normalizedString = createNormalizedString(entry.toString());
                                Matcher matcher = pattern.matcher(normalizedString);
                                if (matcher.matches()) {
                                    searchResult.add(entry);
                                }
                            }
                            if (!entries.isEmpty()) {
                                filteredEntries.clear();
                                filteredEntries.addAll(searchResult);
                                //Only show popup if not in filter mode
                                if (!isPopupHidden()) {
                                    populatePopup(searchResult, text1);
                                    if (!entriesPopup.isShowing()) {
                                        entriesPopup.show(AutoCompleteTextFieldTableCell.this, Side.BOTTOM, 0, 0);
                                    }
                                }
                            } else {
                                entriesPopup.hide();
                            }
                        }
                    });
                }
            }
        });

        focusedProperty().addListener((ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean aBoolean2) ->
        {
            entriesPopup.hide();
        });

    }

    public static <S> Callback<TableColumn<S, String>, TableCell<S, String>> forTableColumn(ObservableList<String> entries) {
        return list -> {
            AutoCompleteTextFieldTableCell<S, String> tableCell = new AutoCompleteTextFieldTableCell(new DefaultStringConverter(), entries);
            tableCell.getEntryMenu().setOnAction(e -> {
                ((MenuItem) e.getTarget()).addEventHandler(Event.ANY, event ->
                {
                    if (tableCell.getLastSelectedObject() != null) {
                        tableCell.commitEdit(tableCell.getLastSelectedObject());
                    }
                });
            });
            return tableCell;
        };
    }

    private String createNormalizedString(String toNormalize) {
        return Normalizer.normalize(toNormalize.toLowerCase(), Normalizer.Form.NFKD).replaceAll("[^\\p{ASCII}]", "");
    }

    public ObservableList<T> getEntries() {
        return entries;
    }

    private void populatePopup(List<T> searchResult, String text) {
        entriesPopup.getItems().clear();
        int count = Math.min(searchResult.size(), getMaxEntries());
        for (int i = 0; i < count; i++) {
            final String result = searchResult.get(i).toString();
            final T itemObject = searchResult.get(i);
            int occurence;

            if (isCaseSensitive()) {
                occurence = result.indexOf(text);
            } else {
                occurence = createNormalizedString(result).indexOf(createNormalizedString(text));
            }
            if (occurence < 0) {
                continue;
            }
            //Part before occurence (might be empty)
            Text pre = new Text(result.substring(0, occurence));
            //Part of (first) occurence
            Text in = new Text(result.substring(occurence, occurence + text.length()));
            in.setStyle(getTextOccurenceStyle());
            //Part after occurence
            Text post = new Text(result.substring(occurence + text.length()));

            TextFlow entryFlow = new TextFlow(pre, in, post);

            CustomMenuItem item = new CustomMenuItem(entryFlow, true);
            item.setOnAction((ActionEvent actionEvent) ->
            {
                lastSelectedItem.set(itemObject);
                entriesPopup.hide();
            });
            entriesPopup.getItems().add(item);
        }

    }

    public T getLastSelectedObject() {
        return lastSelectedItem.get();
    }

    public ContextMenu getEntryMenu() {
        return entriesPopup;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public String getTextOccurenceStyle() {
        return textOccurenceStyle;
    }

    public void setTextOccurenceStyle(String textOccurenceStyle) {
        this.textOccurenceStyle = textOccurenceStyle;
    }

    public boolean isPopupHidden() {
        return popupHidden;
    }

    public void setPopupHidden(boolean popupHidden) {
        this.popupHidden = popupHidden;
    }

    public ObservableList<T> getFilteredEntries() {
        return filteredEntries;
    }

    public int getMaxEntries() {
        return maxEntries;
    }

    public void setMaxEntries(int maxEntries) {
        this.maxEntries = maxEntries;
    }

}