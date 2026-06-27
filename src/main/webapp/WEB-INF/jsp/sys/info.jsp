<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<title>시스템 - 시스템 정보</title>
<h2 class="mb-3"><i class="bi bi-gear"></i> 시스템 관리</h2>
<c:set var="active" value="info" scope="request"/>
<jsp:include page="_nav.jsp"/>

<div class="row g-3">
    <div class="col-md-6">
        <div class="card mb-3">
            <div class="card-header"><i class="bi bi-cpu"></i> 시스템</div>
            <table class="table mb-0">
                <c:forEach var="e" items="${sys}">
                    <tr><th style="width:160px;" class="bg-light">${e.key}</th><td>${e.value}</td></tr>
                </c:forEach>
            </table>
        </div>

        <div class="card mb-3">
            <div class="card-header"><i class="bi bi-memory"></i> JVM 메모리</div>
            <table class="table mb-0">
                <c:forEach var="e" items="${mem}">
                    <tr><th style="width:160px;" class="bg-light">${e.key}</th><td>${e.value}</td></tr>
                </c:forEach>
            </table>
        </div>
    </div>

    <div class="col-md-6">
        <div class="card mb-3">
            <div class="card-header"><i class="bi bi-database"></i> 데이터베이스</div>
            <table class="table mb-0">
                <c:forEach var="e" items="${db}">
                    <tr><th style="width:160px;" class="bg-light">${e.key}</th><td class="small">${e.value}</td></tr>
                </c:forEach>
            </table>
        </div>

        <div class="card">
            <div class="card-header"><i class="bi bi-bar-chart"></i> 데이터 통계</div>
            <table class="table mb-0">
                <c:forEach var="e" items="${counts}">
                    <tr><th style="width:160px;" class="bg-light">${e.key}</th><td>${e.value}</td></tr>
                </c:forEach>
            </table>
        </div>
    </div>
</div>
