<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%--
  공통 사원 선택 모달. decorator.jsp 에서 include 하여 전 화면에서 사용한다.

  사용 예 (단일 선택):
    <input type="hidden" id="targetUserId" name="userId"/>
    <input type="text" id="targetUserNm" placeholder="사원 선택" readonly required/>
    <button type="button" onclick="openUserPicker({hidden:'targetUserId', display:'targetUserNm'})">선택</button>

  사용 예 (다중 선택 — multiple select 채우기):
    <select id="approverIds" name="approverIds" multiple class="d-none"></select>
    <div id="approverChips"></div>
    <button onclick="openUserPicker({multi:true, multiSelect:'approverIds', chipsContainer:'approverChips'})">결재선 선택</button>
--%>
<div class="modal fade" id="gwUserPickerModal" tabindex="-1" aria-labelledby="gwUserPickerTitle" aria-hidden="true">
    <div class="modal-dialog modal-lg modal-dialog-scrollable">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="gwUserPickerTitle"><i class="bi bi-person-search"></i> 사원 선택</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <div class="input-group input-group-sm mb-2">
                    <input type="text" id="gwUserPickerKeyword" class="form-control"
                           form="__gw_user_picker_isolated__"
                           placeholder="이름·이메일·부서 검색"/>
                    <button class="btn btn-outline-primary" type="button" id="gwUserPickerSearchBtn">
                        <i class="bi bi-search"></i> 검색
                    </button>
                </div>
                <div class="table-responsive" style="max-height:380px">
                    <table class="table table-hover table-sm align-middle mb-0">
                        <thead class="table-light sticky-top">
                            <tr>
                                <th id="gwUserPickerCheckCol" style="width:30px;display:none"></th>
                                <th>이름</th><th>부서</th><th>직급</th><th class="small text-muted">이메일</th>
                            </tr>
                        </thead>
                        <tbody id="gwUserPickerBody">
                            <tr><td colspan="5" class="text-center text-muted py-3">검색하여 사원을 선택하세요.</td></tr>
                        </tbody>
                    </table>
                </div>
                <div id="gwUserPickerSelectedBox" class="border-top pt-2 mt-2 d-none">
                    <div class="small text-muted mb-1"><i class="bi bi-check2-square"></i> 선택된 사원</div>
                    <div id="gwUserPickerSelected" class="d-flex flex-wrap gap-1"></div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-outline-secondary btn-sm" data-bs-dismiss="modal">취소</button>
                <button type="button" class="btn btn-primary btn-sm" id="gwUserPickerConfirm" style="display:none">선택 완료</button>
            </div>
        </div>
    </div>
</div>

<script>
(function () {
    var ctx = '${pageContext.request.contextPath}';
    var state = null;       // 현재 picker 호출 옵션
    var selected = [];      // 다중 선택 누적

    function $(id) { return document.getElementById(id); }
    function modal() { return bootstrap.Modal.getOrCreateInstance($('gwUserPickerModal')); }
    function escape(s) {
        return (s == null ? '' : String(s))
            .replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;').replace(/'/g, '&#39;');
    }

    function search() {
        var kw = $('gwUserPickerKeyword').value.trim();
        fetch(ctx + '/cmm/user-picker.json?keyword=' + encodeURIComponent(kw) + '&limit=100',
              { credentials: 'same-origin' })
            .then(function (r) { return r.json(); })
            .then(render)
            .catch(function () { renderError(); });
    }

    function render(users) {
        var tbody = $('gwUserPickerBody');
        if (!users || users.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" class="text-center text-muted py-3">검색 결과가 없습니다.</td></tr>';
            return;
        }
        var html = '';
        users.forEach(function (u) {
            var checked = selected.indexOf(u.userId) >= 0;
            html += '<tr data-uid="' + u.userId + '" style="cursor:pointer"' +
                    (checked ? ' class="table-active"' : '') + '>';
            if (state.multi) {
                html += '<td><input type="checkbox" class="form-check-input" ' +
                        (checked ? 'checked' : '') + ' data-uid="' + u.userId +
                        '" data-name="' + escape(u.name) + '"></td>';
            }
            html += '<td><strong>' + escape(u.name) + '</strong></td>'
                  + '<td class="small">' + escape(u.deptNm) + '</td>'
                  + '<td class="small">' + escape(u.positionNm) + '</td>'
                  + '<td class="small text-muted">' + escape(u.email) + '</td></tr>';
        });
        tbody.innerHTML = html;
        // 행 클릭/체크박스 이벤트 위임
        Array.prototype.forEach.call(tbody.querySelectorAll('tr[data-uid]'), function (tr) {
            tr.addEventListener('click', function (e) {
                if (e.target.tagName === 'INPUT') return;     // 체크박스 더블 처리 방지
                pick(parseInt(tr.dataset.uid, 10), tr.querySelector('strong').textContent);
            });
        });
        if (state.multi) {
            Array.prototype.forEach.call(tbody.querySelectorAll('input[type=checkbox]'), function (cb) {
                cb.addEventListener('change', function () {
                    pickMulti(parseInt(cb.dataset.uid, 10), cb.dataset.name, cb.checked);
                });
            });
        }
    }

    function renderError() {
        $('gwUserPickerBody').innerHTML =
            '<tr><td colspan="5" class="text-center text-danger py-3">검색 중 오류가 발생했습니다.</td></tr>';
    }

    /** 단일 선택 — 즉시 폼에 채우고 닫음. */
    function pick(uid, name) {
        if (state.multi) { return; }
        if (state.hidden)  { $(state.hidden).value  = uid; }
        if (state.display) { $(state.display).value = name; }
        if (typeof state.onSelect === 'function') state.onSelect({userId: uid, name: name});
        modal().hide();
    }

    /** 다중 선택 — selected 배열 갱신 + chips 표시. */
    function pickMulti(uid, name, checked) {
        var idx = selected.indexOf(uid);
        if (checked && idx < 0) selected.push(uid);
        if (!checked && idx >= 0) selected.splice(idx, 1);
        renderChips();
    }

    function renderChips() {
        var box = $('gwUserPickerSelectedBox');
        var area = $('gwUserPickerSelected');
        if (!state.multi) return;
        if (selected.length === 0) { box.classList.add('d-none'); area.innerHTML = ''; return; }
        box.classList.remove('d-none');
        // 이름을 모르는 경우(체크박스에서 받아온 dataset.name 사용) 화면 표시
        var rows = document.querySelectorAll('#gwUserPickerBody input[type=checkbox]');
        var nameMap = {};
        Array.prototype.forEach.call(rows, function (cb) { nameMap[cb.dataset.uid] = cb.dataset.name; });
        area.innerHTML = selected.map(function (uid) {
            return '<span class="badge text-bg-primary">' + escape(nameMap[uid] || ('#'+uid)) + '</span>';
        }).join(' ');
    }

    function confirmMulti() {
        if (!state.multi) return;
        if (state.multiSelect) {
            var sel = $(state.multiSelect);
            if (sel) {
                sel.innerHTML = '';
                selected.forEach(function (uid) {
                    var opt = document.createElement('option');
                    opt.value = uid; opt.selected = true; opt.textContent = uid;
                    sel.appendChild(opt);
                });
            }
        }
        if (state.chipsContainer) {
            var area = $(state.chipsContainer);
            if (area) {
                var rows = document.querySelectorAll('#gwUserPickerBody input[type=checkbox]');
                var nameMap = {};
                Array.prototype.forEach.call(rows, function (cb) { nameMap[cb.dataset.uid] = cb.dataset.name; });
                area.innerHTML = selected.map(function (uid) {
                    return '<span class="badge text-bg-secondary me-1">' + escape(nameMap[uid] || ('#'+uid)) + '</span>';
                }).join('');
            }
        }
        if (typeof state.onSelect === 'function') state.onSelect(selected.slice());
        modal().hide();
    }

    // Public API
    window.openUserPicker = function (opts) {
        state = Object.assign({multi: false}, opts || {});
        selected = [];
        $('gwUserPickerKeyword').value = '';
        $('gwUserPickerCheckCol').style.display = state.multi ? '' : 'none';
        $('gwUserPickerConfirm').style.display  = state.multi ? '' : 'none';
        $('gwUserPickerSelectedBox').classList.add('d-none');
        $('gwUserPickerBody').innerHTML =
            '<tr><td colspan="5" class="text-center text-muted py-3">검색하여 사원을 선택하세요.</td></tr>';
        modal().show();
        // 모달 표시 후 검색창 포커스 + 첫 100명 로드
        setTimeout(function () { $('gwUserPickerKeyword').focus(); search(); }, 200);
    };

    // 키워드 입력 동작 (Enter / 디바운스)
    // - Enter 시 form 제출이 일어나지 않도록 keydown 에서 즉시 preventDefault.
    //   (input 자체에도 form="__gw_user_picker_isolated__" 로 부모 form 격리)
    // - 그 외 키는 keyup 단계에서 300ms 디바운스로 자동 검색.
    var timer = null;
    document.addEventListener('DOMContentLoaded', function () {
        var kw = $('gwUserPickerKeyword');
        if (!kw) return;
        $('gwUserPickerSearchBtn').addEventListener('click', search);
        kw.addEventListener('keydown', function (e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                e.stopPropagation();
                search();
            }
        });
        kw.addEventListener('keyup', function (e) {
            if (e.key === 'Enter') return;
            clearTimeout(timer);
            timer = setTimeout(search, 300);
        });
        $('gwUserPickerConfirm').addEventListener('click', confirmMulti);
    });
})();
</script>
