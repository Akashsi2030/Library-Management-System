package com.library.filter;

import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.library.model.User;

@WebFilter(urlPatterns = {
    "/books", 
    "/issue", 
    "/issueBook", 
    "/returnBook", 
    "/borrow",
    "/dashboard.jsp", 
    "/admin_dashboard.jsp", 
    "/user_dashboard.jsp", 
    "/manage_books.jsp", 
    "/bookForm.jsp", 
    "/issues.jsp", 
    "/books.jsp"
})
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // Prevent caching of sensitive protected views
        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        res.setHeader("Pragma", "no-cache");
        res.setDateHeader("Expires", 0);

        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        String path = uri.substring(contextPath.length());

        // 1. Check Authentication
        if (user == null) {
            res.sendRedirect(contextPath + "/login");
            return;
        }

        // 2. Enforce Role-Based Access Control (Admin-only routes)
        boolean isAdminOnly = path.endsWith("admin_dashboard.jsp") ||
                              path.endsWith("manage_books.jsp") ||
                              path.endsWith("bookForm.jsp");

        String action = req.getParameter("action");
        if ("new".equalsIgnoreCase(action) || "edit".equalsIgnoreCase(action) || "delete".equalsIgnoreCase(action)) {
            isAdminOnly = true;
        }

        if (isAdminOnly && !user.isAdmin()) {
            session.setAttribute("error", "Access Denied: You do not have permission to access that administrative resource.");
            res.sendRedirect(contextPath + "/user_dashboard.jsp");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}

