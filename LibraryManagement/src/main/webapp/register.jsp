<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="header.jspf" %>

<div class="card auth-card">
  <div class="card-header" style="justify-content: center; border-bottom: none; margin-bottom: 8px;">
    <h2 class="card-title">&#128100; Create an Account</h2>
  </div>

  <form method="post" action="${pageContext.request.contextPath}/register">
    <div class="form-group">
      <label for="name">Full Name</label>
      <input type="text" id="name" name="name" value="${name}" placeholder="John Doe" required autofocus>
    </div>

    <div class="form-group">
      <label for="email">Email Address</label>
      <input type="email" id="email" name="email" value="${email}" placeholder="john@example.com" required>
    </div>

    <div class="form-group">
      <label for="password">Password (min 6 characters)</label>
      <input type="password" id="password" name="password" minlength="6" placeholder="Create a password" required>
    </div>

    <div class="form-group">
      <label for="confirmPassword">Confirm Password</label>
      <input type="password" id="confirmPassword" name="confirmPassword" minlength="6" placeholder="Repeat your password" required>
    </div>

    <button type="submit" class="btn btn-primary btn-block" style="margin-top: 8px;">Register Account</button>
  </form>

  <p style="text-align: center; margin-top: 18px; font-size: 14px; color: #64748b;">
    Already have an account? <a href="${pageContext.request.contextPath}/login" style="color: #2563eb; font-weight: 600; text-decoration: none;">Sign in here</a>
  </p>
</div>

<%@ include file="footer.jspf" %>
