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
    });
})();
