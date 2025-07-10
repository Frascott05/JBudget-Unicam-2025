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

package it.unicam.cs.mpgc.jbudget126118.model;

import java.util.List;
import java.time.LocalDate;

/**
 * Represents a financial transaction.
 * This record encapsulates the details of a transaction including its ID, amount, type, date, and associated tags.
 * @param id Unique identifier for the transaction. Suggest to use timestamp or UUID for uniqueness.
 * @param amount The monetary value of the transaction. Should be positive because we use transactionType to differentiate between income and expense.
 * @param transactionType The type of the transaction, either INCOME or EXPENSE.
 * @param transactionDate The date when the transaction occurred or when it is programmed.
 * @param tags A list of tags associated with the transaction for categorization.
 */
public record Transaction(long id, double amount, TransactionType transactionType, LocalDate transactionDate, List<Tag> tags) {
}
