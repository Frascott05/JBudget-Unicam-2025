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

package it.unicam.cs.mpgc.jbudget126118.controller.manager;


import it.unicam.cs.mpgc.jbudget126118.model.Tag;
import it.unicam.cs.mpgc.jbudget126118.model.Transaction;
import it.unicam.cs.mpgc.jbudget126118.model.TransactionType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * TransactionBalance is a class that extends AbstractBalanceCalculator to manage and calculate balances
 * based on a list of transactions.
 * It provides methods to calculate total income, total expenses, and expenses grouped by tags.
 */
public class TransactionBalance extends AbstractBalanceCalculator<Transaction> {
    
    /**
     * Constructor for ManageBalance.
     * Initializes the ManageBalance object with a list of transactions.
     * @param transactions the list of transactions to be managed.
     */
    public TransactionBalance(List<Transaction> transactions) {
        super(transactions);
    }

    /**
     *  Gets the total income from the transactions of a specific type
     *  and sums the amounts of those transactions.
     *  
     * @param type  the type of transaction to filter by (e.g., INCOME, EXPENSE).
     * @return  the total amount of transactions of the specified type.
     */
    private double getTotals(TransactionType type) {
        return items.stream()
                .filter(t -> t.transactionType() == type)
                .mapToDouble(Transaction::amount)
                .sum();
    }

    /**
     *  Gets the total income from the transactions.
     *  This method filters the transactions to include only those of type INCOME
     * @return   the total income as a double value, which is the sum of amounts of all income transactions.
     */
    @Override
    public double getTotalIncome() {
        return getTotals(TransactionType.INCOME);
    }

    /**
     *  Gets the total expenses from the transactions.
     *  This method filters the transactions to include only those of type EXPENSE.
     * @return   the total expenses as a double value, which is the sum of amounts of all expense transactions.
     */
    @Override
    public double getTotalExpense() {
        return getTotals(TransactionType.EXPENSE);
    }

    /**
     *  Gets the total expenses grouped by tags.
     *  This method filters the transactions to include only those of type EXPENSE,
     *  then groups them by their associated tags,
     *  summing the amounts for each tag.
     * @return  a map where the keys are tags and the values are the total expenses associated with each tag.
     */
    @Override
    public Map<Tag, Double> getTagsAmountMap() {
        return items.stream()
                .filter(t -> t.transactionType() == TransactionType.EXPENSE)
                .flatMap(t -> t.tags().stream().map(tag -> Map.entry(tag, t.amount())))
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.summingDouble(Map.Entry::getValue)
                ));
    }


}
