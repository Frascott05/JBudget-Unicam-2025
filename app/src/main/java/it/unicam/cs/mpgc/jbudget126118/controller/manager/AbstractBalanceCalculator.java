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

import java.util.List;
import java.util.Map;

/**
 * Abstract class for balance calculators.
 * This class provides a template for calculating financial balances based on a list of items.
 *
 * @param <T> the type of items managed by this calculator (e.g., Transaction).
 */
public abstract class AbstractBalanceCalculator<T> {

    protected final List<T> items;//protected because it is used in subclasses

    /**
     * Constructor for InterfaceBalanceCalculator.
     * Initializes the calculator with a list of items.
     *
     * @param items the list of items to be managed.
     */
    public AbstractBalanceCalculator(List<T> items) {
        this.items = items;
    }
    
    /**
     * Calculates the total income from a list of transactions.
     *
     * @return the total income as a double value.
     */
    public abstract double getTotalIncome();

    /**
     * Calculates the total expenses from a list.
     *
     * @return the total expenses as a double value.
     */
    public abstract double getTotalExpense();

    /**
     * Calculates the balance by subtracting total expenses from total income.
     *
     * @return the balance as a double value.
     */
    public double getBalance(){
        return getTotalIncome() - getTotalExpense();
    };

    /**
     * Gets a map of tags and their corresponding amounts.
     * @return a map where keys are tags and values are their corresponding amounts.
     */
    public abstract Map<Tag, Double> getTagsAmountMap();

    
}
