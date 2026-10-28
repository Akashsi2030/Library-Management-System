package com.library.servlet;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.library.dao.BorrowDAO;
import com.library.model.BorrowRecord;
import com.library.model.User;

@WebServlet(urlPatterns = {"/issue"})
public class IssueServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final BorrowDAO borrowDAO = new BorrowDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        try {
            List<BorrowRecord> records;
            if (user.isAdmin()) {
                records = borrowDAO.getAllRecords();
            } else {
                records = borrowDAO.getUserBorrowHistory(user.getId());
            }
            req.setAttribute("records", records);
            req.getRequestDispatcher("/issues.jsp").forward(req, res);
        } catch (Exception e) {
            throw new ServletException("Error loading issue records: " + e.getMessage(), e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        String action = req.getParameter("action");

        try {
            if ("issue".equalsIgnoreCase(action)) {
                String bookIdStr = req.getParameter("bookId");
                if (bookIdStr != null && !bookIdStr.trim().isEmpty()) {
                    int bookId = Integer.parseInt(bookIdStr.trim());
                    String err = borrowDAO.issueBook(bookId, user.getId());
                    if (err == null) {
                        session.setAttribute("msg", "Book issued successfully!");
                        res.sendRedirect(req.getContextPath() + "/issue");
                    } else {
                        session.setAttribute("error", err);
                        res.sendRedirect(req.getContextPath() + "/books");
                    }
                    return;
                }
            } else if ("return".equalsIgnoreCase(action)) {
                String issueIdStr = req.getParameter("issueId");
                if (issueIdStr != null && !issueIdStr.trim().isEmpty()) {
                    int issueId = Integer.parseInt(issueIdStr.trim());
                    String err = borrowDAO.returnBook(issueId, user.getId(), user.isAdmin());
                    if (err == null) {
                        session.setAttribute("msg", "Book returned successfully.");
                    } else {
                        session.setAttribute("error", err);
                    }
                    res.sendRedirect(req.getContextPath() + "/issue");
                    return;
                }
            }
            res.sendRedirect(req.getContextPath() + "/issue");
        } catch (Exception e) {
            throw new ServletException("Error processing transaction: " + e.getMessage(), e);
        }
    }
}
