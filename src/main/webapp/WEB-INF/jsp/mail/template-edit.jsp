<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>메일 템플릿 - ${vo.templateNm}</title>
<h2 class="mb-3"><i class="bi bi-pencil"></i> 메일 템플릿 편집</h2>

<form method="post" action="${pageContext.request.contextPath}/mail/template/edit.do">
    <sec:csrfInput/>
    <input type="hidden" name="templateCd" value="${vo.templateCd}"/>
    <div class="card">
        <div class="card-body">
            <div class="row g-2 mb-3">
                <div class="col-md-3">
                    <label class="form-label">코드</label>
                    <input type="text" class="form-control" value="${vo.templateCd}" disabled/>
                </div>
                <div class="col-md-6">
                    <label class="form-label">이름</label>
                    <input type="text" name="templateNm" class="form-control" value="${vo.templateNm}" required/>
                </div>
                <div class="col-md-3">
                    <label class="form-label">사용 여부</label>
                    <select name="useYn" class="form-select">
                        <option value="Y" <c:if test="${vo.useYn eq 'Y'}">selected</c:if>>사용</option>
                        <option value="N" <c:if test="${vo.useYn eq 'N'}">selected</c:if>>중지</option>
                    </select>
                </div>
            </div>
            <div class="mb-3">
                <label class="form-label">제목</label>
                <input type="text" name="subject" class="form-control" value="${vo.subject}" required maxlength="200"/>
            </div>
            <div class="mb-3">
                <label class="form-label">본문 HTML <small class="text-muted">— 변수는 <code>{{변수명}}</code> 형식</small></label>
                <textarea name="bodyHtml" class="form-control font-monospace" rows="14" required>${vo.bodyHtml}</textarea>
            </div>
        </div>
        <div class="card-footer">
            <button class="btn btn-primary"><i class="bi bi-save"></i> 저장</button>
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/mail/template.do">취소</a>
        </div>
    </div>
</form>
