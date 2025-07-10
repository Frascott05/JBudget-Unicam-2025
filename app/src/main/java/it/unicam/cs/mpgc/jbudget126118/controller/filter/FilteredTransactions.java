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

package it.unicam.cs.mpgc.jbudget126118.controller.filter;


import it.unicam.cs.mpgc.jbudget126118.model.Period;
import it.unicam.cs.mpgc.jbudget126118.model.Transaction;
import it.unicam.cs.mpgc.jbudget126118.model.TransactionType;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * FilteredTransactions is a class that implements the Filtered interface for Transaction objects.
 * It provides a way to filter transactions based on their type, date, and an optional period.
 */
public class FilteredTransactions implements Filtered<Transaction> {

    /**
     * Mode is an enumeration that defines the filtering modes for transactions.
     * - ALL: includes all transactions regardless of date.
     * - PAST: includes only transactions that occurred before today.
     * - FUTURE: includes only transactions that will occur after today.
     */
    public enum Mode {
        ALL,
        PAST,
        FUTURE
    }

    private final List<Transaction> allTransactions;
    private final Mode mode;
    private final Period period; // can be null
    private final TransactionType type;

    /**
     * Constructor for FilteredTransactions.
     * Initializes the FilteredTransactions object with a list of transactions, a mode, an optional period, and a transaction type.
     *
     * @param transactions the list of transactions to filter
     * @param mode the filtering mode (ALL, PAST, FUTURE)
     * @param period the period to filter transactions (can be null)
     * @param type the type of transactions to filter (e.g., INCOME, EXPENSE)
     */
    public FilteredTransactions(List<Transaction> transactions, Mode mode, Period period, TransactionType type) {
        this.allTransactions = transactions;
        this.mode = mode;
        this.period = period;
        this.type = type;
    }

    /**
     * Returns a list of transactions filtered by the specified type, date mode, and optional period.
     * 
     * @return a list of filtered transactions
     */
    @Override
    public List<Transaction> filteredItems() {
        LocalDate today = LocalDate.now();
        return allTransactions.stream()
            .filter(t -> t.transactionType() == type)
            .filter(t -> switch (mode) {
                case ALL -> true;
                case PAST -> t.transactionDate().isBefore(today);
                case FUTURE -> t.transactionDate().isAfter(today);
            })
            .filter(t -> period == null || period.contains(t.transactionDate()))
            .collect(Collectors.toList());
        
    }
}
