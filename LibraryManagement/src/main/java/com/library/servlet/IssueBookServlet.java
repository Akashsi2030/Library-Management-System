package com.library.servlet;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.library.dao.BorrowDAO;
import com.library.model.User;

@WebServlet(urlPatterns = {"/issueBook", "/borrow"})
public class IssueBookServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final BorrowDAO borrowDAO = new BorrowDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.sendRedirect(req.getContextPath() + "/books");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        String bookIdStr = req.getParameter("bookId");

        if (bookIdStr == null || bookIdStr.trim().isEmpty()) {
            session.setAttribute("error", "No book selected to issue.");
            res.sendRedirect(req.getContextPath() + "/books");
            return;
        }

        try {
            int bookId = Integer.parseInt(bookIdStr.trim());
            String errorMsg = borrowDAO.issueBook(bookId, user.getId());

            if (errorMsg == null) {
                session.setAttribute("msg", "Book issued successfully! Happy reading.");
                res.sendRedirect(req.getContextPath() + "/issue");
            } else {
                session.setAttribute("error", errorMsg);
                res.sendRedirect(req.getContextPath() + "/books");
            }
        } catch (Exception e) {
            throw new ServletException("Error issuing book: " + e.getMessage(), e);
        }
    }
}

