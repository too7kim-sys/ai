package egovframework.groupware.approval.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import egovframework.groupware.approval.service.ApprovalDocVO;
import egovframework.groupware.approval.service.ApprovalService;
import egovframework.groupware.auth.security.CustomUserDetails;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class ApprovalController {

    private final ApprovalService service;
    private static final ObjectMapper JSON = new ObjectMapper();

    public ApprovalController(ApprovalService service) { this.service = service; }

    /** 결재 본문 JSON 의 키를 사람이 읽을 한글 라벨로 매핑. */
    private static String fieldLabel(String key) {
        switch (key) {
            case "leaveTypeCd": return "휴가 종류";
            case "start":       return "시작일";
            case "end":         return "종료일";
            case "days":        return "일수";
            case "reason":      return "사유";
            case "reportTitle": return "제목";
            case "purpose":     return "목적";
            case "totalAmount": return "총액";
            case "amount":      return "금액";
            case "memo":        return "비고";
            default:            return key;
        }
    }

    /** 결재 본문 JSON 을 "라벨 → 값" 순서맵으로 파싱. 실패 시 null (JSP 가 원문 표시). */
    private Map<String, String> parseContent(String json) {
        if (json == null || json.isBlank()) return new LinkedHashMap<>();
        try {
            Map<String, Object> raw = JSON.readValue(json,
                    new TypeReference<LinkedHashMap<String, Object>>() {});
            Map<String, String> out = new LinkedHashMap<>();
            for (Map.Entry<String, Object> e : raw.entrySet()) {
                Object v = e.getValue();
                out.put(fieldLabel(e.getKey()), v == null ? "" : String.valueOf(v));
            }
            return out;
        } catch (Exception ex) {
            return null;
        }
    }

    @GetMapping("/approval/write.do")
    public String writeForm(@RequestParam(required = false) String form, Model model) {
        model.addAttribute("forms", service.listForms());
        model.addAttribute("formCd", form);
        return "approval/write";
    }

    @GetMapping("/approval/pending.do")
    public String pending(@AuthenticationPrincipal CustomUserDetails me, Model model) {
        model.addAttribute("list", service.listPending(me.getUserId()));
        model.addAttribute("box", "pending");
        return "approval/box";
    }

    @GetMapping("/approval/draft.do")
    public String draft(@AuthenticationPrincipal CustomUserDetails me, Model model) {
        model.addAttribute("list", service.listByDrafter(me.getUserId(), null));
        model.addAttribute("box", "draft");
        return "approval/box";
    }

    @GetMapping("/approval/completed.do")
    public String completed(@AuthenticationPrincipal CustomUserDetails me, Model model) {
        model.addAttribute("list", service.listCompleted(me.getUserId(), "APPROVED"));
        model.addAttribute("box", "completed");
        return "approval/box";
    }

    @GetMapping("/approval/rejected.do")
    public String rejected(@AuthenticationPrincipal CustomUserDetails me, Model model) {
        model.addAttribute("list", service.listCompleted(me.getUserId(), "REJECTED"));
        model.addAttribute("box", "rejected");
        return "approval/box";
    }

    @GetMapping("/approval/detail.do")
    public String detail(@AuthenticationPrincipal CustomUserDetails me,
                         @RequestParam Long docId, Model model) {
        ApprovalDocVO d = service.findDoc(docId);
        // 기안자·결재선 참여자(또는 위임 수임자)·ADMIN 만 열람 가능 — docId 열거 차단.
        if (d == null || !canViewDoc(d, me)) {
            throw new AccessDeniedException("결재 문서 열람 권한이 없습니다");
        }
        model.addAttribute("d", d);
        model.addAttribute("contentFields", parseContent(d.getContentJson()));
        return "approval/detail";
    }

    private boolean canViewDoc(ApprovalDocVO d, CustomUserDetails me) {
        Long uid = me.getUserId();
        if (uid.equals(d.getDrafterId()) || "ADMIN".equals(me.getRoleCd())) return true;
        return d.getLines().stream().anyMatch(l ->
                uid.equals(l.getApproverId()) || uid.equals(l.getDelegatedToUserId()));
    }

    @PostMapping("/approval/act.do")
    public String act(@AuthenticationPrincipal CustomUserDetails me,
                      @RequestParam Long docId,
                      @RequestParam boolean approve,
                      @RequestParam(required = false) String comment) {
        service.act(docId, me.getUserId(), approve, comment);
        return "redirect:/approval/pending.do";
    }

    @PostMapping("/approval/cancel.do")
    public String cancel(@AuthenticationPrincipal CustomUserDetails me,
                         @RequestParam Long docId) {
        service.cancel(docId, me.getUserId());
        return "redirect:/approval/draft.do";
    }
}
