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
