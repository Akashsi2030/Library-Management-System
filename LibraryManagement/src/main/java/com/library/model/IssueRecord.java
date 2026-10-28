package com.library.model;

import java.sql.Date;

/**
 * IssueRecord maintains backwards compatibility with earlier templates,
 * extending the canonical BorrowRecord.
 */
public class IssueRecord extends BorrowRecord {
    private static final long serialVersionUID = 1L;

    public IssueRecord() {
        super();
    }

    public IssueRecord(int id, int userId, int bookId, String bookTitle, String userName, 
                       Date issueDate, Date returnDate, String status) {
        super(id, userId, bookId, bookTitle, userName, issueDate, returnDate, status);
    }
}
