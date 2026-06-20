<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<title>내 휴가</title>

<div class="d-flex justify-content-between align-items-center mb-3">
    <h2 class="mb-0"><i class="bi bi-airplane text-info"></i> 내 휴가</h2>
    <a class="btn btn-primary" href="${pageContext.request.contextPath}/leave/write.do">
        <i class="bi bi-plus-lg"></i> 휴가 신청
    </a>
</div>

<%-- 잔여 / 사용 / 부여 요약 카드 --%>
<div class="row g-3 mb-3">
    <div class="col-6 col-md-4">
        <div class="card h-100 stat-card">
            <div class="card-body">
                <div class="widget-label">연차 잔여</div>
                <div class="widget-value text-primary">${balance.remaining()}<small class="text-muted fs-6 ms-1">일</small></div>
                <div class="widget-trend text-muted">올해 사용 가능</div>
            </div>
        </div>
    </div>
    <div class="col-6 col-md-4">
        <div class="card h-100 stat-card">
            <div class="card-body">
                <div class="widget-label">올해 사용</div>
                <div class="widget-value text-warning">${balance.annualUsed}<small class="text-muted fs-6 ms-1">일</small></div>
                <div class="widget-trend text-muted">결재 완료 차감분</div>
            </div>
        </div>
    </div>
    <div class="col-12 col-md-4">
        <div class="card h-100 stat-card">
            <div class="card-body">
                <div class="widget-label">올해 부여</div>
                <div class="widget-value">${balance.annualGiven}<small class="text-muted fs-6 ms-1">일</small></div>
                <div class="widget-trend text-muted">HR이 부여한 연간 한도</div>
            </div>
        </div>
    </div>
</div>

<div class="card">
    <div class="card-header d-flex justify-content-between align-items-center">
        <span><i class="bi bi-list-ul"></i> 신청 내역
            <small class="text-muted ms-1">총 ${empty list ? 0 : list.size()}건</small></span>
    </div>
    <div class="table-responsive">
    <table class="table table-hover mb-0 align-middle">
        <thead class="table-light">
            <tr>
                <th style="width:90px">유형</th>
                <th>시작</th>
                <th>종료</th>
                <th class="text-end" style="width:80px">일수</th>
                <th>사유</th>
                <th style="width:90px">상태</th>
                <th style="width:110px"></th>
            </tr>
        </thead>
        <tbody>
        <c:forEach var="r" items="${list}">
            <tr>
                <td>
                    <c:choose>
                        <c:when test="${r.leaveTypeCd == 'ANNUAL'}"><span class="badge text-bg-light border">연차</span></c:when>
                        <c:when test="${r.leaveTypeCd == 'HALF'}"><span class="badge text-bg-light border">반차</span></c:when>
                        <c:when test="${r.leaveTypeCd == 'HOURLY'}"><span class="badge bg-info">시간연차</span></c:when>
                        <c:when test="${r.leaveTypeCd == 'SICK'}"><span class="badge text-bg-light border">병가</span></c:when>
                        <c:when test="${r.leaveTypeCd == 'FAMILY'}"><span class="badge text-bg-light border">경조사</span></c:when>
                        <c:when test="${r.leaveTypeCd == 'OTHER'}"><span class="badge text-bg-light border">기타</span></c:when>
                        <c:otherwise><span class="badge bg-secondary">${r.leaveTypeCd}</span></c:otherwise>
                    </c:choose>
                </td>
                <c:choose>
                    <c:when test="${r.leaveTypeCd == 'HOURLY' and r.startAt != null}">
                        <td>${r.startDt}<div class="text-muted small">${r.startAt.toLocalTime()}</div></td>
                        <td>${r.endDt}<div class="text-muted small">${r.endAt.toLocalTime()}</div></td>
                    </c:when>
                    <c:otherwise>
                        <td>${r.startDt}</td><td>${r.endDt}</td>
                    </c:otherwise>
                </c:choose>
                <td class="text-end"><strong>${r.days}</strong></td>
                <td class="small text-muted"><c:out value="${r.reason}"/></td>
                <td>
                    <c:choose>
                        <c:when test="${r.statusCd == 'IN_PROGRESS'}"><span class="badge bg-warning text-dark">결재중</span></c:when>
                        <c:when test="${r.statusCd == 'APPROVED'}"><span class="badge bg-success">승인</span></c:when>
                        <c:when test="${r.statusCd == 'REJECTED'}"><span class="badge bg-danger">반려</span></c:when>
                        <c:when test="${r.statusCd == 'CANCELED'}"><span class="badge bg-dark">취소</span></c:when>
                        <c:otherwise><span class="badge bg-secondary">${r.statusCd}</span></c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <c:if test="${r.approvalDocId != null}">
                        <a class="btn btn-sm btn-outline-secondary"
                           href="${pageContext.request.contextPath}/approval/detail.do?docId=${r.approvalDocId}">
                            <i class="bi bi-file-earmark-check"></i> 결재
                        </a>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty list}">
            <tr>
                <td colspan="7" class="empty-state">
                    <i class="bi bi-airplane empty-state-icon"></i>
                    <div class="empty-state-title">신청 내역이 없습니다</div>
                    <div class="empty-state-desc small text-muted">상단의 '휴가 신청' 으로 첫 휴가를 신청해 보세요.</div>
                </td>
            </tr>
        </c:if>
        </tbody>
    </table>
    </div>
</div>
