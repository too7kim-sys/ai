<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<ul class="nav nav-tabs mb-3">
    <li class="nav-item"><a class="nav-link <c:if test='${active eq "user"}'>active</c:if>"
                            href="${pageContext.request.contextPath}/sys/user/list.do"><i class="bi bi-people"></i> 사용자 관리</a></li>
    <li class="nav-item"><a class="nav-link <c:if test='${active eq "login"}'>active</c:if>"
                            href="${pageContext.request.contextPath}/sys/login-log.do"><i class="bi bi-shield-lock"></i> 로그인 이력</a></li>
    <li class="nav-item"><a class="nav-link <c:if test='${active eq "audit"}'>active</c:if>"
                            href="${pageContext.request.contextPath}/sys/audit-log.do"><i class="bi bi-journal-text"></i> 감사 로그</a></li>
    <li class="nav-item"><a class="nav-link <c:if test='${active eq "code"}'>active</c:if>"
                            href="${pageContext.request.contextPath}/sys/code.do"><i class="bi bi-tags"></i> 공통 코드</a></li>
    <li class="nav-item"><a class="nav-link <c:if test='${active eq "menu"}'>active</c:if>"
                            href="${pageContext.request.contextPath}/sys/menu.do"><i class="bi bi-list-ul"></i> 메뉴 관리</a></li>
    <li class="nav-item"><a class="nav-link <c:if test='${active eq "info"}'>active</c:if>"
                            href="${pageContext.request.contextPath}/sys/info.do"><i class="bi bi-info-circle"></i> 시스템 정보</a></li>
</ul>
