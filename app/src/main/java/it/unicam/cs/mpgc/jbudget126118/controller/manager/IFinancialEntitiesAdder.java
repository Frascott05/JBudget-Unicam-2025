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


/**
 * IFinancialEntities is an interface that defines methods for managing financial entities
 * in a financial management system. It allows for adding new entities such as transactions.
 *
 * @param <T> the type of financial entity to be managed, e.g., Transaction.
 */
public interface IFinancialEntitiesAdder<T> {
    
    
    /**
     *  Adds an entity to the financial management system.
     *  This method is used to add a new entity, such as a transaction,
     *  to the system for further processing or storage.
     * 
     * @param entity the entity to add
     */
    public void add(T entity);
}
