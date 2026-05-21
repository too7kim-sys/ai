package egovframework.groupware.mail.web;

import egovframework.groupware.cmm.Paging;
import egovframework.groupware.mail.service.MailLogVO;
import egovframework.groupware.mail.service.MailRequest;
import egovframework.groupware.mail.service.MailService;
import egovframework.groupware.mail.service.MailTemplateVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;

/**
 * 메일 발송 이력 + 템플릿 관리 (ADMIN/HR_MANAGER 전용).
 */
@Controller
@PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
public class MailController {

    private final MailService service;

    public MailController(MailService service) { this.service = service; }

    @GetMapping("/mail/log.do")
    public String log(@RequestParam(required = false) String status,
                      @RequestParam(required = false) String keyword,
                      @RequestParam(required = false, defaultValue = "1") int page,
                      Model model) {
        Paging p = new Paging();
        p.setPage(Math.max(1, page));
        p.setSize(20);

        List<MailLogVO> list = service.searchLogs(status, keyword, p.getOffset(), p.getSize());
        p.setTotal(service.countLogs(status, keyword));

        model.addAttribute("list", list);
        model.addAttribute("paging", p);
        model.addAttribute("status", status);
        model.addAttribute("keyword", keyword);
        model.addAttribute("statuses", Arrays.asList("QUEUED", "SENT", "FAILED"));
        return "mail/log";
    }

    @GetMapping("/mail/template.do")
    public String templateList(Model model) {
        model.addAttribute("list", service.listTemplates());
        return "mail/template";
    }

    @GetMapping("/mail/template/edit.do")
    public String templateEdit(@RequestParam String templateCd, Model model) {
        MailTemplateVO vo = service.findTemplate(templateCd);
        if (vo == null) return "redirect:/mail/template.do";
        model.addAttribute("vo", vo);
        return "mail/template-edit";
    }

    @PostMapping("/mail/template/edit.do")
    public String templateUpdate(@RequestParam String templateCd,
                                 @RequestParam String templateNm,
                                 @RequestParam String subject,
                                 @RequestParam String bodyHtml,
                                 @RequestParam(required = false) String useYn) {
        MailTemplateVO vo = service.findTemplate(templateCd);
        if (vo == null) return "redirect:/mail/template.do";
        vo.setTemplateNm(templateNm);
        vo.setSubject(subject);
        vo.setBodyHtml(bodyHtml);
        vo.setUseYn("N".equals(useYn) ? "N" : "Y");
        service.updateTemplate(vo);
        return "redirect:/mail/template.do";
    }

    /** 테스트 메일 발송 (ADMIN 만). */
    @PostMapping("/mail/test-send.do")
    public String testSend(@RequestParam String toEmail,
                           @RequestParam(required = false) String templateCd) {
        MailRequest req = MailRequest.builder()
                .to(List.of(toEmail))
                .templateCd(templateCd == null || templateCd.isBlank() ? null : templateCd)
                .subject("[테스트] 사내 그룹웨어 메일 발송 테스트")
                .bodyHtml("<p>본 메일은 메일 발송 시스템 점검용 테스트 메일입니다.</p>")
                .build();
        service.enqueue(req);
        return "redirect:/mail/log.do";
    }
}
