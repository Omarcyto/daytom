package com.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ResultSetIterator implements Iterator<ResultSet> {

    /**
     * Result set to iterate.
     */
    private ResultSet rs;

    /**
     * Next result set. <code>null</code> indicates no more elements.
     */
    private ResultSet next = null;

    /**
     * ResultSetIterator's constructor.
     *
     * @param rs ResultSet to iterate.
     */
    public ResultSetIterator(ResultSet rs) {
        this.rs = rs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNext() {
        try {
            updateNext();
            return next != null;
        } catch (SQLException ex) {
            throw new RuntimeException("At hasNext.", ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResultSet next() {
        try {
            updateNext();
            if (next == null) {
                throw new NoSuchElementException();
            }
            return next;
        } catch (SQLException ex) {
            throw new RuntimeException("At nex element.", ex);
        } finally {
            next = null;
        }
    }

    /**
     * Update the <code>next</code> with the next value. <code>null</code> indicates no more elements.
     *
     * @throws SQLException
     */
    private void updateNext() throws SQLException {
        if (next == null) {
            next = rs.next() ? rs : null;
        }
    }
}
