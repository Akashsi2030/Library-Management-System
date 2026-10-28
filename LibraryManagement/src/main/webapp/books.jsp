<%@ page contentType="text/html;charset=UTF-8" language="java" %>
  <%@ include file="header.jspf" %>

    <div class="card">
      <div class="card-header">
        <div>
          <h2 class="card-title">&#128218; Books Catalogue</h2>
          <p style="color: #64748b; font-size: 13px;">Browse all available library titles or search by keyword.</p>
        </div>

        <c:if test="${sessionScope.user.admin}">
          <a class="btn btn-primary" href="${pageContext.request.contextPath}/books?action=new">+ Add New Book</a>
        </c:if>
      </div>

      <!-- Search Filter Form -->
      <form method="get" action="${pageContext.request.contextPath}/books" class="search-form">
        <input type="text" name="q" value="${q}" placeholder="Search by title, author, category, or ISBN...">
        <button type="submit" class="btn btn-primary">&#128269; Search</button>
        <c:if test="${not empty q}">
          <a class="btn btn-secondary" href="${pageContext.request.contextPath}/books">Clear</a>
        </c:if>
      </form>

      <div class="table-responsive">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Title</th>
              <th>Author</th>
              <th>ISBN</th>
              <th>Category</th>
              <th>Total</th>
              <th>Available</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="b" items="${books}">
              <tr>
                <td>#${b.id}</td>
                <td><strong>${b.title}</strong></td>
                <td>${b.author}</td>
                <td><code>${empty b.isbn ? 'N/A' : b.isbn}</code></td>
                <td>${b.category}</td>
                <td>${b.quantity}</td>
                <td>
                  <c:choose>
                    <c:when test="${b.available > 0}">
                      <span style="color: #16a34a; font-weight: 700;">${b.available}</span>
                    </c:when>
                    <c:otherwise>
                      <span class="status-badge badge-out-of-stock">0 left</span>
                    </c:otherwise>
                  </c:choose>
                </td>
                <td>
                  <div style="display: flex; gap: 6px; align-items: center;">
                    <!-- Issue / Borrow Action -->
                    <c:if test="${b.available > 0}">
                      <form method="post" action="${pageContext.request.contextPath}/issueBook" style="display:inline;">
                        <input type="hidden" name="bookId" value="${b.id}">
                        <button type="submit" class="btn btn-primary btn-sm">&#128218; Borrow</button>
                      </form>
                    </c:if>
                    <c:if test="${b.available <= 0}">
                      <span style="color: #94a3b8; font-size: 13px;">Unavailable</span>
                    </c:if>

                    <!-- Admin CRUD Actions -->
                    <c:if test="${sessionScope.user.admin}">
                      <a class="btn btn-secondary btn-sm"
                        href="${pageContext.request.contextPath}/books?action=edit&id=${b.id}">Edit</a>
                      <a class="btn btn-danger btn-sm"
                        href="${pageContext.request.contextPath}/books?action=delete&id=${b.id}"
                        onclick="return confirm('Are you sure you want to permanently delete this book: &quot;${b.title}&quot;?');">
                        Delete
                      </a>
                    </c:if>
                  </div>
                </td>
              </tr>
            </c:forEach>

            <c:if test="${empty books}">
              <tr>
                <td colspan="8" style="text-align: center; color: #94a3b8; padding: 24px;">
                  No books found matching your criteria.
                </td>
              </tr>
            </c:if>
          </tbody>
        </table>
      </div>
    </div>

    <%@ include file="footer.jspf" %>