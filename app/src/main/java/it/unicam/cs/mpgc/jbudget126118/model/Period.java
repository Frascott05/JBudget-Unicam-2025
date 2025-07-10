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

import java.time.LocalDate;

/**
 * Represents a period defined by a start and end date.
 * This record encapsulates the details of a period, providing methods to check if a date falls within the period.
 * @param startDate The start date of the period. Can be null, in which case the current date is used.
 * @param endDate The end date of the period. Can be null, in which case the current date is used.
 */
public record Period(LocalDate startDate, LocalDate endDate) {

    /**
     * Returns the start date of the period or the current date if the start date is null.
     * @return The start date or current date.
     */
    public LocalDate getDataStartOrNow() {
        return startDate != null ? startDate : LocalDate.now();
    }

    /**
     * Returns the end date of the period or the current date if the end date is null.
     * @return The end date or current date.
     */
    public LocalDate getDataEndOrNow() {
        return endDate != null ? endDate : LocalDate.now();
    }

    /**
     * Checks if a given date falls within the period defined by the start and end dates.
     * The method considers the start date as inclusive and the end date as inclusive.
     * If either date is null, it defaults to the current date.
     * @param data The date to check.
     * @return true if the date is within the period, false otherwise.
     */
    public boolean contains(LocalDate data) {
        return (data.isEqual(getDataStartOrNow()) || data.isAfter(getDataStartOrNow()))
                && (data.isEqual(getDataEndOrNow()) || data.isBefore(getDataEndOrNow()));
    }
}

