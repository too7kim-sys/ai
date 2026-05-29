package egovframework.groupware.contract.service.impl;

import egovframework.groupware.cmm.ApiException;
import egovframework.groupware.contract.mapper.ContractMapper;
import egovframework.groupware.contract.service.ContractService;
import egovframework.groupware.contract.service.ContractTemplateVO;
import egovframework.groupware.contract.service.EmploymentContractVO;
import egovframework.groupware.mail.service.MailAttachment;
import egovframework.groupware.mail.service.MailRequest;
import egovframework.groupware.mail.service.MailService;
import egovframework.groupware.mail.service.impl.TemplateRenderer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ContractServiceImpl implements ContractService {

    private final ContractMapper mapper;
    private final ContractPdfWriter pdfWriter;
    private final MailService mailService;
    private final TemplateRenderer renderer;
    private final String baseUrl;
    private static final NumberFormat WON = NumberFormat.getNumberInstance(Locale.KOREA);

    public ContractServiceImpl(ContractMapper mapper, ContractPdfWriter pdfWriter,
                               MailService mailService, TemplateRenderer renderer,
                               @Value("${app.base-url:http://localhost:8080/groupware}") String baseUrl) {
        this.mapper = mapper;
        this.pdfWriter = pdfWriter;
        this.mailService = mailService;
        this.renderer = renderer;
        this.baseUrl = baseUrl;
    }

    @Override public List<ContractTemplateVO> listTemplates() { return mapper.listTemplates(); }

    @Override
    @Transactional
    public Long create(EmploymentContractVO vo) {
        if (vo.getContractNo() == null || vo.getContractNo().isBlank()) {
            vo.setContractNo("EC-" + System.currentTimeMillis() + "-" + ThreadLocalRandom.current().nextInt(1000));
        }
        mapper.insert(vo);
        return vo.getContractId();
    }

    @Override
    @Transactional
    public void update(EmploymentContractVO vo) { mapper.update(vo); }

    @Override
    public EmploymentContractVO findById(Long contractId) { return mapper.findById(contractId); }

    @Override
    public List<EmploymentContractVO> listAll(String keyword, String status) {
        return mapper.listAll(keyword, status);
    }

    @Override
    public List<EmploymentContractVO> listByUser(Long userId) { return mapper.listByUser(userId); }

    @Override
    @Transactional
    public Long sendSignRequest(Long contractId) {
        EmploymentContractVO c = mapper.findById(contractId);
        if (c == null) throw new ApiException("CONTRACT_NOT_FOUND", "계약을 찾을 수 없습니다");
        if (c.getEmail() == null) throw new ApiException("NO_EMAIL", "사원 이메일이 없습니다");
        mapper.updateStatus(contractId, "SENT");
        Map<String, Object> vars = vars(c);
        vars.put("signUrl", baseUrl + "/contract/my/sign.do?contractId=" + contractId);
        return mailService.enqueue(MailRequest.builder()
            .templateCd("CONTRACT_SIGN_REQUEST")
            .to(List.of(c.getEmail()))
            .vars(vars)
            .relatedEntity("CONTRACT")
            .relatedId(String.valueOf(contractId))
            .build());
    }

    @Override
    @Transactional
    public void sign(Long contractId, Long userId, String signatureBase64) {
        EmploymentContractVO c = mapper.findById(contractId);
        if (c == null) throw new ApiException("CONTRACT_NOT_FOUND", "계약을 찾을 수 없습니다");
        if (!c.getUserId().equals(userId)) {
            throw new ApiException("FORBIDDEN", "본인의 계약만 서명할 수 있습니다");
        }
        if (!"SENT".equals(c.getStatusCd()) && !"DRAFT".equals(c.getStatusCd())) {
            throw new ApiException("INVALID_STATE", "현재 상태에서는 서명할 수 없습니다: " + c.getStatusCd());
        }
        mapper.markSigned(contractId, signatureBase64);
    }

    @Override
    @Transactional
    public void activate(Long contractId) { mapper.updateStatus(contractId, "ACTIVE"); }

    @Override
    public String renderBody(Long contractId) {
        EmploymentContractVO c = mapper.findById(contractId);
        if (c == null) return "";
        ContractTemplateVO tpl = mapper.findTemplate(c.getTemplateId());
        return renderer.render(tpl == null ? "" : tpl.getBodyHtml(), vars(c));
    }

    @Override
    public byte[] generatePdf(Long contractId) {
        EmploymentContractVO c = mapper.findById(contractId);
        if (c == null) throw new ApiException("CONTRACT_NOT_FOUND", "계약을 찾을 수 없습니다");
        return pdfWriter.write(c, renderBody(contractId));
    }

    private Map<String, Object> vars(EmploymentContractVO c) {
        Map<String, Object> v = new HashMap<>();
        v.put("companyName", "사내 그룹웨어");
        v.put("contractNo", nz(c.getContractNo()));
        v.put("userName", nz(c.getUserName()));
        v.put("deptNm", nz(c.getDeptNm()));
        v.put("email", nz(c.getEmail()));
        v.put("contractTypeCd", nz(c.getContractTypeCd()));
        v.put("contractTypeNm", typeNm(c.getContractTypeCd()));
        v.put("workplace", nz(c.getWorkplace()));
        v.put("jobDescription", nz(c.getJobDescription()));
        v.put("startDt", c.getStartDt() == null ? "" : c.getStartDt().toString());
        v.put("endDt", c.getEndDt() == null ? "기간의 정함이 없음" : c.getEndDt().toString());
        v.put("workHoursPerWeek", c.getWorkHoursPerWeek() == null ? "40" : c.getWorkHoursPerWeek().toPlainString());
        v.put("workStartTime", c.getWorkStartTime() == null ? "09:00" : c.getWorkStartTime());
        v.put("workEndTime", c.getWorkEndTime() == null ? "18:00" : c.getWorkEndTime());
        v.put("breakMinutes", c.getBreakMinutes() == null ? 60 : c.getBreakMinutes());
        v.put("weeklyHoliday", c.getWeeklyHoliday() == null ? "일요일" : c.getWeeklyHoliday());
        v.put("annualPaidLeaveDays", c.getAnnualPaidLeaveDays() == null ? 15 : c.getAnnualPaidLeaveDays());
        v.put("probationMonths", c.getProbationMonths() == null ? 0 : c.getProbationMonths());
        v.put("annualSalary", c.getAnnualSalary() == null ? "(별도 협의)" : WON.format(c.getAnnualSalary()));
        v.put("monthlyBaseSal", c.getMonthlyBaseSal() == null ? "(연봉 ÷ 12)" : WON.format(c.getMonthlyBaseSal()));
        v.put("paymentDay", c.getPaymentDay() == null ? "25" : String.valueOf(c.getPaymentDay()));
        v.put("insuranceApplied", insuranceLabel(c.getInsuranceAppliedJson()));
        v.put("specialTerms", c.getSpecialTerms() == null || c.getSpecialTerms().isBlank()
                ? "(별도 약정 없음)" : c.getSpecialTerms());
        return v;
    }

    private static String nz(String s) { return s == null ? "" : s; }

    private static String typeNm(String cd) {
        if (cd == null) return "";
        switch (cd) {
            case "REGULAR":    return "정규직";
            case "FIXED_TERM": return "기간제";
            case "PART_TIME":  return "단시간";
            case "TEMP":       return "시용(수습)";
            case "INTERN":     return "인턴";
            default:           return cd;
        }
    }

    /** insurance_applied_json — 단순 substring 검사로 한글 라벨 변환. */
    private static String insuranceLabel(String json) {
        if (json == null || json.isBlank()) return "(미지정)";
        StringBuilder sb = new StringBuilder();
        if (json.contains("\"NP\":true")) sb.append("국민연금, ");
        if (json.contains("\"HI\":true")) sb.append("건강보험(장기요양 포함), ");
        if (json.contains("\"EI\":true")) sb.append("고용보험, ");
        if (json.contains("\"WC\":true")) sb.append("산재보험");
        String s = sb.toString().replaceAll(", $", "").trim();
        return s.isEmpty() ? "(미적용)" : s;
    }
}
