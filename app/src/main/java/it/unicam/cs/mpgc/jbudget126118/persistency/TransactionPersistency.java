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

package it.unicam.cs.mpgc.jbudget126118.persistency;

import it.unicam.cs.mpgc.jbudget126118.model.Tag;
import it.unicam.cs.mpgc.jbudget126118.model.Transaction;

import java.util.List;


/**
 * Interface for persistency layer to handle transactions and tags.
 * This interface defines methods for loading and saving transactions and tags.
 * It allows for different implementations, such as file-based or database-based storage.
 * 
 */
public interface TransactionPersistency extends FinancialEntitiesPersistency<Transaction> {

    /**
     * Loads all transactions from the storage.
     * 
     * @return a list of transactions.
     */
    @Override
    List<Transaction> load();

    /**
     * Load tags from the storage.
     * 
     * @return List of tags.
     */
    List<Tag> loadTags();

    /**
     * Saves a single transaction to the storage.
     * 
     * @param t the transaction to save.
     */
    @Override
    void save(Transaction t);
}

