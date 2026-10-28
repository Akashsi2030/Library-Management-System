package com.library.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.library.model.BorrowRecord;
import com.library.util.DBConnection;

public class BorrowDAO {

    /**
     * Issues a book to a user.
     * Decrements available book stock and creates an issue record.
     * Returns null on success, or a human-readable error message.
     */
    public String issueBook(int bookId, int userId) throws SQLException {
        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                // 1. Check book availability with row lock
                String checkStockSql = "SELECT available FROM books WHERE id=? FOR UPDATE";
                try (PreparedStatement ps = c.prepareStatement(checkStockSql)) {
                    ps.setInt(1, bookId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next() || rs.getInt(1) <= 0) {
                            c.rollback();
                            return "Book is currently out of stock.";
                        }
                    }
                }

                // 2. Prevent duplicate active borrow by same user
                String checkDuplicateSql = "SELECT id FROM issues WHERE book_id=? AND user_id=? AND return_date IS NULL";
                try (PreparedStatement ps = c.prepareStatement(checkDuplicateSql)) {
                    ps.setInt(1, bookId);
                    ps.setInt(2, userId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            c.rollback();
                            return "You already have an active copy of this book issued.";
                        }
                    }
                }

                // 3. Insert issue record (with status 'ISSUED')
                String insertIssueSql = "INSERT INTO issues(book_id, user_id, issue_date, status) VALUES(?, ?, CURDATE(), 'ISSUED')";
                try (PreparedStatement ps = c.prepareStatement(insertIssueSql)) {
                    ps.setInt(1, bookId);
                    ps.setInt(2, userId);
                    ps.executeUpdate();
                }

                // 4. Decrement available book count
                String decrementSql = "UPDATE books SET available = available - 1 WHERE id=?";
                try (PreparedStatement ps = c.prepareStatement(decrementSql)) {
                    ps.setInt(1, bookId);
                    ps.executeUpdate();
                }

                c.commit();
                return null;
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        }
    }

    /**
     * Returns an issued book.
     * Updates return_date and status, and increments available book stock.
     * Returns null on success, or a human-readable error message.
     */
    public String returnBook(int recordId, int userId, boolean isAdmin) throws SQLException {
        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                // 1. Lock and check issue record
                String findIssueSql = "SELECT book_id, user_id FROM issues WHERE id=? AND return_date IS NULL FOR UPDATE";
                int bookId = 0;
                try (PreparedStatement ps = c.prepareStatement(findIssueSql)) {
                    ps.setInt(1, recordId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            c.rollback();
                            return "Record not found or already returned.";
                        }
                        if (!isAdmin && rs.getInt("user_id") != userId) {
                            c.rollback();
                            return "Access denied: you can only return books you issued.";
                        }
                        bookId = rs.getInt("book_id");
                    }
                }

                // 2. Update issue record with return date and status
                String updateIssueSql = "UPDATE issues SET return_date=CURDATE(), status='RETURNED' WHERE id=?";
                try (PreparedStatement ps = c.prepareStatement(updateIssueSql)) {
                    ps.setInt(1, recordId);
                    ps.executeUpdate();
                }

                // 3. Increment book availability
                String incrementSql = "UPDATE books SET available = available + 1 WHERE id=?";
                try (PreparedStatement ps = c.prepareStatement(incrementSql)) {
                    ps.setInt(1, bookId);
                    ps.executeUpdate();
                }

                c.commit();
                return null;
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        }
    }

    /**
     * Maps ResultSet row to BorrowRecord.
     */
    private BorrowRecord mapRecord(ResultSet rs) throws SQLException {
        BorrowRecord r = new BorrowRecord();
        r.setId(rs.getInt("id"));
        r.setBookId(rs.getInt("book_id"));
        r.setUserId(rs.getInt("user_id"));
        r.setBookTitle(rs.getString("title"));
        r.setUserName(rs.getString("name"));
        r.setIssueDate(rs.getDate("issue_date"));
        r.setReturnDate(rs.getDate("return_date"));
        String st = rs.getString("status");
        if (st == null || st.trim().isEmpty()) {
            st = (r.getReturnDate() == null) ? "ISSUED" : "RETURNED";
        }
        r.setStatus(st);
        return r;
    }

    /**
     * Returns full borrow history for a specific user.
     */
    public List<BorrowRecord> getUserBorrowHistory(int userId) throws SQLException {
        return queryRecords("WHERE i.user_id = ? ", userId);
    }

    /**
     * Returns currently active (unreturned) borrowings for a specific user.
     */
    public List<BorrowRecord> getUserActiveBorrowings(int userId) throws SQLException {
        return queryRecords("WHERE i.user_id = ? AND i.return_date IS NULL ", userId);
    }

    /**
     * Returns all currently active (unreturned) borrowings across all users.
     */
    public List<BorrowRecord> getAllActiveBorrowings() throws SQLException {
        return queryRecords("WHERE i.return_date IS NULL ", 0);
    }

    /**
     * Returns all records (admin view).
     */
    public List<BorrowRecord> getAllRecords() throws SQLException {
        return queryRecords("", 0);
    }

    /**
     * Helper to list records by condition.
     */
    private List<BorrowRecord> queryRecords(String whereClause, int param) throws SQLException {
        List<BorrowRecord> list = new ArrayList<>();
        String sql = "SELECT i.id, i.book_id, i.user_id, b.title, u.name, i.issue_date, i.return_date, i.status "
                   + "FROM issues i "
                   + "JOIN books b ON b.id = i.book_id "
                   + "JOIN users u ON u.id = i.user_id "
                   + whereClause
                   + "ORDER BY (i.return_date IS NOT NULL), i.issue_date DESC, i.id DESC";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (param > 0) {
                ps.setInt(1, param);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRecord(rs));
                }
            }
        }
        return list;
    }

    /**
     * Counts all active unreturned borrowings.
     */
    public int countActiveBorrowings() throws SQLException {
        String sql = "SELECT COUNT(*) FROM issues WHERE return_date IS NULL";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}

