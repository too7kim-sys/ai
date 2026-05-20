<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko"><head><meta charset="UTF-8"><title>서버 오류</title>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"/>
</head><body class="bg-light"><div class="container py-5 text-center">
<h1 class="display-1 text-danger">500</h1>
<p class="lead">서버 오류가 발생했습니다.</p>
<c:if test="${not empty message}">
    <div class="alert alert-secondary text-start small">${message}</div>
</c:if>
<a href="${pageContext.request.contextPath}/dashboard.do" class="btn btn-primary">대시보드로</a>
</div></body></html>
