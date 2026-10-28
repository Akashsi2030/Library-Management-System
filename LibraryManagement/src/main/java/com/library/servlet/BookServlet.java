package com.library.servlet;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.library.dao.BookDAO;
import com.library.model.Book;
import com.library.model.User;

@WebServlet(urlPatterns = {"/books"})
public class BookServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final BookDAO bookDAO = new BookDAO();

    private boolean isAdmin(HttpServletRequest req) {
        User u = (User) req.getSession().getAttribute("user");
        return u != null && u.isAdmin();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null || action.trim().isEmpty()) {
            action = "list";
        }

        try {
            switch (action) {
                case "new":
                    if (!isAdmin(req)) {
                        res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Admin role required.");
                        return;
                    }
                    req.getRequestDispatcher("/manage_books.jsp").forward(req, res);
                    break;

                case "edit":
                    if (!isAdmin(req)) {
                        res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Admin role required.");
                        return;
                    }
                    String editIdStr = req.getParameter("id");
                    if (editIdStr != null && !editIdStr.trim().isEmpty()) {
                        Book book = bookDAO.getById(Integer.parseInt(editIdStr.trim()));
                        req.setAttribute("book", book);
                    }
                    req.getRequestDispatcher("/manage_books.jsp").forward(req, res);
                    break;

                case "delete":
                    if (!isAdmin(req)) {
                        res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Admin role required.");
                        return;
                    }
                    String delIdStr = req.getParameter("id");
                    if (delIdStr != null && !delIdStr.trim().isEmpty()) {
                        bookDAO.delete(Integer.parseInt(delIdStr.trim()));
                        req.getSession().setAttribute("msg", "Book deleted successfully.");
                    }
                    res.sendRedirect(req.getContextPath() + "/books");
                    break;

                case "list":
                default:
                    String query = req.getParameter("q");
                    List<Book> books;
                    if (query != null && !query.trim().isEmpty()) {
                        books = bookDAO.search(query.trim());
                        req.setAttribute("q", query.trim());
                    } else {
                        books = bookDAO.getAllBooks();
                    }
                    req.setAttribute("books", books);
                    req.getRequestDispatcher("/books.jsp").forward(req, res);
                    break;
            }
        } catch (Exception e) {
            throw new ServletException("Error in BookServlet: " + e.getMessage(), e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if (!isAdmin(req)) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Admin role required.");
            return;
        }

        try {
            String title = req.getParameter("title");
            String author = req.getParameter("author");
            String isbn = req.getParameter("isbn");
            String category = req.getParameter("category");
            String quantityStr = req.getParameter("quantity");
            String idStr = req.getParameter("id");

            if (title == null || title.trim().isEmpty() ||
                author == null || author.trim().isEmpty() ||
                quantityStr == null || quantityStr.trim().isEmpty()) {
                req.getSession().setAttribute("error", "Title, Author, and Quantity are required.");
                res.sendRedirect(req.getContextPath() + "/books");
                return;
            }

            int quantity = Integer.parseInt(quantityStr.trim());
            Book b = new Book();
            b.setTitle(title.trim());
            b.setAuthor(author.trim());
            b.setIsbn(isbn != null ? isbn.trim() : "");
            b.setCategory(category != null && !category.trim().isEmpty() ? category.trim() : "General");
            b.setQuantity(quantity);

            if (idStr == null || idStr.trim().isEmpty()) {
                // New Book
                bookDAO.add(b);
                req.getSession().setAttribute("msg", "Book added successfully.");
            } else {
                // Existing Book Update
                b.setId(Integer.parseInt(idStr.trim()));
                bookDAO.update(b);
                req.getSession().setAttribute("msg", "Book details updated successfully.");
            }
            res.sendRedirect(req.getContextPath() + "/books");
        } catch (Exception e) {
            throw new ServletException("Failed to save book: " + e.getMessage(), e);
        }
    }
}
