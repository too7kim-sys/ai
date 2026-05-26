// =============================================================
//  사내 그룹웨어 공통 스크립트
// =============================================================
(function () {
    'use strict';

    document.addEventListener('DOMContentLoaded', function () {
        var body = document.body;
        var toggle = document.getElementById('menuToggle');
        var backdrop = document.getElementById('sidebarBackdrop');
        var isDesktop = function () { return window.matchMedia('(min-width: 992px)').matches; };

        function closeSidebar() { body.classList.remove('sidebar-open'); }

        if (toggle) {
            toggle.addEventListener('click', function () {
                if (isDesktop()) {
                    body.classList.toggle('sidebar-collapsed');
                } else {
                    body.classList.toggle('sidebar-open');
                }
            });
        }
        if (backdrop) backdrop.addEventListener('click', closeSidebar);

        // 모바일에서 메뉴 클릭 시 사이드바 닫기
        document.querySelectorAll('.side-nav .nav-link').forEach(function (link) {
            link.addEventListener('click', function () {
                if (!isDesktop()) closeSidebar();
            });
        });

        // 현재 페이지 메뉴 활성화 (가장 긴 매칭 경로)
        var path = window.location.pathname;
        var best = null, bestLen = -1;
        document.querySelectorAll('.side-nav .nav-link').forEach(function (link) {
            var linkPath = (link.getAttribute('href') || '').split('?')[0];
            if (linkPath && path.indexOf(linkPath) !== -1 && linkPath.length > bestLen) {
                best = link; bestLen = linkPath.length;
            }
        });
        if (best) best.classList.add('active');

        // ----- 상단 그룹 탭 + 좌측 세부 메뉴 (1차/2차 메뉴 분리) -----
        // .menu-title 다음의 형제 <li> 들을 같은 그룹으로 묶고, 상단 탭 클릭에
        // 따라 좌측에는 선택된 그룹의 항목만 표시한다.
        var titles = document.querySelectorAll('.side-nav .menu-title');
        var groups = {}; // name -> { items: [li...] }
        var groupOrder = [];
        titles.forEach(function (title) {
            var name = (title.textContent || '').trim();
            var items = [];
            for (var sib = title.nextElementSibling; sib && !sib.classList.contains('menu-title');
                 sib = sib.nextElementSibling) {
                sib.classList.add('group-item');
                sib.setAttribute('data-group', name);
                items.push(sib);
            }
            if (items.length > 0) { groups[name] = { items: items }; groupOrder.push(name); }
        });

        // 좌측 사이드바 상단에 "현재 그룹" 라벨 추가
        var sideNav = document.querySelector('.side-nav');
        var currentGroupLabel = null;
        if (sideNav && groupOrder.length > 0) {
            currentGroupLabel = document.createElement('li');
            currentGroupLabel.className = 'current-group-label';
            sideNav.insertBefore(currentGroupLabel, sideNav.firstChild);
        }

        function setCurrentGroup(name) {
            // 좌측 항목 필터링
            Object.keys(groups).forEach(function (k) {
                var visible = (k === name);
                groups[k].items.forEach(function (li) {
                    if (visible) li.classList.remove('hidden');
                    else li.classList.add('hidden');
                });
            });
            // 좌측 라벨
            if (currentGroupLabel) {
                currentGroupLabel.innerHTML = '';
                var icon = document.createElement('i');
                icon.className = 'bi bi-folder2-open';
                var text = document.createElement('span');
                text.textContent = name;
                currentGroupLabel.appendChild(icon);
                currentGroupLabel.appendChild(text);
            }
            // 상단 탭 active 갱신
            document.querySelectorAll('.groupbar .group-tab').forEach(function (tab) {
                if (tab.getAttribute('data-group') === name) tab.classList.add('active');
                else tab.classList.remove('active');
            });
            localStorage.setItem('sb-active-group', name);
        }

        // 현재 페이지가 속한 그룹 자동 식별 → 폴백으로 localStorage → 첫 그룹
        var initialGroup = null;
        if (best) {
            var li = best.closest('li');
            if (li && li.hasAttribute('data-group')) initialGroup = li.getAttribute('data-group');
        }
        if (!initialGroup) initialGroup = localStorage.getItem('sb-active-group');
        if (!initialGroup || !groups[initialGroup]) initialGroup = groupOrder[0];
        if (initialGroup) setCurrentGroup(initialGroup);

        // 그룹 탭 클릭 — href 가 있는 탭(대시보드)은 그대로 이동, 없는 탭은 좌측만 전환
        document.querySelectorAll('.groupbar .group-tab').forEach(function (tab) {
            tab.addEventListener('click', function (e) {
                var hasHref = tab.getAttribute('href');
                if (hasHref) return;
                e.preventDefault();
                var name = tab.getAttribute('data-group');
                if (groups[name]) setCurrentGroup(name);
            });
        });

        // ----- 다크 / 라이트 모드 -----
        var themeToggle = document.getElementById('themeToggle');
        var themeIcon = document.getElementById('themeIcon');

        function applyTheme(mode) {
            if (mode === 'dark') {
                body.classList.add('dark-mode');
                if (themeIcon) themeIcon.className = 'bi bi-sun';
            } else {
                body.classList.remove('dark-mode');
                if (themeIcon) themeIcon.className = 'bi bi-moon-stars';
            }
        }
        applyTheme(localStorage.getItem('gw-theme') || 'light');

        if (themeToggle) {
            themeToggle.addEventListener('click', function () {
                var next = body.classList.contains('dark-mode') ? 'light' : 'dark';
                localStorage.setItem('gw-theme', next);
                applyTheme(next);
            });
        }

        // ----- 전체화면 -----
        var fsToggle = document.getElementById('fullscreenToggle');
        var fsIcon = document.getElementById('fullscreenIcon');

        if (fsToggle) {
            fsToggle.addEventListener('click', function () {
                if (!document.fullscreenElement) {
                    (document.documentElement.requestFullscreen
                        || document.documentElement.webkitRequestFullscreen
                        || function () {}).call(document.documentElement);
                } else {
                    (document.exitFullscreen || document.webkitExitFullscreen
                        || function () {}).call(document);
                }
            });
            document.addEventListener('fullscreenchange', function () {
                if (fsIcon) {
                    fsIcon.className = document.fullscreenElement
                        ? 'bi bi-fullscreen-exit' : 'bi bi-arrows-fullscreen';
                }
            });
        }
    });
})();
