<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="header.jspf" %>

<div class="card">
  <div class="card-header">
    <div>
      <h2 class="card-title">&#128221; ${sessionScope.user.admin ? 'All Borrowing Transactions' : 'My Borrow History'}</h2>
      <p style="color: #64748b; font-size: 13px;">
        ${sessionScope.user.admin ? 'Listing of all library circulation records.' : 'Track your borrowed books and return dates.'}
      </p>
    </div>
    <a class="btn btn-secondary btn-sm" href="${pageContext.request.contextPath}/books">&larr; Back to Books</a>
  </div>

  <div class="table-responsive">
    <table>
      <thead>
        <tr>
          <th>Record #</th>
          <th>Book Title</th>
          <th>Borrower</th>
          <th>Issued Date</th>
          <th>Returned Date</th>
          <th>Status</th>
          <th>Action</th>
        </tr>
      </thead>
      <tbody>
        <c:forEach var="rec" items="${records}">
          <tr>
            <td>#${rec.id}</td>
            <td><strong>${rec.bookTitle}</strong></td>
            <td>${rec.userName}</td>
            <td>${rec.issueDate}</td>
            <td>
              <c:choose>
                <c:when test="${not empty rec.returnDate}">
                  ${rec.returnDate}
                </c:when>
                <c:otherwise>
                  <span style="color: #94a3b8;">&mdash;</span>
                </c:otherwise>
              </c:choose>
            </td>
            <td>
              <c:choose>
                <c:when test="${rec.status eq 'ISSUED'}">
                  <span class="status-badge badge-issued">&#9679; Issued</span>
                </c:when>
                <c:otherwise>
                  <span class="status-badge badge-returned">&#10004; Returned</span>
                </c:otherwise>
              </c:choose>
            </td>
            <td>
              <c:if test="${rec.status eq 'ISSUED'}">
                <form method="post" action="${pageContext.request.contextPath}/returnBook" style="display:inline;">
                  <input type="hidden" name="issueId" value="${rec.id}">
                  <button type="submit" class="btn btn-success btn-sm" onclick="return confirm('Confirm returning this book?');">
                    Return Book
                  </button>
                </form>
              </c:if>
              <c:if test="${rec.status eq 'RETURNED'}">
                <span style="color: #64748b; font-size: 13px;">Completed</span>
              </c:if>
            </td>
          </tr>
        </c:forEach>

        <c:if test="${empty records}">
          <tr>
            <td colspan="7" style="text-align: center; color: #94a3b8; padding: 24px;">
              No borrow records found.
            </td>
          </tr>
        </c:if>
      </tbody>
    </table>
  </div>
</div>

<%@ include file="footer.jspf" %>
