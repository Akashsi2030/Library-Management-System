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

@WebServlet(urlPatterns = {"/returnBook"})
public class ReturnBookServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final BorrowDAO borrowDAO = new BorrowDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.sendRedirect(req.getContextPath() + "/issue");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        String recordIdStr = req.getParameter("issueId");
        if (recordIdStr == null || recordIdStr.trim().isEmpty()) {
            recordIdStr = req.getParameter("recordId");
        }

        if (recordIdStr == null || recordIdStr.trim().isEmpty()) {
            session.setAttribute("error", "Invalid return request: record ID missing.");
            res.sendRedirect(req.getContextPath() + "/issue");
            return;
        }

        try {
            int recordId = Integer.parseInt(recordIdStr.trim());
            String errorMsg = borrowDAO.returnBook(recordId, user.getId(), user.isAdmin());

            if (errorMsg == null) {
                session.setAttribute("msg", "Book returned successfully. Thank you!");
            } else {
                session.setAttribute("error", errorMsg);
            }
            res.sendRedirect(req.getContextPath() + "/issue");
        } catch (Exception e) {
            throw new ServletException("Error returning book: " + e.getMessage(), e);
        }
    }
}

