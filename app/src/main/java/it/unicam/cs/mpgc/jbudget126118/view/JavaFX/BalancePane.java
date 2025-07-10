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
import it.unicam.cs.mpgc.jbudget126118.controller.manager.TransactionBalance;
import it.unicam.cs.mpgc.jbudget126118.model.Period;
import it.unicam.cs.mpgc.jbudget126118.model.Tag;
import it.unicam.cs.mpgc.jbudget126118.model.Transaction;
import it.unicam.cs.mpgc.jbudget126118.model.TransactionType;
import it.unicam.cs.mpgc.jbudget126118.persistency.TransactionPersistency;
import javafx.geometry.Insets;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.Map;

/**
 * BalancePane is a JavaFX pane that provides an interface for analyzing
 * the financial balance of transactions. It allows users to filter transactions
 * by date and type, view total income and expenses, and visualize the data
 * using a pie chart and a table of tag amounts.
 */
public class BalancePane extends VBox {
    private final ComboBox<String> modeFilterBox = new ComboBox<>();
    private final DatePicker startDatePicker = new DatePicker();
    private final DatePicker endDatePicker = new DatePicker();
    private final Label incomeLabel = new Label();
    private final Label expenseLabel = new Label();
    private final PieChart pieChart = new PieChart();
    private final TableView<TagAmount> tagTable = new TableView<>();

    private final TransactionPersistency persistency;

    /**
     * Constructor for BalancePane.
     * Initializes the pane with controls for filtering transactions,
     * displaying total income and expenses, and visualizing data
     * using a pie chart and a table.
     * Sets up the layout and event handling for updating the balance analysis.
     * @param persistency the persistency for loading transactions and tags
     */
    public BalancePane(TransactionPersistency persistency) {
        this.persistency = persistency;
        setPadding(new Insets(10));
        setSpacing(10);

        modeFilterBox.getItems().addAll("Tutte", "Passate", "Future");
        modeFilterBox.setValue("Tutte");

        Button updateButton = new Button("Aggiorna Analisi");
        updateButton.setOnAction(e -> updateBalance());

        setupTagTable();

        HBox chartAndTable = new HBox(20, pieChart, tagTable);
        chartAndTable.setPadding(new Insets(10));

        getChildren().addAll(
                new Label("Filtro temporale:"), modeFilterBox,
                new Label("Data Inizio:"), startDatePicker,
                new Label("Data Fine:"), endDatePicker,
                updateButton,
                incomeLabel, expenseLabel,
                chartAndTable
        );

        updateBalance();
    }

    /**
     * Sets up the tag table with columns for displaying
     * tag names and their corresponding amounts.
     * This method initializes the table columns
     * and binds them to the properties of the TagAmount class.
     */
    private void setupTagTable() {
        TableColumn<TagAmount, String> tagCol = new TableColumn<>("Tag");
        tagCol.setCellValueFactory(new PropertyValueFactory<>("tagName"));

        TableColumn<TagAmount, String> amountCol = new TableColumn<>("Importo");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        tagTable.getColumns().addAll(tagCol, amountCol);
        tagTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
    }

    /**
     * Updates the balance analysis by loading all transactions,
     * filtering them based on the selected date range and mode,
     * calculating total income and expenses,
     * and updating the pie chart and tag table.
     */
    private void updateBalance() {
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

        // Income
        var filteredIncome = new FilteredTransactions(allTransactions, mode, period, TransactionType.INCOME);
        var incomeBalance = new TransactionBalance(filteredIncome.filteredItems());

        // Expense
        var filteredExpense = new FilteredTransactions(allTransactions, mode, period, TransactionType.EXPENSE);
        var expenseBalance = new TransactionBalance(filteredExpense.filteredItems());

        incomeLabel.setText(String.format("Totale Entrate: %.2f", incomeBalance.getTotalIncome()));
        expenseLabel.setText(String.format("Totale Uscite: %.2f", expenseBalance.getTotalExpense()));

        updatePieChart(expenseBalance);
        updateTagTable(expenseBalance);
    }

    /**
     * Updates the pie chart with the expense balance data.
     * It clears the existing data and adds new slices based on the tags and their amounts.
     * 
     * @param expenseBalance The TransactionBalance object containing the expense data.
     */
    private void updatePieChart(TransactionBalance expenseBalance) {
        pieChart.getData().clear();
        Map<Tag, Double> tagAmounts = expenseBalance.getTagsAmountMap();

        tagAmounts.forEach((tag, amount) -> {
            PieChart.Data slice = new PieChart.Data(tag.name(), amount);
            pieChart.getData().add(slice);

            slice.nameProperty().bind(
                    javafx.beans.binding.Bindings.concat(
                            tag.name(), " (",
                            String.format("%.2f", amount), "€)"
                    )
            );
        });
    }

    /**
     * Updates the tag table with the amounts associated with each tag.
     * It clears the existing items in the table and adds new TagAmount objects
     * based on the expense balance data.
     * @param expenseBalance The TransactionBalance object containing the expense data.
     */
    private void updateTagTable(TransactionBalance expenseBalance) {
        Map<Tag, Double> tagAmounts = expenseBalance.getTagsAmountMap();

        tagTable.getItems().clear();
        tagAmounts.forEach((tag, amount) -> {
            tagTable.getItems().add(new TagAmount(
                    tag.name(),
                    String.format("%.2f €", amount)
            ));
        });
    }

    /**
     * Represents a tag and its associated amount.
     * This class is used to display tag names and their corresponding amounts
     * in the tag table of the BalancePane.
     */
    public static class TagAmount {
        private final String tagName;
        private final String amount;

        /**
         * Constructor for the class TagAmount
         * @param tagName Name of the tag
         * @param amount Amount for that tag
         */
        public TagAmount(String tagName, String amount) {
            this.tagName = tagName;
            this.amount = amount;
        }

        public String getTagName() {
            return tagName;
        }

        public String getAmount() {
            return amount;
        }
    }
}