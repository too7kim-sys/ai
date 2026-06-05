package egovframework.groupware.hr.service;

import egovframework.groupware.cmm.BaseVO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

/** 개인 프로젝트 수행 경력 (KOSA 표준 + 코사증빙 첨부). */
@Getter
@Setter
public class HrProjectVO extends BaseVO {
    private Long projectId;
    private Long userId;
    private String projectNm;
    private String clientNm;
    private String contractorNm;
    private String roleNm;
    private LocalDate startDt;
    private LocalDate endDt;
    private String techStack;
    private String description;
    /** KOSA_GRADE : BEGINNER / INTERMEDIATE / ADVANCED / SPECIAL */
    private String kosaGradeCd;
    private String kosaGradeNm;
    /** KOSA 신고 완료 여부 (Y/N). 기본 N. */
    private String kosaConfirmedYn;
    private Long attachGroupId;

    /** 첨부 파일 메타 (resultMap 으로 JOIN 채움). */
    private List<egovframework.groupware.attach.service.AttachVO> attachments;
}
