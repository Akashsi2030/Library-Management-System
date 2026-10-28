package com.library.dao;

import java.sql.SQLException;
import java.util.List;

import com.library.model.BorrowRecord;

/**
 * IssueDAO maintains backward compatibility with existing templates
 * while utilizing BorrowDAO.
 */
public class IssueDAO extends BorrowDAO {

    public String issue(int bookId, int userId) throws SQLException {
        return super.issueBook(bookId, userId);
    }

    public List<BorrowRecord> list(int userId) throws SQLException {
        if (userId > 0) {
            return super.getUserBorrowHistory(userId);
        }
        return super.getAllRecords();
    }
}
