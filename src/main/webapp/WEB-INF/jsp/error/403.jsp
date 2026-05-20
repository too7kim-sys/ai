<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="ko"><head><meta charset="UTF-8"><title>접근 금지</title>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"/>
</head><body class="bg-light"><div class="container py-5 text-center">
<h1 class="display-1 text-danger">403</h1>
<p class="lead">접근 권한이 없습니다.</p>
<a href="${pageContext.request.contextPath}/dashboard.do" class="btn btn-primary">대시보드로</a>
</div></body></html>
