<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.library.dao.*, com.library.model.*, java.util.List" %>
<%@ include file="header.jspf" %>

<%
    // Ensure only Admin accesses this page
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null || !currentUser.isAdmin()) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }

    BookDAO bookDAO = new BookDAO();
    BorrowDAO borrowDAO = new BorrowDAO();
    UserDAO userDAO = new UserDAO();

    int totalBooks = 0;
    int availableCopies = 0;
    int activeBorrowings = 0;
    int totalUsers = 0;
    List<BorrowRecord> activeRecords = null;

    try {
        totalBooks = bookDAO.countTotalCopies();
        availableCopies = bookDAO.countAvailableCopies();
        activeBorrowings = borrowDAO.countActiveBorrowings();
        totalUsers = userDAO.countUsers();
        activeRecords = borrowDAO.getAllActiveBorrowings();
    } catch (Exception e) {
        // Fallback gracefully
    }
    request.setAttribute("activeRecords", activeRecords);
%>

<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px;">
  <div>
    <h1 style="font-size: 26px; font-weight: 800; color: #1e293b;">Admin Dashboard</h1>
    <p style="color: #64748b; font-size: 14px;">Welcome back, <%= currentUser.getName() %>. Here is today's library overview.</p>
  </div>
  <div style="display: flex; gap: 10px;">
    <a class="btn btn-primary" href="${pageContext.request.contextPath}/manage_books.jsp">+ Add New Book</a>
    <a class="btn btn-secondary" href="${pageContext.request.contextPath}/books">Browse Books</a>
  </div>
</div>

<!-- Stats Counter Grid -->
<div class="stats-grid">
  <div class="stat-card">
    <div class="stat-icon">&#128218;</div>
    <div class="stat-content">
      <div class="stat-value"><%= totalBooks %></div>
      <div class="stat-label">Total Book Copies</div>
    </div>
  </div>

  <div class="stat-card">
    <div class="stat-icon green">&#10004;</div>
    <div class="stat-content">
      <div class="stat-value"><%= availableCopies %></div>
      <div class="stat-label">Available Copies</div>
    </div>
  </div>

  <div class="stat-card">
    <div class="stat-icon orange">&#9200;</div>
    <div class="stat-content">
      <div class="stat-value"><%= activeBorrowings %></div>
      <div class="stat-label">Currently Issued</div>
    </div>
  </div>

  <div class="stat-card">
    <div class="stat-icon purple">&#128101;</div>
    <div class="stat-content">
      <div class="stat-value"><%= totalUsers %></div>
      <div class="stat-label">Registered Members</div>
    </div>
  </div>
</div>

<!-- Active Borrowings Table -->
<div class="card">
  <div class="card-header">
    <h2 class="card-title">&#128221; Active Issued Books (Pending Returns)</h2>
    <a href="${pageContext.request.contextPath}/issue" class="btn btn-secondary btn-sm">View All History &rarr;</a>
  </div>

  <div class="table-responsive">
    <table>
      <thead>
        <tr>
          <th>Record #</th>
          <th>Book Title</th>
          <th>Borrower Name</th>
          <th>Issued Date</th>
          <th>Status</th>
          <th>Action</th>
        </tr>
      </thead>
      <tbody>
        <c:forEach var="rec" items="${activeRecords}">
          <tr>
            <td>#${rec.id}</td>
            <td><strong>${rec.bookTitle}</strong></td>
            <td>${rec.userName}</td>
            <td>${rec.issueDate}</td>
            <td>
              <span class="status-badge badge-issued">&#9679; Issued</span>
            </td>
            <td>
              <form method="post" action="${pageContext.request.contextPath}/returnBook" style="display:inline;">
                <input type="hidden" name="issueId" value="${rec.id}">
                <button type="submit" class="btn btn-success btn-sm" onclick="return confirm('Mark this book as returned?');">
                  Mark Returned
                </button>
              </form>
            </td>
          </tr>
        </c:forEach>
        <c:if test="${empty activeRecords}">
          <tr>
            <td colspan="6" style="text-align: center; color: #94a3b8; padding: 24px;">
              &#128522; No pending active borrowings. All books have been returned!
            </td>
          </tr>
        </c:if>
      </tbody>
    </table>
  </div>
</div>

<%@ include file="footer.jspf" %>

