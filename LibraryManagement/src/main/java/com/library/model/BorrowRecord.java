package com.library.model;

import java.io.Serializable;
import java.sql.Date;

public class BorrowRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int userId;
    private int bookId;
    private String bookTitle;
    private String userName;
    private Date issueDate;
    private Date returnDate;
    private String status; // "ISSUED" or "RETURNED"

    // Default constructor
    public BorrowRecord() {}

    // Constructor without id
    public BorrowRecord(int userId, int bookId, Date issueDate, Date returnDate, String status) {
        this.userId = userId;
        this.bookId = bookId;
        this.issueDate = issueDate;
        this.returnDate = returnDate;
        this.status = status;
    }

    // Full constructor
    public BorrowRecord(int id, int userId, int bookId, String bookTitle, String userName, 
                        Date issueDate, Date returnDate, String status) {
        this.id = id;
        this.userId = userId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.userName = userName;
        this.issueDate = issueDate;
        this.returnDate = returnDate;
        this.status = status;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
        if (returnDate != null) {
            this.status = "RETURNED";
        }
    }

    public String getStatus() {
        if (this.status != null && !this.status.trim().isEmpty()) {
            return this.status;
        }
        return (this.returnDate == null) ? "ISSUED" : "RETURNED";
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "BorrowRecord [id=" + id + ", userId=" + userId + ", bookId=" + bookId 
                + ", bookTitle=" + bookTitle + ", userName=" + userName 
                + ", issueDate=" + issueDate + ", returnDate=" + returnDate 
                + ", status=" + getStatus() + "]";
    }
}

