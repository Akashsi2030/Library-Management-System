package com.library.servlet;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.library.dao.UserDAO;
import com.library.model.User;

@WebServlet(urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            User u = (User) session.getAttribute("user");
            if (u.isAdmin()) {
                res.sendRedirect(req.getContextPath() + "/admin_dashboard.jsp");
            } else {
                res.sendRedirect(req.getContextPath() + "/user_dashboard.jsp");
            }
            return;
        }
        req.getRequestDispatcher("/login.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        if (email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()) {
            req.setAttribute("error", "Email and password are required.");
            req.getRequestDispatcher("/login.jsp").forward(req, res);
            return;
        }

        try {
            User user = userDAO.login(email.trim(), password);
            if (user == null) {
                req.setAttribute("error", "Invalid email or password.");
                req.setAttribute("email", email.trim());
                req.getRequestDispatcher("/login.jsp").forward(req, res);
                return;
            }

            // Create clean new session
            HttpSession oldSession = req.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }

            HttpSession session = req.getSession(true);
            session.setAttribute("user", user);
            session.setMaxInactiveInterval(30 * 60); // 30 minutes

            if (user.isAdmin()) {
                res.sendRedirect(req.getContextPath() + "/admin_dashboard.jsp");
            } else {
                res.sendRedirect(req.getContextPath() + "/user_dashboard.jsp");
            }
        } catch (Exception e) {
            throw new ServletException("Login error occurred: " + e.getMessage(), e);
        }
    }
}
