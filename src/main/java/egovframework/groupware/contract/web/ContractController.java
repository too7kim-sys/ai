package egovframework.groupware.contract.web;

import egovframework.groupware.auth.security.CustomUserDetails;
import egovframework.groupware.contract.service.ContractService;
import egovframework.groupware.contract.service.EmploymentContractVO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@Controller
public class ContractController {

    private final ContractService service;

    public ContractController(ContractService service) {
        this.service = service;
    }

    /* ------------------ 사원 ------------------ */

    @GetMapping("/contract/my.do")
    public String my(@AuthenticationPrincipal CustomUserDetails me, Model model) {
        model.addAttribute("list", service.listByUser(me.getUserId()));
        return "contract/my";
    }

    @GetMapping("/contract/my/sign.do")
    public String signForm(@AuthenticationPrincipal CustomUserDetails me,
                           @RequestParam Long contractId, Model model) {
        EmploymentContractVO c = service.findById(contractId);
        if (c == null || !c.getUserId().equals(me.getUserId())) return "error/403";
        model.addAttribute("c", c);
        model.addAttribute("body", service.renderBody(contractId));
        return "contract/sign";
    }

    @PostMapping("/contract/my/sign.do")
    public String sign(@AuthenticationPrincipal CustomUserDetails me,
                       @RequestParam Long contractId,
                       @RequestParam(required = false) String signature) {
        service.sign(contractId, me.getUserId(), signature);
        return "redirect:/contract/my.do";
    }

    @GetMapping("/contract/my/pdf.do")
    public ResponseEntity<byte[]> myPdf(@AuthenticationPrincipal CustomUserDetails me,
                                        @RequestParam Long contractId) {
        EmploymentContractVO c = service.findById(contractId);
        if (c == null || !c.getUserId().equals(me.getUserId())) return ResponseEntity.status(403).build();
        byte[] pdf = service.generatePdf(contractId);
        String fname = URLEncoder.encode("근로계약서-" + c.getContractNo() + ".pdf", StandardCharsets.UTF_8);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + fname)
            .contentType(MediaType.APPLICATION_PDF).body(pdf);
    }

    /* ------------------ HR 관리 ------------------ */

    @GetMapping("/contract/admin/list.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public String adminList(@RequestParam(required = false) String keyword,
                            @RequestParam(required = false) String status,
                            Model model) {
        model.addAttribute("list", service.listAll(keyword, status));
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        return "contract/admin-list";
    }

    @GetMapping("/contract/admin/write.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public String writeForm(@RequestParam(required = false) Long userId, Model model) {
        model.addAttribute("templates", service.listTemplates());
        model.addAttribute("userId", userId);
        return "contract/admin-write";
    }

    @PostMapping("/contract/admin/write.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public String write(@RequestParam Long userId,
                        @RequestParam Long templateId,
                        @RequestParam String contractTypeCd,
                        @RequestParam String startDt,
                        @RequestParam(required = false) String endDt,
                        @RequestParam String workplace,
                        @RequestParam String jobDescription,
                        @RequestParam(required = false, defaultValue = "40") String workHoursPerWeek,
                        @RequestParam(required = false) String specialTerms,
                        @RequestParam(required = false) Long salaryContractId) {
        EmploymentContractVO vo = new EmploymentContractVO();
        vo.setUserId(userId);
        vo.setTemplateId(templateId);
        vo.setContractTypeCd(contractTypeCd);
        vo.setStartDt(LocalDate.parse(startDt));
        if (endDt != null && !endDt.isBlank()) vo.setEndDt(LocalDate.parse(endDt));
        vo.setWorkplace(workplace);
        vo.setJobDescription(jobDescription);
        vo.setWorkHoursPerWeek(new BigDecimal(workHoursPerWeek));
        vo.setSpecialTerms(specialTerms);
        vo.setSalaryContractId(salaryContractId);
        vo.setStatusCd("DRAFT");
        Long id = service.create(vo);
        return "redirect:/contract/admin/preview.do?contractId=" + id;
    }

    @GetMapping("/contract/admin/preview.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public String preview(@RequestParam Long contractId, Model model) {
        EmploymentContractVO c = service.findById(contractId);
        model.addAttribute("c", c);
        model.addAttribute("body", service.renderBody(contractId));
        return "contract/admin-preview";
    }

    @PostMapping("/contract/admin/sign-request.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public String sendSignRequest(@RequestParam Long contractId) {
        service.sendSignRequest(contractId);
        return "redirect:/contract/admin/preview.do?contractId=" + contractId;
    }

    @PostMapping("/contract/admin/activate.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public String activate(@RequestParam Long contractId) {
        service.activate(contractId);
        return "redirect:/contract/admin/preview.do?contractId=" + contractId;
    }
}
