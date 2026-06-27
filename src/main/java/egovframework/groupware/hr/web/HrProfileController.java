package egovframework.groupware.hr.web;

import egovframework.groupware.attach.service.AttachService;
import egovframework.groupware.attach.service.AttachVO;
import egovframework.groupware.auth.security.CustomUserDetails;
import egovframework.groupware.cmm.ApiException;
import egovframework.groupware.doc.web.DocController;
import egovframework.groupware.hr.service.HrAwardVO;
import egovframework.groupware.hr.service.HrCareerVO;
import egovframework.groupware.hr.service.HrEducationVO;
import egovframework.groupware.hr.service.HrProjectVO;
import egovframework.groupware.hr.service.HrService;
import egovframework.groupware.hr.service.HrTrainingVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 인사 프로필 부속 도메인(경력 · 학력 · 교육이수 · 상벌) 의 추가/삭제.
 *
 * <p>화면은 {@code /user/profile.do} 에 포함되어 있고, 본 컨트롤러는 그 폼들의
 * 처리만 담당한다.
 *
 * <p>권한 정책:
 * <ul>
 *   <li>경력 · 학력 · 교육이수: 본인 또는 ADMIN / HR_MANAGER 가 추가/삭제 가능</li>
 *   <li>상벌: HR 가 부여하는 정보이므로 ADMIN / HR_MANAGER 만 가능 (본인 변경 불가)</li>
 * </ul>
 */
@Controller
public class HrProfileController {

    private final HrService hrService;
    private final AttachService attachService;

    public HrProfileController(HrService hrService, AttachService attachService) {
        this.hrService = hrService;
        this.attachService = attachService;
    }

    /** 본인 또는 HR/ADMIN 이면 통과. 아니면 403. */
    private static void assertSelfOrHr(CustomUserDetails me, Long targetUserId) {
        if (me.getUserId().equals(targetUserId)) return;
        if ("ADMIN".equals(me.getRoleCd()) || "HR_MANAGER".equals(me.getRoleCd())) return;
        throw new ApiException("FORBIDDEN", "본인 또는 인사 담당자만 가능합니다");
    }

    /* ===== 경력 ===== */
    @PostMapping("/hr/career.do")
    public String addCareer(@AuthenticationPrincipal CustomUserDetails me,
                            @RequestParam Long userId,
                            @RequestParam String companyNm,
                            @RequestParam(required = false) String positionNm,
                            @RequestParam String startDt,
                            @RequestParam(required = false) String endDt,
                            @RequestParam(required = false) String description) {
        assertSelfOrHr(me, userId);
        HrCareerVO vo = new HrCareerVO();
        vo.setUserId(userId);
        vo.setCompanyNm(companyNm);
        vo.setPositionNm(positionNm);
        vo.setStartDt(LocalDate.parse(startDt));
        if (endDt != null && !endDt.isBlank()) vo.setEndDt(LocalDate.parse(endDt));
        vo.setDescription(description);
        vo.setCreatedBy(me.getUserId());
        hrService.createCareer(vo);
        return "redirect:/user/profile.do?userId=" + userId;
    }

    @PostMapping("/hr/career/delete.do")
    public String deleteCareer(@AuthenticationPrincipal CustomUserDetails me,
                               @RequestParam Long careerId, @RequestParam Long userId) {
        assertSelfOrHr(me, userId);
        hrService.deleteCareer(careerId);
        return "redirect:/user/profile.do?userId=" + userId;
    }

    /* ===== 학력 ===== */
    @PostMapping("/hr/education.do")
    public String addEducation(@AuthenticationPrincipal CustomUserDetails me,
                               @RequestParam Long userId,
                               @RequestParam String schoolNm,
                               @RequestParam(required = false) String major,
                               @RequestParam(required = false) String degreeCd,
                               @RequestParam String eduStatusCd,
                               @RequestParam(required = false) String admissionDt,
                               @RequestParam(required = false) String graduationDt) {
        assertSelfOrHr(me, userId);
        HrEducationVO vo = new HrEducationVO();
        vo.setUserId(userId);
        vo.setSchoolNm(schoolNm);
        vo.setMajor(major);
        vo.setDegreeCd(degreeCd);
        vo.setEduStatusCd(eduStatusCd);
        if (admissionDt != null && !admissionDt.isBlank()) vo.setAdmissionDt(LocalDate.parse(admissionDt));
        if (graduationDt != null && !graduationDt.isBlank()) vo.setGraduationDt(LocalDate.parse(graduationDt));
        vo.setCreatedBy(me.getUserId());
        hrService.createEducation(vo);
        return "redirect:/user/profile.do?userId=" + userId;
    }

    @PostMapping("/hr/education/delete.do")
    public String deleteEducation(@AuthenticationPrincipal CustomUserDetails me,
                                  @RequestParam Long eduId, @RequestParam Long userId) {
        assertSelfOrHr(me, userId);
        hrService.deleteEducation(eduId);
        return "redirect:/user/profile.do?userId=" + userId;
    }

    /* ===== 교육이수 ===== */
    @PostMapping("/hr/training.do")
    public String addTraining(@AuthenticationPrincipal CustomUserDetails me,
                              @RequestParam Long userId,
                              @RequestParam String courseNm,
                              @RequestParam(required = false) String providerNm,
                              @RequestParam(required = false) String startDt,
                              @RequestParam(required = false) String endDt,
                              @RequestParam(required = false) BigDecimal hours,
                              @RequestParam(required = false) String certNo) {
        assertSelfOrHr(me, userId);
        HrTrainingVO vo = new HrTrainingVO();
        vo.setUserId(userId);
        vo.setCourseNm(courseNm);
        vo.setProviderNm(providerNm);
        if (startDt != null && !startDt.isBlank()) vo.setStartDt(LocalDate.parse(startDt));
        if (endDt != null && !endDt.isBlank()) vo.setEndDt(LocalDate.parse(endDt));
        vo.setHours(hours);
        vo.setCertNo(certNo);
        vo.setCreatedBy(me.getUserId());
        hrService.createTraining(vo);
        return "redirect:/user/profile.do?userId=" + userId;
    }

    @PostMapping("/hr/training/delete.do")
    public String deleteTraining(@AuthenticationPrincipal CustomUserDetails me,
                                 @RequestParam Long trnId, @RequestParam Long userId) {
        assertSelfOrHr(me, userId);
        hrService.deleteTraining(trnId);
        return "redirect:/user/profile.do?userId=" + userId;
    }

    /* ===== 상벌 (HR 전용) ===== */
    @PostMapping("/hr/award.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public String addAward(@AuthenticationPrincipal CustomUserDetails me,
                           @RequestParam Long userId,
                           @RequestParam String awardTypeCd,
                           @RequestParam String title,
                           @RequestParam String occurredOn,
                           @RequestParam(required = false) String organization,
                           @RequestParam(required = false) String reason) {
        HrAwardVO vo = new HrAwardVO();
        vo.setUserId(userId);
        vo.setAwardTypeCd(awardTypeCd);
        vo.setTitle(title);
        vo.setOccurredOn(LocalDate.parse(occurredOn));
        vo.setOrganization(organization);
        vo.setReason(reason);
        vo.setCreatedBy(me.getUserId());
        hrService.createAward(vo);
        return "redirect:/user/profile.do?userId=" + userId;
    }

    @PostMapping("/hr/award/delete.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public String deleteAward(@RequestParam Long awardId, @RequestParam Long userId) {
        hrService.deleteAward(awardId);
        return "redirect:/user/profile.do?userId=" + userId;
    }

    /* ===== 프로젝트 수행 경력 (KOSA 표준 + 첨부) ===== */
    @PostMapping("/hr/project.do")
    public String addProject(@AuthenticationPrincipal CustomUserDetails me,
                             @RequestParam Long userId,
                             @RequestParam String projectNm,
                             @RequestParam(required = false) String clientNm,
                             @RequestParam(required = false) String contractorNm,
                             @RequestParam(required = false) String roleNm,
                             @RequestParam(required = false) String startDt,
                             @RequestParam(required = false) String endDt,
                             @RequestParam(required = false) String techStack,
                             @RequestParam(required = false) String description,
                             @RequestParam(required = false) String kosaGradeCd,
                             @RequestParam(required = false) String kosaConfirmedYn,
                             @RequestParam(value = "files", required = false) MultipartFile[] files) {
        assertSelfOrHr(me, userId);
        HrProjectVO vo = new HrProjectVO();
        vo.setUserId(userId);
        vo.setProjectNm(projectNm);
        vo.setClientNm(clientNm);
        vo.setContractorNm(contractorNm);
        vo.setRoleNm(roleNm);
        if (startDt != null && !startDt.isBlank()) vo.setStartDt(LocalDate.parse(startDt));
        if (endDt != null && !endDt.isBlank()) vo.setEndDt(LocalDate.parse(endDt));
        vo.setTechStack(techStack);
        vo.setDescription(description);
        vo.setKosaGradeCd(kosaGradeCd);
        vo.setKosaConfirmedYn("Y".equals(kosaConfirmedYn) ? "Y" : "N");
        vo.setCreatedBy(me.getUserId());
        hrService.createProject(vo, files);
        return "redirect:/user/profile.do?userId=" + userId;
    }

    @PostMapping("/hr/project/edit.do")
    public String editProject(@AuthenticationPrincipal CustomUserDetails me,
                              @RequestParam Long projectId,
                              @RequestParam Long userId,
                              @RequestParam String projectNm,
                              @RequestParam(required = false) String clientNm,
                              @RequestParam(required = false) String contractorNm,
                              @RequestParam(required = false) String roleNm,
                              @RequestParam(required = false) String startDt,
                              @RequestParam(required = false) String endDt,
                              @RequestParam(required = false) String techStack,
                              @RequestParam(required = false) String description,
                              @RequestParam(required = false) String kosaGradeCd,
                              @RequestParam(required = false) String kosaConfirmedYn,
                              @RequestParam(value = "files", required = false) MultipartFile[] files) {
        assertSelfOrHr(me, userId);
        HrProjectVO vo = new HrProjectVO();
        vo.setProjectId(projectId);
        vo.setUserId(userId);
        vo.setProjectNm(projectNm);
        vo.setClientNm(clientNm);
        vo.setContractorNm(contractorNm);
        vo.setRoleNm(roleNm);
        if (startDt != null && !startDt.isBlank()) vo.setStartDt(LocalDate.parse(startDt));
        if (endDt != null && !endDt.isBlank()) vo.setEndDt(LocalDate.parse(endDt));
        vo.setTechStack(techStack);
        vo.setDescription(description);
        vo.setKosaGradeCd(kosaGradeCd);
        vo.setKosaConfirmedYn("Y".equals(kosaConfirmedYn) ? "Y" : "N");
        vo.setCreatedBy(me.getUserId());
        hrService.updateProject(vo, files);
        return "redirect:/user/profile.do?userId=" + userId;
    }

    @PostMapping("/hr/project/delete.do")
    public String deleteProject(@AuthenticationPrincipal CustomUserDetails me,
                                @RequestParam Long projectId, @RequestParam Long userId) {
        assertSelfOrHr(me, userId);
        hrService.deleteProject(projectId);
        return "redirect:/user/profile.do?userId=" + userId;
    }

    /**
     * 프로젝트 첨부 (코사증빙 등) 다운로드.
     * <p>본인 또는 HR/ADMIN 만 다운로드 가능. owner_entity 가 HR_PROJECT 가 아닌
     * 첨부 ID 로 다른 모듈 파일을 가져오는 IDOR 도 차단한다.
     */
    @GetMapping("/hr/project/attach/download.do")
    public void downloadProjectAttach(@AuthenticationPrincipal CustomUserDetails me,
                                      @RequestParam Long attachId,
                                      HttpServletResponse resp) throws IOException {
        AttachVO vo = attachService.findById(attachId);
        if (vo == null) { resp.sendError(404); return; }
        if (!"HR_PROJECT".equals(attachService.findOwnerEntity(attachId))) {
            resp.sendError(403); return;
        }
        HrProjectVO p = hrService.findProjectByAttachGroup(vo.getGroupId());
        boolean isHr = "ADMIN".equals(me.getRoleCd()) || "HR_MANAGER".equals(me.getRoleCd());
        if (p == null || (!isHr && !p.getUserId().equals(me.getUserId()))) {
            resp.sendError(403); return;
        }
        DocController.streamAttachment(resp, vo, attachService.resolvePath(vo));
    }
}
