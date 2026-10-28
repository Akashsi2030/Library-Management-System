<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.library.dao.*, com.library.model.*, java.util.List" %>
<%@ include file="header.jspf" %>

<%
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }

    BorrowDAO borrowDAO = new BorrowDAO();
    BookDAO bookDAO = new BookDAO();
    List<BorrowRecord> myActiveRecords = null;
    List<Book> availableBooks = null;

    try {
        myActiveRecords = borrowDAO.getUserActiveBorrowings(currentUser.getId());
        availableBooks = bookDAO.getAllBooks();
    } catch (Exception e) {
        // Fallback
    }
    request.setAttribute("myActiveRecords", myActiveRecords);
    request.setAttribute("availableBooks", availableBooks);
%>

<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px;">
  <div>
    <h1 style="font-size: 26px; font-weight: 800; color: #1e293b;">Member Dashboard</h1>
    <p style="color: #64748b; font-size: 14px;">Welcome, <%= currentUser.getName() %>! Browse books or return your borrowed copies.</p>
  </div>
  <a class="btn btn-primary" href="${pageContext.request.contextPath}/books">&#128269; Explore All Books</a>
</div>

<!-- My Currently Borrowed Books Section -->
<div class="card">
  <div class="card-header">
    <h2 class="card-title">&#128214; My Currently Issued Books</h2>
    <a href="${pageContext.request.contextPath}/issue" class="btn btn-secondary btn-sm">Full Borrow History &rarr;</a>
  </div>

  <div class="table-responsive">
    <table>
      <thead>
        <tr>
          <th>Issue ID</th>
          <th>Book Title</th>
          <th>Issued On</th>
          <th>Status</th>
          <th>Action</th>
        </tr>
      </thead>
      <tbody>
        <c:forEach var="r" items="${myActiveRecords}">
          <tr>
            <td>#${r.id}</td>
            <td><strong>${r.bookTitle}</strong></td>
            <td>${r.issueDate}</td>
            <td>
              <span class="status-badge badge-issued">&#9679; Issued</span>
            </td>
            <td>
              <form method="post" action="${pageContext.request.contextPath}/returnBook" style="display:inline;">
                <input type="hidden" name="issueId" value="${r.id}">
                <button type="submit" class="btn btn-success btn-sm" onclick="return confirm('Do you want to return this book?');">
                  Return Book
                </button>
              </form>
            </td>
          </tr>
        </c:forEach>
        <c:if test="${empty myActiveRecords}">
          <tr>
            <td colspan="5" style="text-align: center; color: #94a3b8; padding: 24px;">
              You do not have any books issued at the moment. Browse the collection below to borrow one!
            </td>
          </tr>
        </c:if>
      </tbody>
    </table>
  </div>
</div>

<!-- Available Books to Borrow -->
<div class="card">
  <div class="card-header">
    <h2 class="card-title">&#128218; Popular Books Available to Borrow</h2>
    <a href="${pageContext.request.contextPath}/books" class="btn btn-secondary btn-sm">View All &rarr;</a>
  </div>

  <div class="table-responsive">
    <table>
      <thead>
        <tr>
          <th>Title</th>
          <th>Author</th>
          <th>Category</th>
          <th>Available</th>
          <th>Action</th>
        </tr>
      </thead>
      <tbody>
        <c:forEach var="b" items="${availableBooks}">
          <tr>
            <td><strong>${b.title}</strong></td>
            <td>${b.author}</td>
            <td>${b.category}</td>
            <td>
              <c:choose>
                <c:when test="${b.available > 0}">
                  <span style="color: #16a34a; font-weight: 600;">${b.available} copies available</span>
                </c:when>
                <c:otherwise>
                  <span class="status-badge badge-out-of-stock">Out of Stock</span>
                </c:otherwise>
              </c:choose>
            </td>
            <td>
              <c:if test="${b.available > 0}">
                <form method="post" action="${pageContext.request.contextPath}/issueBook" style="display:inline;">
                  <input type="hidden" name="bookId" value="${b.id}">
                  <button type="submit" class="btn btn-primary btn-sm">&#128218; Borrow</button>
                </form>
              </c:if>
              <c:if test="${b.available <= 0}">
                <span style="color: #94a3b8; font-size: 13px;">Unavailable</span>
              </c:if>
            </td>
          </tr>
        </c:forEach>
      </tbody>
    </table>
  </div>
</div>

<%@ include file="footer.jspf" %>

