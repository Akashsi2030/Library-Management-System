<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:choose>
  <c:when test="${not empty sessionScope.user}">
    <c:choose>
      <c:when test="${sessionScope.user.admin}">
        <c:redirect url="/admin_dashboard.jsp" />
      </c:when>
      <c:otherwise>
        <c:redirect url="/user_dashboard.jsp" />
      </c:otherwise>
    </c:choose>
  </c:when>
  <c:otherwise>
    <c:redirect url="/login" />
  </c:otherwise>
</c:choose>
