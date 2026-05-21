<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>시스템 - 메뉴 관리</title>
<h2 class="mb-3"><i class="bi bi-gear"></i> 시스템 관리</h2>
<c:set var="active" value="menu" scope="request"/>
<jsp:include page="_nav.jsp"/>

<div class="row g-3">
    <div class="col-md-7">
        <div class="card">
            <div class="card-header d-flex justify-content-between">
                <strong>메뉴 목록</strong>
                <a class="btn btn-sm btn-primary" href="?menuId=0">
                    <i class="bi bi-plus"></i> 새 메뉴
                </a>
            </div>
            <table class="table table-hover mb-0 align-middle">
                <thead class="table-light">
                <tr><th>ID</th><th>이름</th><th>URL</th><th>상위</th><th style="width:60px;">정렬</th><th>역할</th><th style="width:60px;">사용</th><th></th></tr>
                </thead>
                <tbody>
                <c:forEach var="m" items="${list}">
                    <tr>
                        <td>${m.menuId}</td>
                        <td><i class="bi bi-${empty m.icon ? 'circle' : m.icon}"></i> ${m.menuNm}</td>
                        <td class="small text-muted"><code>${m.url}</code></td>
                        <td class="small">${m.parentNm}</td>
                        <td>${m.sortNo}</td>
                        <td class="small"><c:if test="${not empty m.roleCsv}"><span class="badge bg-secondary">${m.roleCsv}</span></c:if></td>
                        <td>
                            <c:choose>
                                <c:when test="${m.useYn eq 'Y'}"><span class="badge bg-success">활성</span></c:when>
                                <c:otherwise><span class="badge bg-secondary">중지</span></c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <a class="btn btn-sm btn-outline-primary" href="?menuId=${m.menuId}"><i class="bi bi-pencil"></i></a>
                            <form method="post" action="${pageContext.request.contextPath}/sys/menu/delete.do" class="d-inline"
                                  onsubmit="return confirm('삭제하시겠습니까?')">
                                <sec:csrfInput/><input type="hidden" name="menuId" value="${m.menuId}"/>
                                <button class="btn btn-sm btn-outline-danger"><i class="bi bi-trash"></i></button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty list}">
                    <tr><td colspan="8" class="text-center text-muted py-3">메뉴가 없습니다.</td></tr>
                </c:if>
                </tbody>
            </table>
        </div>
    </div>

    <div class="col-md-5">
        <div class="card">
            <div class="card-header">
                <strong>
                    <c:choose>
                        <c:when test="${menu != null && menu.menuId != null}">메뉴 편집 #${menu.menuId}</c:when>
                        <c:otherwise>새 메뉴</c:otherwise>
                    </c:choose>
                </strong>
            </div>
            <form method="post" action="${pageContext.request.contextPath}/sys/menu/save.do" class="card-body">
                <sec:csrfInput/>
                <c:if test="${menu != null && menu.menuId != null}">
                    <input type="hidden" name="menuId" value="${menu.menuId}"/>
                </c:if>
                <div class="mb-2"><label class="form-label">메뉴 이름</label>
                    <input type="text" name="menuNm" class="form-control" value="${menu.menuNm}" required maxlength="100"/></div>
                <div class="mb-2"><label class="form-label">URL</label>
                    <input type="text" name="url" class="form-control" value="${menu.url}" maxlength="255"/></div>
                <div class="row g-2 mb-2">
                    <div class="col-md-6">
                        <label class="form-label">상위 메뉴</label>
                        <select name="parentId" class="form-select">
                            <option value="">(없음)</option>
                            <c:forEach var="p" items="${list}">
                                <c:if test="${empty p.parentId}">
                                    <option value="${p.menuId}" <c:if test="${menu.parentId == p.menuId}">selected</c:if>>${p.menuNm}</option>
                                </c:if>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label">정렬</label>
                        <input type="number" name="sortNo" class="form-control" value="${menu.sortNo}"/>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label">사용</label>
                        <select name="useYn" class="form-select">
                            <option value="Y" <c:if test="${menu.useYn eq 'Y'}">selected</c:if>>활성</option>
                            <option value="N" <c:if test="${menu.useYn eq 'N'}">selected</c:if>>중지</option>
                        </select>
                    </div>
                </div>
                <div class="mb-2"><label class="form-label">아이콘 (bi-...)</label>
                    <input type="text" name="icon" class="form-control" value="${menu.icon}" placeholder="house"/></div>
                <div class="mb-3">
                    <label class="form-label">접근 가능 역할</label>
                    <c:forEach var="r" items="${availableRoles}">
                        <div class="form-check form-check-inline">
                            <input type="checkbox" name="roles" value="${r}" class="form-check-input" id="r-${r}"
                                   <c:if test="${menu != null && menu.roles != null && menu.roles.contains(r)}">checked</c:if>/>
                            <label class="form-check-label small" for="r-${r}">${r}</label>
                        </div>
                    </c:forEach>
                </div>
                <button class="btn btn-primary"><i class="bi bi-save"></i> 저장</button>
                <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/sys/menu.do">취소</a>
            </form>
        </div>
    </div>
</div>
