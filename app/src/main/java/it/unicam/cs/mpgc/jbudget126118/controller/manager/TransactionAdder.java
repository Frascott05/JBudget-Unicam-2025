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


import it.unicam.cs.mpgc.jbudget126118.persistency.TransactionPersistency;
import it.unicam.cs.mpgc.jbudget126118.model.Recurrence;
import it.unicam.cs.mpgc.jbudget126118.model.Transaction;

import java.time.LocalDate;


/**
 * TransactionAdder is a class responsible for managing the addition of transactions
 * to a financial management system. It implements the IFinancialEntities interface,
 * which defines methods for adding financial entities.
 * This class provides functionality to add a single transaction or to add a recurrence of a transaction
 * until a specified end date.
 * It uses a persistency layer to save transactions, allowing for easy storage and retrieval.
 */
public class TransactionAdder implements IFinancialEntitiesAdder<Transaction> {
    private final TransactionPersistency persistency;

    /**
     * Constructor for ManageTransaction.
     * Initializes the ManageTransaction object with a specified persistency layer.
     * @param persistency the persistency layer to be used for saving transactions.
     */
    public TransactionAdder(TransactionPersistency persistency) {
        this.persistency = persistency;
    }


    /**
     * Adds a transaction to the persistency layer.
     * This method saves the provided transaction to the storage.
     * @param transaction the transaction to be added.
     */
    @Override
    public void add(Transaction transaction) {
        persistency.save(transaction);
    }


    /**
     * Adds a recurrence of a transaction until a specified end date.
     * This method creates new transactions based on the provided transaction and recurrence period,
     * and saves them to the persistency layer until the current date exceeds the end date.
     * @param t the transaction to be repeated.
     * @param EndDate the date until which the recurrence should continue.
     * @param recurrence the recurrence details, including the period of recurrence.
     */
    public void addRecurrence(Transaction t, LocalDate EndDate, Recurrence recurrence) {
        LocalDate currentDate = t.transactionDate();

        while (currentDate.isBefore(EndDate) || currentDate.isEqual(EndDate)) {
            Transaction newTransaction = new Transaction(
                System.currentTimeMillis(),
                Math.abs(t.amount()),
                t.transactionType(),
                currentDate,
                t.tags()
            );
            add(newTransaction);
            currentDate = currentDate.plusDays(recurrence.period());
        }

    }
}
