package egovframework.groupware.hr.web;

import egovframework.groupware.auth.security.CustomUserDetails;
import egovframework.groupware.cmm.ApiException;
import egovframework.groupware.hr.service.HrAwardVO;
import egovframework.groupware.hr.service.HrCareerVO;
import egovframework.groupware.hr.service.HrEducationVO;
import egovframework.groupware.hr.service.HrService;
import egovframework.groupware.hr.service.HrTrainingVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    public HrProfileController(HrService hrService) { this.hrService = hrService; }

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
}
