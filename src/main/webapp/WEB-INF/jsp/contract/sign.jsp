<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>근로계약 서명</title>
<h2 class="mb-3"><i class="bi bi-pen"></i> 근로계약 서명 - ${c.contractNo}</h2>
<div class="card mb-3"><div class="card-body">
    ${body}
</div></div>
<form method="post" action="${pageContext.request.contextPath}/contract/my/sign.do">
    <sec:csrfInput/>
    <input type="hidden" name="contractId" value="${c.contractId}"/>
    <div class="mb-3">
        <label class="form-label">서명 (캔버스 - 마우스로 그려주세요)</label>
        <canvas id="sig" width="500" height="160" style="border:1px solid #ccc; background:#fff;"></canvas>
        <input type="hidden" name="signature" id="sigData"/>
        <div><button type="button" class="btn btn-sm btn-outline-secondary mt-2" onclick="clearSig()">지우기</button></div>
    </div>
    <div class="form-check mb-3">
        <input class="form-check-input" type="checkbox" id="agree" required/>
        <label class="form-check-label" for="agree">위 근로계약 내용을 모두 확인하였고, 이에 동의합니다.</label>
    </div>
    <button type="submit" class="btn btn-primary" onclick="document.getElementById('sigData').value=document.getElementById('sig').toDataURL('image/png');">
        <i class="bi bi-check2-circle"></i> 서명 완료
    </button>
</form>
<script>
(function(){
  var c = document.getElementById('sig'); var ctx = c.getContext('2d'); var drawing = false;
  function pos(e){ var r=c.getBoundingClientRect(); return {x:(e.clientX||e.touches[0].clientX)-r.left, y:(e.clientY||e.touches[0].clientY)-r.top}; }
  c.addEventListener('mousedown', function(e){drawing=true; var p=pos(e); ctx.beginPath(); ctx.moveTo(p.x,p.y);});
  c.addEventListener('mousemove', function(e){if(!drawing) return; var p=pos(e); ctx.lineTo(p.x,p.y); ctx.stroke();});
  c.addEventListener('mouseup', function(){drawing=false;});
  c.addEventListener('mouseleave', function(){drawing=false;});
})();
function clearSig(){ var c=document.getElementById('sig'); c.getContext('2d').clearRect(0,0,c.width,c.height); }
</script>
