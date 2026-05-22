<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover"/>
    <meta name="theme-color" content="#313a46"/>
    <title>로그인 | 사내 그룹웨어</title>
    <link rel="preconnect" href="https://fonts.googleapis.com"/>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700&display=swap"/>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css"/>
    <style>
        body {
            font-family: "Nunito", -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
            background: #313a46;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 1rem;
        }
        .login-wrap { max-width: 420px; width: 100%; }
        .login-brand {
            text-align: center;
            color: #fff;
            margin-bottom: 1.5rem;
        }
        .login-brand i { font-size: 2.5rem; color: #727cf5; }
        .login-brand h1 { font-size: 1.35rem; font-weight: 700; margin-top: .5rem; }
        .login-brand p { color: #8c98a5; font-size: .85rem; margin: 0; }
        .login-card {
            background: #fff;
            border-radius: .4rem;
            padding: 2rem;
            box-shadow: 0 0 35px 0 rgba(0,0,0,.25);
        }
        .login-card .form-label { font-weight: 600; color: #323a46; font-size: .85rem; }
        .login-card .form-control { padding: .55rem .75rem; }
        .login-card .form-control:focus {
            border-color: #b3b9f9;
            box-shadow: 0 0 0 .2rem rgba(114,124,245,.15);
        }
        .btn-login {
            background: #727cf5; border-color: #727cf5; color: #fff;
            font-weight: 600; padding: .55rem;
        }
        .btn-login:hover { background: #5b63c4; border-color: #5b63c4; color: #fff; }
        .seed-box {
            background: #f9fafd;
            border: 1px dashed #dde3ec;
            border-radius: .3rem;
            font-size: .78rem;
            color: #8a96a3;
            padding: .6rem .8rem;
            margin-top: 1.25rem;
            text-align: center;
        }
        @media (max-width: 575.98px) {
            .login-card { padding: 1.5rem; }
            .login-brand h1 { font-size: 1.15rem; }
        }
    </style>
</head>
<body>
<div class="login-wrap">
    <div class="login-brand">
        <i class="bi bi-grid-3x3-gap-fill"></i>
        <h1>사내 그룹웨어</h1>
        <p>인사 · 회계 · 결재 · 협업 통합 솔루션</p>
    </div>
    <div class="login-card">
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger small py-2">${errorMessage}</div>
        </c:if>
        <c:if test="${not empty infoMessage}">
            <div class="alert alert-info small py-2">${infoMessage}</div>
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
                <input type="password" name="password" class="form-control" required
                       placeholder="비밀번호"/>
            </div>
            <button type="submit" class="btn btn-login w-100">
                <i class="bi bi-box-arrow-in-right"></i> 로그인
            </button>
        </form>
        <div class="seed-box">
            <i class="bi bi-info-circle"></i>
            데모 계정 — <strong>admin@company.com</strong> / <strong>Demo!2025</strong>
        </div>
    </div>
</div>
</body>
</html>
