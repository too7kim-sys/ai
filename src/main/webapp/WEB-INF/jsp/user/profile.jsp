<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>${user.name} 프로필</title>
<div class="d-flex justify-content-between align-items-center mb-3">
    <h2><i class="bi bi-person-vcard"></i> ${user.name} 프로필</h2>
    <div>
        <c:if test="${isOwn}">
            <a class="btn btn-primary btn-sm"
               href="${pageContext.request.contextPath}/user/me.do">
                <i class="bi bi-person-gear"></i> 내 정보 수정
            </a>
        </c:if>
        <sec:authorize access="hasAnyRole('ADMIN','HR_MANAGER')">
            <a class="btn btn-outline-primary btn-sm"
               href="${pageContext.request.contextPath}/sys/user/edit.do?userId=${user.userId}">
                <i class="bi bi-pencil"></i> HR 정보 수정
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
            <div class="empty-state py-5">
                <i class="bi bi-shield-lock empty-state-icon"></i>
                <div class="empty-state-title">민감 정보 보호 영역</div>
                <div class="empty-state-desc small text-muted">
                    인사이력·인사기록·부양가족·경력·학력·교육이수·상벌 정보는<br>
                    본인 또는 인사 담당자(HR/ADMIN) 만 열람할 수 있습니다.
                </div>
            </div>
        </div>
        </c:if>
        <c:if test="${canViewSensitive}">
        <div class="card mb-3">
            <div class="card-header d-flex justify-content-between align-items-center">
                <span><i class="bi bi-clock-history"></i> 인사이력
                    <small class="text-muted ms-1">총 ${empty histories ? 0 : histories.size()}건</small></span>
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
                    <tr><td colspan="3" class="empty-state">
                        <i class="bi bi-clock-history empty-state-icon"></i>
                        <div class="empty-state-title">아직 인사이력이 없습니다</div>
                    </td></tr>
                </c:if>
                </tbody>
            </table>
        </div>

        <div class="card mb-3">
            <div class="card-header d-flex justify-content-between align-items-center">
                <span><i class="bi bi-journal-text"></i> 인사기록
                    <small class="text-muted ms-1">총 ${empty records ? 0 : records.size()}건</small></span>
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
                    <tr><td colspan="3" class="empty-state">
                        <i class="bi bi-journal-text empty-state-icon"></i>
                        <div class="empty-state-title">아직 인사기록이 없습니다</div>
                    </td></tr>
                </c:if>
                </tbody>
            </table>
        </div>

        <div class="card mb-3">
            <div class="card-header d-flex justify-content-between align-items-center">
                <span><i class="bi bi-people-fill"></i> 부양가족
                    <small class="text-muted ms-1">총 ${empty families ? 0 : families.size()}건</small></span>
            </div>
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
                    <tr><td colspan="5" class="empty-state">
                        <i class="bi bi-people-fill empty-state-icon"></i>
                        <div class="empty-state-title">등록된 부양가족이 없습니다</div>
                    </td></tr>
                </c:if>
                </tbody>
            </table>
        </div>

        <%-- ─────────── 경력 ─────────── --%>
        <div class="card mb-3">
            <div class="card-header d-flex justify-content-between align-items-center">
                <span><i class="bi bi-briefcase"></i> 경력 <small class="text-muted">(이전 회사)</small>
                    <small class="text-muted ms-1">총 ${empty careers ? 0 : careers.size()}건</small></span>
            </div>
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
                    <tr><td colspan="${canManage ? 5 : 4}" class="empty-state">
                        <i class="bi bi-briefcase empty-state-icon"></i>
                        <div class="empty-state-title">등록된 경력이 없습니다</div>
                    </td></tr>
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
            <div class="card-header d-flex justify-content-between align-items-center">
                <span><i class="bi bi-mortarboard"></i> 학력
                    <small class="text-muted ms-1">총 ${empty educations ? 0 : educations.size()}건</small></span>
            </div>
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
                    <tr><td colspan="${canManage ? 6 : 5}" class="empty-state">
                        <i class="bi bi-mortarboard empty-state-icon"></i>
                        <div class="empty-state-title">등록된 학력이 없습니다</div>
                    </td></tr>
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
            <div class="card-header d-flex justify-content-between align-items-center">
                <span><i class="bi bi-journal-bookmark"></i> 교육이수
                    <small class="text-muted ms-1">총 ${empty trainings ? 0 : trainings.size()}건</small></span>
            </div>
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
                    <tr><td colspan="${canManage ? 6 : 5}" class="empty-state">
                        <i class="bi bi-journal-bookmark empty-state-icon"></i>
                        <div class="empty-state-title">교육이수 내역이 없습니다</div>
                    </td></tr>
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

        <%-- ─────────── 프로젝트 수행 경력 (KOSA 표준 + 코사증빙 첨부) ─────────── --%>
        <div class="card mb-3">
            <div class="card-header d-flex justify-content-between align-items-center">
                <span><i class="bi bi-diagram-3"></i> 프로젝트 수행 경력
                    <small class="text-muted ms-1">KOSA 표준</small></span>
                <span class="small text-muted">총 ${empty projects ? 0 : projects.size()}건</span>
            </div>
            <ul class="list-group list-group-flush">
                <c:forEach var="p" items="${projects}">
                    <li class="list-group-item">
                        <div class="d-flex justify-content-between gap-2">
                            <div class="flex-grow-1">
                                <%-- 1줄: 제목 + KOSA 뱃지 --%>
                                <div class="d-flex align-items-center flex-wrap gap-1">
                                    <strong class="me-1">${p.projectNm}</strong>
                                    <c:if test="${not empty p.kosaGradeNm}">
                                        <span class="badge bg-info">${p.kosaGradeNm}</span>
                                    </c:if>
                                    <c:if test="${p.kosaConfirmedYn eq 'Y'}">
                                        <span class="badge bg-success"><i class="bi bi-check2-circle"></i> KOSA 신고완료</span>
                                    </c:if>
                                    <c:if test="${empty p.endDt}">
                                        <span class="badge bg-primary">진행중</span>
                                    </c:if>
                                </div>
                                <%-- 2줄: 기간 · 발주처/수행사/역할 --%>
                                <div class="small text-muted mt-1">
                                    <i class="bi bi-calendar-range"></i>
                                    ${p.startDt} ~ <c:choose><c:when test="${empty p.endDt}">현재</c:when><c:otherwise>${p.endDt}</c:otherwise></c:choose>
                                    <c:if test="${not empty p.clientNm}">
                                        <span class="mx-1">·</span><i class="bi bi-building"></i>
                                        발주처 <span class="text-dark">${p.clientNm}</span>
                                    </c:if>
                                    <c:if test="${not empty p.contractorNm}">
                                        <span class="mx-1">·</span>수행사 ${p.contractorNm}
                                    </c:if>
                                    <c:if test="${not empty p.roleNm}">
                                        <span class="mx-1">·</span><i class="bi bi-person-badge"></i>
                                        <span class="text-dark">${p.roleNm}</span>
                                    </c:if>
                                </div>
                                <%-- 3줄: 기술 스택을 칩으로 --%>
                                <c:if test="${not empty p.techStack}">
                                    <div class="mt-2 d-flex flex-wrap gap-1">
                                        <c:forEach var="tech" items="${fn:split(p.techStack, ',')}">
                                            <c:set var="techTrim" value="${fn:trim(tech)}"/>
                                            <c:if test="${not empty techTrim}">
                                                <span class="badge bg-light text-dark border">${techTrim}</span>
                                            </c:if>
                                        </c:forEach>
                                    </div>
                                </c:if>
                                <%-- 4줄: 담당 업무 상세 --%>
                                <c:if test="${not empty p.description}">
                                    <div class="small text-muted mt-2" style="white-space:pre-wrap"><c:out value="${p.description}"/></div>
                                </c:if>
                                <%-- 5줄: 첨부 (코사증빙 등) --%>
                                <c:if test="${not empty p.attachments}">
                                    <div class="mt-2 d-flex flex-wrap gap-2">
                                        <c:forEach var="a" items="${p.attachments}">
                                            <a class="badge bg-light text-dark border text-decoration-none"
                                               href="${pageContext.request.contextPath}/hr/project/attach/download.do?attachId=${a.attachId}"
                                               title="다운로드">
                                                <i class="bi bi-paperclip"></i>
                                                <c:out value="${a.fileNm}"/>
                                            </a>
                                        </c:forEach>
                                    </div>
                                </c:if>
                            </div>
                            <c:if test="${canManage}">
                                <form method="post" action="${pageContext.request.contextPath}/hr/project/delete.do"
                                      onsubmit="return confirm('이 프로젝트를 삭제할까요? 첨부 파일은 유지됩니다.');">
                                    <sec:csrfInput/>
                                    <input type="hidden" name="projectId" value="${p.projectId}"/>
                                    <input type="hidden" name="userId" value="${user.userId}"/>
                                    <button class="btn btn-sm btn-outline-danger" title="삭제"><i class="bi bi-x"></i></button>
                                </form>
                            </c:if>
                        </div>
                    </li>
                </c:forEach>
                <c:if test="${empty projects}">
                    <li class="list-group-item empty-state">
                        <i class="bi bi-diagram-3 empty-state-icon"></i>
                        <div class="empty-state-title">등록된 프로젝트 수행 경력이 없습니다</div>
                        <c:if test="${canManage}">
                            <div class="empty-state-desc small text-muted">아래 폼에서 추가하고 KOSA 경력증명서 등 증빙 파일을 함께 업로드하세요.</div>
                        </c:if>
                    </li>
                </c:if>
            </ul>
            <c:if test="${canManage}">
            <div class="card-body border-top pt-3">
                <form method="post" enctype="multipart/form-data"
                      action="${pageContext.request.contextPath}/hr/project.do" class="row g-3">
                    <sec:csrfInput/>
                    <input type="hidden" name="userId" value="${user.userId}"/>

                    <%-- 기본 정보 --%>
                    <div class="col-md-5">
                        <label class="form-label small">프로젝트명 <span class="text-danger">*</span></label>
                        <input type="text" name="projectNm" class="form-control form-control-sm"
                               placeholder="예: 차세대 그룹웨어 구축" required/>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label small">발주처</label>
                        <input type="text" name="clientNm" class="form-control form-control-sm" placeholder="고객사"/>
                    </div>
                    <div class="col-md-2">
                        <label class="form-label small">수행사</label>
                        <input type="text" name="contractorNm" class="form-control form-control-sm" placeholder="소속/외주"/>
                    </div>
                    <div class="col-md-2">
                        <label class="form-label small">역할</label>
                        <input type="text" name="roleNm" class="form-control form-control-sm" placeholder="PL/PM/개발"/>
                    </div>

                    <%-- 기간 + KOSA --%>
                    <div class="col-md-2">
                        <label class="form-label small">시작일</label>
                        <input type="date" name="startDt" class="form-control form-control-sm"/>
                    </div>
                    <div class="col-md-2">
                        <label class="form-label small">종료일 <span class="text-muted">(공란=진행중)</span></label>
                        <input type="date" name="endDt" class="form-control form-control-sm"/>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label small">KOSA 등급</label>
                        <select name="kosaGradeCd" class="form-select form-select-sm">
                            <option value="">- 미지정 -</option>
                            <c:forEach var="g" items="${kosaGradeCodes}"><option value="${g.codeVal}">${g.codeNm}</option></c:forEach>
                        </select>
                    </div>
                    <div class="col-md-2 d-flex align-items-end">
                        <div class="form-check form-switch">
                            <input class="form-check-input" type="checkbox" id="kosaConfirmedChk" name="kosaConfirmedYn" value="Y"/>
                            <label class="form-check-label small" for="kosaConfirmedChk">KOSA 신고완료</label>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label small">코사증빙 첨부 <small class="text-muted">(여러 개 선택 가능)</small></label>
                        <input type="file" name="files" multiple class="form-control form-control-sm"/>
                    </div>

                    <%-- 상세 --%>
                    <div class="col-md-12">
                        <label class="form-label small">사용 기술 <span class="text-muted">(콤마 구분)</span></label>
                        <input type="text" name="techStack" class="form-control form-control-sm"
                               placeholder="예: Java, Spring Boot, Oracle, AWS, Docker"/>
                    </div>
                    <div class="col-md-12">
                        <label class="form-label small">담당 업무</label>
                        <textarea name="description" class="form-control form-control-sm" rows="2"
                                  placeholder="요건 분석, 모듈 설계, 핵심 알고리즘 구현 등"></textarea>
                    </div>

                    <div class="col-12 text-end">
                        <button class="btn btn-primary btn-sm">
                            <i class="bi bi-plus-lg"></i> 프로젝트 추가
                        </button>
                    </div>
                </form>
            </div>
            </c:if>
        </div>

        <%-- ─────────── 상벌 ─────────── --%>
        <div class="card">
            <div class="card-header d-flex justify-content-between align-items-center">
                <span><i class="bi bi-award"></i> 상벌
                    <small class="text-muted ms-1">총 ${empty awards ? 0 : awards.size()}건</small></span>
            </div>
            <table class="table table-sm mb-0 align-middle">
                <thead class="table-light"><tr><th>구분</th><th>일자</th><th>제목</th><th>수여기관</th><th>사유</th><c:if test="${canEditAward}"><th style="width:60px"></th></c:if></tr></thead>
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
                        <c:if test="${canEditAward}">
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
                    <tr><td colspan="${canEditAward ? 6 : 5}" class="empty-state">
                        <i class="bi bi-award empty-state-icon"></i>
                        <div class="empty-state-title">등록된 상벌이 없습니다</div>
                    </td></tr>
                </c:if>
                </tbody>
            </table>
            <c:if test="${canEditAward}">
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
