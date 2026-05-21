<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>자료실</title>
<div class="d-flex justify-content-between mb-3">
    <h2><i class="bi bi-folder2-open"></i> 자료실</h2>
    <div class="d-flex gap-2">
        <form method="get" action="${pageContext.request.contextPath}/doc/search.do" class="d-flex">
            <input type="text" name="keyword" class="form-control form-control-sm me-2" placeholder="전체 검색"/>
            <button class="btn btn-sm btn-outline-secondary"><i class="bi bi-search"></i></button>
        </form>
        <button class="btn btn-sm btn-primary" data-bs-toggle="modal" data-bs-target="#newFolderModal">
            <i class="bi bi-folder-plus"></i> 새 폴더
        </button>
    </div>
</div>

<div class="row g-3">
    <!-- 폴더 목록 -->
    <div class="col-md-3">
        <div class="card">
            <div class="card-header"><i class="bi bi-folder"></i> 폴더</div>
            <div class="list-group list-group-flush">
                <c:forEach var="f" items="${folders}">
                    <a class="list-group-item list-group-item-action <c:if test='${folderId == f.folderId}'>active</c:if>"
                       href="${pageContext.request.contextPath}/doc/list.do?folderId=${f.folderId}">
                        <c:choose>
                            <c:when test="${f.accessScope eq 'COMPANY'}"><i class="bi bi-building"></i></c:when>
                            <c:when test="${f.accessScope eq 'DEPT'}"><i class="bi bi-people"></i></c:when>
                            <c:otherwise><i class="bi bi-lock"></i></c:otherwise>
                        </c:choose>
                        ${f.folderNm}
                    </a>
                </c:forEach>
                <c:if test="${empty folders}">
                    <div class="list-group-item text-muted small">폴더가 없습니다.</div>
                </c:if>
            </div>
        </div>
    </div>

    <div class="col-md-9">
        <c:if test="${folder == null}">
            <div class="alert alert-info">왼쪽 목록에서 폴더를 선택하세요.</div>
        </c:if>
        <c:if test="${folder != null}">
            <div class="card">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <span>
                        <strong>${folder.folderNm}</strong>
                        <small class="text-muted">${folder.folderPath}</small>
                        <span class="badge bg-secondary ms-2">
                            <c:choose>
                                <c:when test="${folder.accessScope eq 'COMPANY'}">전사</c:when>
                                <c:when test="${folder.accessScope eq 'DEPT'}">부서</c:when>
                                <c:otherwise>개인</c:otherwise>
                            </c:choose>
                        </span>
                    </span>
                    <form method="get" class="d-flex">
                        <input type="hidden" name="folderId" value="${folder.folderId}"/>
                        <input type="text" name="keyword" value="${keyword}" class="form-control form-control-sm me-2" placeholder="파일명 검색"/>
                        <button class="btn btn-sm btn-outline-secondary">검색</button>
                    </form>
                </div>
                <table class="table mb-0">
                    <thead class="table-light">
                    <tr><th>제목</th><th>파일</th><th>크기</th><th>업로더</th><th>다운로드</th><th>등록일</th><th></th></tr>
                    </thead>
                    <tbody>
                    <c:forEach var="d" items="${files}">
                        <tr>
                            <td>${d.title}
                                <c:if test="${not empty d.description}">
                                    <div class="small text-muted">${d.description}</div>
                                </c:if>
                            </td>
                            <td class="small">${d.fileNm}</td>
                            <td class="small text-muted">
                                <c:choose>
                                    <c:when test="${d.fileSize >= 1048576}">${d.fileSize / 1048576} MB</c:when>
                                    <c:otherwise>${d.fileSize / 1024} KB</c:otherwise>
                                </c:choose>
                            </td>
                            <td>${d.ownerName}</td>
                            <td>${d.downloadCnt}</td>
                            <td class="small text-muted">${fn:substring(d.createdAt, 0, 10)}</td>
                            <td>
                                <a class="btn btn-sm btn-outline-success"
                                   href="${pageContext.request.contextPath}/doc/download.do?docId=${d.docId}">
                                    <i class="bi bi-download"></i>
                                </a>
                                <form method="post" action="${pageContext.request.contextPath}/doc/delete.do" class="d-inline"
                                      onsubmit="return confirm('삭제하시겠습니까?')">
                                    <sec:csrfInput/>
                                    <input type="hidden" name="docId" value="${d.docId}"/>
                                    <input type="hidden" name="folderId" value="${folder.folderId}"/>
                                    <button class="btn btn-sm btn-outline-danger"><i class="bi bi-trash"></i></button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty files}">
                        <tr><td colspan="7" class="text-center text-muted py-4">파일이 없습니다.</td></tr>
                    </c:if>
                    </tbody>
                </table>
                <div class="card-footer">
                    <form method="post" action="${pageContext.request.contextPath}/doc/upload.do" enctype="multipart/form-data" class="row g-2">
                        <sec:csrfInput/>
                        <input type="hidden" name="folderId" value="${folder.folderId}"/>
                        <div class="col-md-4"><input type="text" name="title" class="form-control form-control-sm" placeholder="제목 (선택)"/></div>
                        <div class="col-md-3"><input type="text" name="description" class="form-control form-control-sm" placeholder="설명 (선택)"/></div>
                        <div class="col-md-3"><input type="file" name="file" class="form-control form-control-sm" required/></div>
                        <div class="col-md-2 d-grid"><button class="btn btn-sm btn-primary"><i class="bi bi-upload"></i> 업로드</button></div>
                    </form>
                </div>
            </div>
        </c:if>
    </div>
</div>

<!-- 새 폴더 모달 -->
<div class="modal fade" id="newFolderModal" tabindex="-1">
    <div class="modal-dialog">
        <form method="post" action="${pageContext.request.contextPath}/doc/folder/create.do" class="modal-content">
            <sec:csrfInput/>
            <div class="modal-header"><h5 class="modal-title">새 폴더</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
            <div class="modal-body">
                <div class="mb-3"><label class="form-label">폴더명</label>
                    <input type="text" name="folderNm" class="form-control" required/></div>
                <div class="mb-3"><label class="form-label">상위 폴더 (선택)</label>
                    <select name="parentId" class="form-select">
                        <option value="">(루트)</option>
                        <c:forEach var="f" items="${folders}">
                            <option value="${f.folderId}">${f.folderNm}</option>
                        </c:forEach>
                    </select></div>
                <div class="mb-3"><label class="form-label">공유 범위</label>
                    <select name="accessScope" class="form-select">
                        <option value="COMPANY">전사 공유</option>
                        <option value="DEPT">부서 공유</option>
                        <option value="PRIVATE">개인 전용</option>
                    </select></div>
                <div class="mb-2"><label class="form-label">설명</label>
                    <input type="text" name="description" class="form-control"/></div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">취소</button>
                <button class="btn btn-primary">생성</button>
            </div>
        </form>
    </div>
</div>
