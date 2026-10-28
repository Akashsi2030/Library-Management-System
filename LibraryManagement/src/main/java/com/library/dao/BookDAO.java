package com.library.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.library.model.Book;
import com.library.util.DBConnection;

public class BookDAO {

    private Book map(ResultSet rs) throws SQLException {
        Book b = new Book();
        b.setId(rs.getInt("id"));
        b.setTitle(rs.getString("title"));
        b.setAuthor(rs.getString("author"));
        b.setIsbn(rs.getString("isbn"));
        b.setCategory(rs.getString("category"));
        b.setQuantity(rs.getInt("quantity"));
        b.setAvailable(rs.getInt("available"));
        return b;
    }

    /**
     * Lists all books ordered by title.
     */
    public List<Book> getAllBooks() throws SQLException {
        return search(null);
    }

    /**
     * Searches books by title, author, category, or ISBN.
     */
    public List<Book> search(String keyword) throws SQLException {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ? OR category LIKE ? OR isbn LIKE ? ORDER BY title ASC";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            String k = "%" + (keyword == null ? "" : keyword.trim()) + "%";
            ps.setString(1, k);
            ps.setString(2, k);
            ps.setString(3, k);
            ps.setString(4, k);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    /**
     * Retrieves a single book by ID.
     */
    public Book getById(int id) throws SQLException {
        String sql = "SELECT * FROM books WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    /**
     * Adds a new book to the catalogue.
     */
    public void add(Book b) throws SQLException {
        String sql = "INSERT INTO books(title, author, isbn, category, quantity, available) VALUES(?, ?, ?, ?, ?, ?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, b.getTitle().trim());
            ps.setString(2, b.getAuthor().trim());
            ps.setString(3, b.getIsbn() == null ? "" : b.getIsbn().trim());
            ps.setString(4, b.getCategory() == null ? "General" : b.getCategory().trim());
            int qty = Math.max(1, b.getQuantity());
            ps.setInt(5, qty);
            ps.setInt(6, qty);
            ps.executeUpdate();
        }
    }

    /**
     * Updates an existing book and synchronizes available stock with total quantity delta.
     */
    public void update(Book b) throws SQLException {
        String sql = "UPDATE books SET title=?, author=?, isbn=?, category=?, "
                   + "available = GREATEST(0, available + (? - quantity)), quantity=? WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, b.getTitle().trim());
            ps.setString(2, b.getAuthor().trim());
            ps.setString(3, b.getIsbn() == null ? "" : b.getIsbn().trim());
            ps.setString(4, b.getCategory() == null ? "General" : b.getCategory().trim());
            int qty = Math.max(1, b.getQuantity());
            ps.setInt(5, qty);
            ps.setInt(6, qty);
            ps.setInt(7, b.getId());
            ps.executeUpdate();
        }
    }

    /**
     * Updates book total quantity directly.
     */
    public void updateQuantity(int id, int newQuantity) throws SQLException {
        String sql = "UPDATE books SET available = GREATEST(0, available + (? - quantity)), quantity=? WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, newQuantity);
            ps.setInt(2, newQuantity);
            ps.setInt(3, id);
            ps.executeUpdate();
        }
    }

    /**
     * Deletes a book by ID.
     */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM books WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /**
     * Returns total book copies across the library.
     */
    public int countTotalCopies() throws SQLException {
        String sql = "SELECT COALESCE(SUM(quantity), 0) FROM books";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /**
     * Returns total currently available copies.
     */
    public int countAvailableCopies() throws SQLException {
        String sql = "SELECT COALESCE(SUM(available), 0) FROM books";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}
