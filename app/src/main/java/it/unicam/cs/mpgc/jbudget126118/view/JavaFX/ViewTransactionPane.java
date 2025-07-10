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

import it.unicam.cs.mpgc.jbudget126118.controller.filter.FilteredTransactions;
import it.unicam.cs.mpgc.jbudget126118.model.Period;
import it.unicam.cs.mpgc.jbudget126118.model.Tag;
import it.unicam.cs.mpgc.jbudget126118.model.Transaction;
import it.unicam.cs.mpgc.jbudget126118.model.TransactionType;
import it.unicam.cs.mpgc.jbudget126118.persistency.TransactionPersistency;
import it.unicam.cs.mpgc.jbudget126118.persistency.XmlPersistency;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ViewTransactionPane is a JavaFX pane that provides an interface for viewing and filtering transactions.
 * It allows users to filter transactions by type and date, and displays the results in a table.
 */
public class ViewTransactionPane extends VBox {
    private final ComboBox<String> modeFilterBox = new ComboBox<>();
    private final ComboBox<TransactionType> typeFilterBox = new ComboBox<>();
    private final DatePicker startDatePicker = new DatePicker();
    private final DatePicker endDatePicker = new DatePicker();
    private final TableView<TransactionTableModel> transactionTable = new TableView<>();

    private final TransactionPersistency persistency;

    /**
     * Constructor for ViewTransactionPane.
     * Initializes the pane with controls for filtering transactions,
     * setting up the transaction table, and updating the displayed transactions.
     * @param persistency the persistency for the transactions and tags
     */
    public ViewTransactionPane(TransactionPersistency persistency) {
        this.persistency = persistency;
        setSpacing(10);
        setPadding(new Insets(10));

        modeFilterBox.getItems().addAll("Tutte", "Passate", "Future");
        modeFilterBox.setValue("Tutte");

        typeFilterBox.getItems().addAll(TransactionType.values());
        typeFilterBox.setValue(TransactionType.EXPENSE);

        Button filterButton = new Button("Applica Filtro");
        filterButton.setOnAction(e -> updateTransactionList());

        setupTransactionTable();

        getChildren().addAll(
                new Label("Tipo transazione:"), typeFilterBox,
                new Label("Filtro temporale:"), modeFilterBox,
                new Label("Data inizio (opzionale):"), startDatePicker,
                new Label("Data fine (opzionale):"), endDatePicker,
                filterButton,
                new Label("Transazioni:"),
                transactionTable
        );

        updateTransactionList();
    }

    /**
     * Sets up the transaction table with columns for date, type, amount, and tags.
     * Each column is bound to a property of the TransactionTableModel.
     */
    private void setupTransactionTable() {
        TableColumn<TransactionTableModel, String> dateCol = new TableColumn<>("Data");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<TransactionTableModel, String> typeCol = new TableColumn<>("Tipo");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<TransactionTableModel, String> amountCol = new TableColumn<>("Importo");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<TransactionTableModel, String> tagsCol = new TableColumn<>("Tags");
        tagsCol.setCellValueFactory(new PropertyValueFactory<>("tags"));

        transactionTable.getColumns().addAll(dateCol, typeCol, amountCol, tagsCol);

        transactionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    }

    /**
     * Updates the transaction list based on the selected filters.
     * It retrieves all transactions, applies the filters for date and type,
     * and updates the table with the filtered results.
     */
    private void updateTransactionList() {
        List<Transaction> allTransactions = persistency.load();

        Period period = null;
        if (startDatePicker.getValue() != null || endDatePicker.getValue() != null) {
            period = new Period(startDatePicker.getValue(), endDatePicker.getValue());
        }

        FilteredTransactions.Mode mode = switch (modeFilterBox.getValue()) {
            case "Passate" -> FilteredTransactions.Mode.PAST;
            case "Future" -> FilteredTransactions.Mode.FUTURE;
            default -> FilteredTransactions.Mode.ALL;
        };

        TransactionType type = typeFilterBox.getValue();

        FilteredTransactions filtered = new FilteredTransactions(allTransactions, mode, period, type);

        List<TransactionTableModel> tableData = filtered.filteredItems().stream()
                .map(t -> new TransactionTableModel(
                        t.transactionDate().toString(),
                        t.transactionType().toString(),
                        String.format("%.2f", t.amount()),
                        t.tags().stream().map(Tag::name).collect(Collectors.joining(", "))
                ))
                .toList();

        transactionTable.setItems(FXCollections.observableArrayList(tableData));
    }

    /**
     * TransactionTableModel is a model class for representing a transaction in the table.
     * It contains properties for date, type, amount, and tags.
     */
    public static class TransactionTableModel {
        private final String date;
        private final String type;
        private final String amount;
        private final String tags;

        public TransactionTableModel(String date, String type, String amount, String tags) {
            this.date = date;
            this.type = type;
            this.amount = amount;
            this.tags = tags;
        }

        public String getDate() {
            return date;
        }

        public String getType() {
            return type;
        }

        public String getAmount() {
            return amount;
        }

        public String getTags() {
            return tags;
        }
    }
}