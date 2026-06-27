package egovframework.groupware.approval.service;

import egovframework.groupware.cmm.BaseVO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ApprovalDocVO extends BaseVO {
    private Long docId;
    private String docNo;
    private Long formId;
    private String formCd;
    private String formNm;
    private String title;
    private String contentJson;
    private Long drafterId;
    private String drafterName;
    private String drafterDept;
    /** DRAFT | IN_PROGRESS | APPROVED | REJECTED | CANCELED */
    private String statusCd;
    private LocalDateTime submittedAt;
    private LocalDateTime completedAt;

    private List<ApprovalLineVO> lines = new ArrayList<>();
}
