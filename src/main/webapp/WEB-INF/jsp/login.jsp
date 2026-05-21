<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover"/>
    <meta name="theme-color" content="#1e3a8a"/>
    <title>로그인 | 사내 그룹웨어</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css"/>
    <style>
        body {
            background: linear-gradient(135deg, #1e3a8a 0%, #312e81 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 1rem;
        }
        .login-card {
            max-width: 420px;
            width: 100%;
            padding: 2.5rem;
            border-radius: 12px;
            background: white;
            box-shadow: 0 25px 50px -12px rgba(0,0,0,.3);
        }
        .brand { text-align:center; margin-bottom: 2rem; }
        .brand h1 { font-size: 1.5rem; font-weight: 700; color: #1e3a8a; margin-top:.5rem; }
        @media (max-width: 575.98px) {
            .login-card { padding: 1.5rem; }
            .brand h1 { font-size: 1.25rem; }
        }
    </style>
</head>
<body>
<div class="login-card">
    <div class="brand">
        <i class="bi bi-grid-3x3-gap-fill" style="font-size: 3rem; color:#1e3a8a;"></i>
        <h1>사내 그룹웨어</h1>
        <p class="text-muted small mb-0">인사 · 회계 · 결재 통합 솔루션</p>
    </div>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger small">${errorMessage}</div>
    </c:if>
    <c:if test="${not empty infoMessage}">
        <div class="alert alert-info small">${infoMessage}</div>
    </c:if>
    <form action="${pageContext.request.contextPath}/auth/login" method="post">
        <sec:csrfInput/>
        <div class="mb-3">
            <label class="form-label">이메일</label>
            <input type="email" name="email" class="form-control" required autofocus
                   placeholder="user@company.com"/>
        </div>
        <div class="mb-3">
            <label class="form-label">비밀번호</label>
            <input type="password" name="password" class="form-control" required/>
        </div>
        <button type="submit" class="btn btn-primary w-100">
            <i class="bi bi-box-arrow-in-right"></i> 로그인
        </button>
    </form>
    <div class="text-center text-muted small mt-4">
        시드 계정: admin@company.com / Demo!2025
    </div>
</div>
</body>
</html>
