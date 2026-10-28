<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="header.jspf" %>

<div class="card" style="max-width: 600px; margin: 30px auto;">
  <div class="card-header">
    <h2 class="card-title">&#128218; ${empty book ? 'Add New Book' : 'Edit Book Details'}</h2>
    <a class="btn btn-secondary btn-sm" href="${pageContext.request.contextPath}/books">&larr; Back to Books</a>
  </div>

  <form method="post" action="${pageContext.request.contextPath}/books">
    <input type="hidden" name="id" value="${book.id}">

    <div class="form-group">
      <label for="title">Book Title *</label>
      <input type="text" id="title" name="title" value="${book.title}" placeholder="e.g. Clean Architecture" required autofocus>
    </div>

    <div class="form-group">
      <label for="author">Author *</label>
      <input type="text" id="author" name="author" value="${book.author}" placeholder="e.g. Robert C. Martin" required>
    </div>

    <div class="form-row">
      <div class="form-group">
        <label for="isbn">ISBN</label>
        <input type="text" id="isbn" name="isbn" value="${book.isbn}" placeholder="e.g. 978-0134494166">
      </div>

      <div class="form-group">
        <label for="category">Category</label>
        <input type="text" id="category" name="category" value="${empty book ? 'Computer Science' : book.category}" placeholder="e.g. Programming, Database, Science">
      </div>
    </div>

    <div class="form-group">
      <label for="quantity">Total Stock Quantity *</label>
      <input type="number" id="quantity" name="quantity" min="1" value="${empty book ? 1 : book.quantity}" required>
    </div>

    <div style="display: flex; gap: 12px; margin-top: 24px;">
      <button type="submit" class="btn btn-primary" style="flex: 1;">
        ${empty book ? '&#10010; Add Book to Catalogue' : '&#128190; Update Book'}
      </button>
      <a class="btn btn-secondary" href="${pageContext.request.contextPath}/books">Cancel</a>
    </div>
  </form>
</div>

<%@ include file="footer.jspf" %>

