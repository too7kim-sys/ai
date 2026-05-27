<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>${user.name} 프로필</title>
<div class="d-flex justify-content-between align-items-center mb-3">
    <h2><i class="bi bi-person-vcard"></i> ${user.name} 프로필</h2>
    <div>
        <sec:authorize access="hasAnyRole('ADMIN','HR_MANAGER')">
            <a class="btn btn-outline-primary btn-sm"
               href="${pageContext.request.contextPath}/sys/user/edit.do?userId=${user.userId}">
                <i class="bi bi-pencil"></i> 정보 수정
            </a>
        </sec:authorize>
        <a class="btn btn-outline-secondary btn-sm" href="${pageContext.request.contextPath}/user/list.do">
            <i class="bi bi-arrow-left"></i> 목록
        </a>
    </div>
</div>

<div class="row g-3">
    <div class="col-md-4">
        <div class="card">
            <div class="card-body text-center">
                <div class="display-3 text-primary"><i class="bi bi-person-circle"></i></div>
                <h4 class="mt-2 mb-0">${user.name}</h4>
                <div class="text-muted">${user.positionNm} · ${user.deptNm}</div>
                <span class="badge bg-secondary mt-2">${user.roleNm}</span>
            </div>
            <ul class="list-group list-group-flush small">
                <li class="list-group-item d-flex justify-content-between">
                    <span class="text-muted">이메일</span><span>${user.email}</span></li>
                <li class="list-group-item d-flex justify-content-between">
                    <span class="text-muted">연락처</span><span>${user.phone}</span></li>
                <li class="list-group-item d-flex justify-content-between">
                    <span class="text-muted">입사일</span><span>${user.hireDate}</span></li>
                <c:if test="${canViewSensitive}">
                <li class="list-group-item d-flex justify-content-between">
                    <span class="text-muted">은행</span><span><c:out value="${user.bankCd}"/> <c:out value="${user.bankAccount}"/></span></li>
                <li class="list-group-item d-flex justify-content-between">
                    <span class="text-muted">최근 로그인</span><span>${user.lastLoginAt}</span></li>
                </c:if>
            </ul>
        </div>
    </div>

    <div class="col-md-8">
        <c:if test="${!canViewSensitive}">
        <div class="card">
            <div class="card-body text-muted text-center py-5">
                <i class="bi bi-shield-lock fs-3 d-block mb-2"></i>
                인사이력·인사기록·부양가족 정보는 본인 또는 인사 담당자만 열람할 수 있습니다.
            </div>
        </div>
        </c:if>
        <c:if test="${canViewSensitive}">
        <div class="card mb-3">
            <div class="card-header d-flex justify-content-between align-items-center">
                <span><i class="bi bi-clock-history"></i> 인사이력</span>
                <sec:authorize access="hasAnyRole('ADMIN','HR_MANAGER')">
                    <a class="btn btn-sm btn-outline-primary"
                       href="${pageContext.request.contextPath}/hr/history.do?userId=${user.userId}">전체 보기</a>
                </sec:authorize>
            </div>
            <table class="table table-sm mb-0">
                <thead class="table-light">
                <tr><th>발효일</th><th>유형</th><th>변경 내역</th></tr>
                </thead>
                <tbody>
                <c:forEach var="h" items="${histories}" end="9">
                    <tr>
                        <td>${h.effectiveDt}</td>
                        <td><span class="badge bg-info">${h.changeTypeNm != null ? h.changeTypeNm : h.changeTypeCd}</span></td>
                        <td class="small text-muted">${h.beforeText} <i class="bi bi-arrow-right"></i> <span class="text-success">${h.afterText}</span></td>
                    </tr>
                </c:forEach>
                <c:if test="${empty histories}">
                    <tr><td colspan="3" class="text-center text-muted py-3">이력 없음</td></tr>
                </c:if>
                </tbody>
            </table>
        </div>

        <div class="card mb-3">
            <div class="card-header d-flex justify-content-between align-items-center">
                <span><i class="bi bi-journal-text"></i> 인사기록</span>
                <sec:authorize access="hasAnyRole('ADMIN','HR_MANAGER')">
                    <a class="btn btn-sm btn-outline-primary"
                       href="${pageContext.request.contextPath}/hr/record.do?userId=${user.userId}">관리</a>
                </sec:authorize>
            </div>
            <table class="table table-sm mb-0">
                <thead class="table-light"><tr><th>일자</th><th>분류</th><th>제목</th></tr></thead>
                <tbody>
                <c:forEach var="r" items="${records}" end="9">
                    <tr>
                        <td>${r.eventDt}</td>
                        <td><span class="badge bg-light text-dark">${r.categoryNm != null ? r.categoryNm : r.categoryCd}</span></td>
                        <td>${r.title}</td>
                    </tr>
                </c:forEach>
                <c:if test="${empty records}">
                    <tr><td colspan="3" class="text-center text-muted py-3">기록 없음</td></tr>
                </c:if>
                </tbody>
            </table>
        </div>

        <div class="card mb-3">
            <div class="card-header"><i class="bi bi-people-fill"></i> 부양가족</div>
            <table class="table table-sm mb-0">
                <thead class="table-light"><tr><th>관계</th><th>이름</th><th>생년월일</th><th>부양</th><th>경로/장애</th></tr></thead>
                <tbody>
                <c:forEach var="f" items="${families}">
                    <tr>
                        <td>${f.relationCd}</td>
                        <td>${f.name}</td>
                        <td>${f.birthDt}</td>
                        <td><c:choose><c:when test="${f.dependentYn eq 'Y'}"><span class="badge bg-success">Y</span></c:when><c:otherwise>N</c:otherwise></c:choose></td>
                        <td>
                            <c:if test="${f.elderlyYn eq 'Y'}"><span class="badge bg-info">경로</span></c:if>
                            <c:if test="${f.disabledYn eq 'Y'}"><span class="badge bg-warning text-dark">장애</span></c:if>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty families}">
                    <tr><td colspan="5" class="text-center text-muted py-3">등록 없음</td></tr>
                </c:if>
                </tbody>
            </table>
        </div>

        <%-- ─────────── 경력 ─────────── --%>
        <div class="card mb-3">
            <div class="card-header"><i class="bi bi-briefcase"></i> 경력 (이전 회사)</div>
            <table class="table table-sm mb-0 align-middle">
                <thead class="table-light"><tr><th>회사</th><th>직책</th><th style="width:200px">기간</th><th>업무</th><c:if test="${canManage}"><th style="width:60px"></th></c:if></tr></thead>
                <tbody>
                <c:forEach var="c" items="${careers}">
                    <tr>
                        <td><strong>${c.companyNm}</strong></td>
                        <td>${c.positionNm}</td>
                        <td class="small">${c.startDt} ~ <c:choose><c:when test="${empty c.endDt}"><span class="text-success">재직</span></c:when><c:otherwise>${c.endDt}</c:otherwise></c:choose></td>
                        <td class="small text-muted"><c:out value="${c.description}"/></td>
                        <c:if test="${canManage}">
                        <td>
                            <form method="post" action="${pageContext.request.contextPath}/hr/career/delete.do" onsubmit="return confirm('삭제할까요?');">
                                <sec:csrfInput/>
                                <input type="hidden" name="careerId" value="${c.careerId}"/>
                                <input type="hidden" name="userId" value="${user.userId}"/>
                                <button class="btn btn-sm btn-outline-danger"><i class="bi bi-x"></i></button>
                            </form>
                        </td>
                        </c:if>
                    </tr>
                </c:forEach>
                <c:if test="${empty careers}">
                    <tr><td colspan="${canManage ? 5 : 4}" class="text-center text-muted py-3">등록된 경력이 없습니다.</td></tr>
                </c:if>
                </tbody>
            </table>
            <c:if test="${canManage}">
            <div class="card-body border-top pt-3">
                <form method="post" action="${pageContext.request.contextPath}/hr/career.do" class="row g-2">
                    <sec:csrfInput/>
                    <input type="hidden" name="userId" value="${user.userId}"/>
                    <div class="col-md-3"><input type="text" name="companyNm" class="form-control form-control-sm" placeholder="회사명" required/></div>
                    <div class="col-md-2"><input type="text" name="positionNm" class="form-control form-control-sm" placeholder="직책"/></div>
                    <div class="col-md-2"><input type="date" name="startDt" class="form-control form-control-sm" required/></div>
                    <div class="col-md-2"><input type="date" name="endDt" class="form-control form-control-sm" placeholder="종료(공란=재직)"/></div>
                    <div class="col-md-2"><input type="text" name="description" class="form-control form-control-sm" placeholder="담당 업무"/></div>
                    <div class="col-md-1"><button class="btn btn-primary btn-sm w-100"><i class="bi bi-plus"></i></button></div>
                </form>
            </div>
            </c:if>
        </div>

        <%-- ─────────── 학력 ─────────── --%>
        <div class="card mb-3">
            <div class="card-header"><i class="bi bi-mortarboard"></i> 학력</div>
            <table class="table table-sm mb-0 align-middle">
                <thead class="table-light"><tr><th>학교</th><th>전공</th><th>학위</th><th>상태</th><th>기간</th><c:if test="${canManage}"><th style="width:60px"></th></c:if></tr></thead>
                <tbody>
                <c:forEach var="e" items="${educations}">
                    <tr>
                        <td><strong>${e.schoolNm}</strong></td>
                        <td>${e.major}</td>
                        <td><c:out value="${e.degreeNm}"/></td>
                        <td>
                            <c:choose>
                                <c:when test="${e.eduStatusCd eq 'GRADUATED'}"><span class="badge bg-success">${e.eduStatusNm}</span></c:when>
                                <c:when test="${e.eduStatusCd eq 'ENROLLED'}"><span class="badge bg-info">${e.eduStatusNm}</span></c:when>
                                <c:when test="${e.eduStatusCd eq 'DROPPED'}"><span class="badge bg-secondary">${e.eduStatusNm}</span></c:when>
                                <c:otherwise>${e.eduStatusNm}</c:otherwise>
                            </c:choose>
                        </td>
                        <td class="small">${e.admissionDt} ~ ${e.graduationDt}</td>
                        <c:if test="${canManage}">
                        <td>
                            <form method="post" action="${pageContext.request.contextPath}/hr/education/delete.do" onsubmit="return confirm('삭제할까요?');">
                                <sec:csrfInput/>
                                <input type="hidden" name="eduId" value="${e.eduId}"/>
                                <input type="hidden" name="userId" value="${user.userId}"/>
                                <button class="btn btn-sm btn-outline-danger"><i class="bi bi-x"></i></button>
                            </form>
                        </td>
                        </c:if>
                    </tr>
                </c:forEach>
                <c:if test="${empty educations}">
                    <tr><td colspan="${canManage ? 6 : 5}" class="text-center text-muted py-3">등록된 학력이 없습니다.</td></tr>
                </c:if>
                </tbody>
            </table>
            <c:if test="${canManage}">
            <div class="card-body border-top pt-3">
                <form method="post" action="${pageContext.request.contextPath}/hr/education.do" class="row g-2">
                    <sec:csrfInput/>
                    <input type="hidden" name="userId" value="${user.userId}"/>
                    <div class="col-md-3"><input type="text" name="schoolNm" class="form-control form-control-sm" placeholder="학교명" required/></div>
                    <div class="col-md-2"><input type="text" name="major" class="form-control form-control-sm" placeholder="전공"/></div>
                    <div class="col-md-2">
                        <select name="degreeCd" class="form-select form-select-sm">
                            <option value="">- 학위 -</option>
                            <c:forEach var="d" items="${degreeCodes}"><option value="${d.codeVal}">${d.codeNm}</option></c:forEach>
                        </select>
                    </div>
                    <div class="col-md-1">
                        <select name="eduStatusCd" class="form-select form-select-sm" required>
                            <c:forEach var="s" items="${eduStatusCodes}"><option value="${s.codeVal}">${s.codeNm}</option></c:forEach>
                        </select>
                    </div>
                    <div class="col-md-2"><input type="date" name="admissionDt" class="form-control form-control-sm"/></div>
                    <div class="col-md-1"><input type="date" name="graduationDt" class="form-control form-control-sm"/></div>
                    <div class="col-md-1"><button class="btn btn-primary btn-sm w-100"><i class="bi bi-plus"></i></button></div>
                </form>
            </div>
            </c:if>
        </div>

        <%-- ─────────── 교육이수 ─────────── --%>
        <div class="card mb-3">
            <div class="card-header"><i class="bi bi-journal-bookmark"></i> 교육이수</div>
            <table class="table table-sm mb-0 align-middle">
                <thead class="table-light"><tr><th>과정</th><th>주관</th><th>기간</th><th>시간</th><th>수료번호</th><c:if test="${canManage}"><th style="width:60px"></th></c:if></tr></thead>
                <tbody>
                <c:forEach var="t" items="${trainings}">
                    <tr>
                        <td><strong>${t.courseNm}</strong></td>
                        <td>${t.providerNm}</td>
                        <td class="small">${t.startDt} ~ ${t.endDt}</td>
                        <td><c:if test="${not empty t.hours}">${t.hours}h</c:if></td>
                        <td class="small text-muted"><c:out value="${t.certNo}"/></td>
                        <c:if test="${canManage}">
                        <td>
                            <form method="post" action="${pageContext.request.contextPath}/hr/training/delete.do" onsubmit="return confirm('삭제할까요?');">
                                <sec:csrfInput/>
                                <input type="hidden" name="trnId" value="${t.trnId}"/>
                                <input type="hidden" name="userId" value="${user.userId}"/>
                                <button class="btn btn-sm btn-outline-danger"><i class="bi bi-x"></i></button>
                            </form>
                        </td>
                        </c:if>
                    </tr>
                </c:forEach>
                <c:if test="${empty trainings}">
                    <tr><td colspan="${canManage ? 6 : 5}" class="text-center text-muted py-3">교육이수 내역이 없습니다.</td></tr>
                </c:if>
                </tbody>
            </table>
            <c:if test="${canManage}">
            <div class="card-body border-top pt-3">
                <form method="post" action="${pageContext.request.contextPath}/hr/training.do" class="row g-2">
                    <sec:csrfInput/>
                    <input type="hidden" name="userId" value="${user.userId}"/>
                    <div class="col-md-3"><input type="text" name="courseNm" class="form-control form-control-sm" placeholder="과정명" required/></div>
                    <div class="col-md-2"><input type="text" name="providerNm" class="form-control form-control-sm" placeholder="주관 기관"/></div>
                    <div class="col-md-2"><input type="date" name="startDt" class="form-control form-control-sm"/></div>
                    <div class="col-md-2"><input type="date" name="endDt" class="form-control form-control-sm"/></div>
                    <div class="col-md-1"><input type="number" step="0.5" min="0" name="hours" class="form-control form-control-sm" placeholder="시간"/></div>
                    <div class="col-md-1"><input type="text" name="certNo" class="form-control form-control-sm" placeholder="수료번호"/></div>
                    <div class="col-md-1"><button class="btn btn-primary btn-sm w-100"><i class="bi bi-plus"></i></button></div>
                </form>
            </div>
            </c:if>
        </div>

        <%-- ─────────── 상벌 ─────────── --%>
        <div class="card">
            <div class="card-header"><i class="bi bi-award"></i> 상벌</div>
            <table class="table table-sm mb-0 align-middle">
                <thead class="table-light"><tr><th>구분</th><th>일자</th><th>제목</th><th>수여기관</th><th>사유</th><c:if test="${canManage}"><th style="width:60px"></th></c:if></tr></thead>
                <tbody>
                <c:forEach var="a" items="${awards}">
                    <tr>
                        <td>
                            <c:choose>
                                <c:when test="${a.awardTypeCd eq 'AWARD'}"><span class="badge bg-success"><i class="bi bi-trophy"></i> ${a.awardTypeNm}</span></c:when>
                                <c:otherwise><span class="badge bg-danger"><i class="bi bi-exclamation-triangle"></i> ${a.awardTypeNm}</span></c:otherwise>
                            </c:choose>
                        </td>
                        <td class="small">${a.occurredOn}</td>
                        <td><strong>${a.title}</strong></td>
                        <td>${a.organization}</td>
                        <td class="small text-muted"><c:out value="${a.reason}"/></td>
                        <c:if test="${canManage}">
                        <td>
                            <form method="post" action="${pageContext.request.contextPath}/hr/award/delete.do" onsubmit="return confirm('삭제할까요?');">
                                <sec:csrfInput/>
                                <input type="hidden" name="awardId" value="${a.awardId}"/>
                                <input type="hidden" name="userId" value="${user.userId}"/>
                                <button class="btn btn-sm btn-outline-danger"><i class="bi bi-x"></i></button>
                            </form>
                        </td>
                        </c:if>
                    </tr>
                </c:forEach>
                <c:if test="${empty awards}">
                    <tr><td colspan="${canManage ? 6 : 5}" class="text-center text-muted py-3">등록된 상벌이 없습니다.</td></tr>
                </c:if>
                </tbody>
            </table>
            <c:if test="${canManage}">
            <div class="card-body border-top pt-3">
                <form method="post" action="${pageContext.request.contextPath}/hr/award.do" class="row g-2">
                    <sec:csrfInput/>
                    <input type="hidden" name="userId" value="${user.userId}"/>
                    <div class="col-md-2">
                        <select name="awardTypeCd" class="form-select form-select-sm" required>
                            <c:forEach var="t" items="${awardTypeCodes}"><option value="${t.codeVal}">${t.codeNm}</option></c:forEach>
                        </select>
                    </div>
                    <div class="col-md-2"><input type="date" name="occurredOn" class="form-control form-control-sm" required/></div>
                    <div class="col-md-3"><input type="text" name="title" class="form-control form-control-sm" placeholder="제목" required/></div>
                    <div class="col-md-2"><input type="text" name="organization" class="form-control form-control-sm" placeholder="수여 기관"/></div>
                    <div class="col-md-2"><input type="text" name="reason" class="form-control form-control-sm" placeholder="사유"/></div>
                    <div class="col-md-1"><button class="btn btn-primary btn-sm w-100"><i class="bi bi-plus"></i></button></div>
                </form>
            </div>
            </c:if>
        </div>
        </c:if>
    </div>
</div>
