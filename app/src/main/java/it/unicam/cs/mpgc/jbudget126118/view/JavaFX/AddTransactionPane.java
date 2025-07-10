/*
 * MIT License
 *
 * Copyright (c) 2025 Francesco Scotti
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package it.unicam.cs.mpgc.jbudget126118.view.JavaFX;

import it.unicam.cs.mpgc.jbudget126118.controller.manager.TransactionAdder;
import it.unicam.cs.mpgc.jbudget126118.model.Recurrence;
import it.unicam.cs.mpgc.jbudget126118.model.Tag;
import it.unicam.cs.mpgc.jbudget126118.model.Transaction;
import it.unicam.cs.mpgc.jbudget126118.model.TransactionType;
import it.unicam.cs.mpgc.jbudget126118.persistency.TransactionPersistency;
import it.unicam.cs.mpgc.jbudget126118.persistency.XmlPersistency;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AddTransactionPane is a JavaFX pane that provides an interface for adding new transactions.
 * It allows users to input transaction details such as amount, date, type, recurrence,
 * and associated tags. The pane also supports adding and removing tags from a selected list.
 */
public class AddTransactionPane extends VBox {

    private final TransactionPersistency persistency;
    /**
     * Constructor for AddTransactionPane.
     * Initializes the pane with controls for entering transaction details,
     * selecting tags, and adding the transaction to the system.
     * @param p the persistency for loading transactions and tags
     */
    public AddTransactionPane(TransactionPersistency p) {
        this.persistency = p;
        setPadding(new Insets(15));
        setSpacing(10);

        TextField amountField = new TextField();
        DatePicker datePicker = new DatePicker(LocalDate.now());
        ComboBox<TransactionType> typeComboBox = new ComboBox<>(FXCollections.observableArrayList(TransactionType.values()));
        ComboBox<Recurrence> recurrenceComboBox = new ComboBox<>(FXCollections.observableArrayList(Recurrence.values()));
        DatePicker endRecurrencePicker = new DatePicker();

        Label tagsLabel = new Label("Categorie (max 3):");

        ListView<String> selectedTagsListView = new ListView<>();
        selectedTagsListView.setPrefHeight(60);

        ComboBox<String> tagComboBox = new ComboBox<>();
        tagComboBox.setPromptText("Seleziona un tag");
        tagComboBox.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                }
            }
        });

        Button addTagButton = new Button("Aggiungi Tag");
        addTagButton.setDisable(true);

        Button removeTagButton = new Button("Rimuovi Tag");
        removeTagButton.setDisable(true);

        List<Tag> allTags = persistency.loadTags();

        Map<Tag, List<Tag>> tagHierarchy = new HashMap<>();
        for (Tag tag : allTags) {
            if (tag.parent() != null) {
                tagHierarchy.computeIfAbsent(tag.parent(), k -> new ArrayList<>()).add(tag);
            }
        }

        List<String> formattedTags = new ArrayList<>();
        for (Tag tag : allTags) {
            if (tag.parent() == null) {
                formattedTags.add(tag.name());
                addChildrenTags(tag, tagHierarchy, formattedTags, 1);
            }
        }
        tagComboBox.setItems(FXCollections.observableArrayList(formattedTags));

        tagComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            addTagButton.setDisable(newVal == null || selectedTagsListView.getItems().size() >= 3);
        });

        selectedTagsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            removeTagButton.setDisable(newVal == null);
        });

        addTagButton.setOnAction(e -> {
            String selectedTag = tagComboBox.getValue();
            if (selectedTag != null && selectedTagsListView.getItems().size() < 3
                    && !selectedTagsListView.getItems().contains(selectedTag.trim())) {
                selectedTagsListView.getItems().add(selectedTag.trim());
                addTagButton.setDisable(selectedTagsListView.getItems().size() >= 3);
            }
        });

        removeTagButton.setOnAction(e -> {
            String selected = selectedTagsListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selectedTagsListView.getItems().remove(selected);
                addTagButton.setDisable(false);
                removeTagButton.setDisable(true);
            }
        });

        Button addButton = new Button("Aggiungi Transazione");
        TransactionAdder adder = new TransactionAdder(persistency);

        addButton.setOnAction(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                LocalDate date = datePicker.getValue();
                TransactionType type = typeComboBox.getValue();
                Recurrence recurrence = recurrenceComboBox.getValue();

                List<Tag> selectedTags = new ArrayList<>();
                for (String formattedTagName : selectedTagsListView.getItems()) {
                    String tagName = formattedTagName.trim();
                    allTags.stream()
                            .filter(t -> t.name().equals(tagName))
                            .findFirst()
                            .ifPresent(selectedTags::add);
                }

                Transaction t = new Transaction(System.currentTimeMillis(), amount, type, date, selectedTags);

                if (recurrence != Recurrence.NONE && endRecurrencePicker.getValue() != null) {
                    adder.addRecurrence(t, endRecurrencePicker.getValue(), recurrence);
                } else {
                    adder.add(t);
                }

                amountField.clear();
                selectedTagsListView.getItems().clear();
                addTagButton.setDisable(false);
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Importo non valido.");
                alert.showAndWait();
            }
        });

        HBox tagSelectionBox = new HBox(5, tagComboBox, addTagButton, removeTagButton);
        tagSelectionBox.setPadding(new Insets(5, 0, 5, 0));

        getChildren().addAll(
                new Label("Importo:"), amountField,
                new Label("Data:\n(inserire una data futura per una transazione programmata)"), datePicker,
                new Label("Tipo:"), typeComboBox,
                new Label("Ricorrenza:"), recurrenceComboBox,
                new Label("Fine Ricorrenza (se applicabile):"), endRecurrencePicker,
                tagsLabel, selectedTagsListView,
                tagSelectionBox,
                addButton
        );
    }

    /**
     * Recursively adds child tags to the formatted tags list.
     * This method traverses the tag hierarchy and formats the tags
     * with indentation based on their level in the hierarchy.
     *
     * @param parent         The parent tag whose children are to be added.
     * @param tagHierarchy   A map representing the hierarchy of tags.
     * @param formattedTags  The list to which formatted tags will be added.
     * @param level          The current level in the hierarchy for indentation.
     */
    private void addChildrenTags(Tag parent, Map<Tag, List<Tag>> tagHierarchy, List<String> formattedTags, int level) {
        if (tagHierarchy.containsKey(parent)) {
            for (Tag child : tagHierarchy.get(parent)) {
                formattedTags.add("  ".repeat(level) + child.name());
                addChildrenTags(child, tagHierarchy, formattedTags, level + 1);
            }
        }
    }
}