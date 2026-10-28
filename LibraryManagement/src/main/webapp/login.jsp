<%@ page contentType="text/html;charset=UTF-8" language="java" %>
  <%@ include file="header.jspf" %>

    <div class="card auth-card">
      <div class="card-header" style="justify-content: center; border-bottom: none; margin-bottom: 8px;">
        <h2 class="card-title">&#128274; Sign In to Library</h2>
      </div>

      <form method="post" action="${pageContext.request.contextPath}/login">
        <div class="form-group">
          <label for="email">Email Address</label>
          <input type="email" id="email" name="email" value="${email}" placeholder="you@example.com" required autofocus>
        </div>

        <div class="form-group">
          <label for="password">Password</label>
          <input type="password" id="password" name="password" placeholder="Enter your password" required>
        </div>

        <button type="submit" class="btn btn-primary btn-block" style="margin-top: 8px;">Sign In</button>
      </form>

      <p style="text-align: center; margin-top: 18px; font-size: 14px; color: #64748b;">
        Don't have an account? <a href="${pageContext.request.contextPath}/register"
          style="color: #2563eb; font-weight: 600; text-decoration: none;">Create one here</a>
      </p>

      <div class="hint-box">
        <strong>Demo Credentials:</strong><br>
        &bull; <strong>Admin:</strong> <code>admin@library.com</code> / <code>admin123</code><br>
        &bull; <strong>Member:</strong> <code>user@library.com</code> / <code>user123</code>
      </div>
    </div>

    <%@ include file="footer.jspf" %>