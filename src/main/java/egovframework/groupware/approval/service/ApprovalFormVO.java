package egovframework.groupware.approval.service;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApprovalFormVO {
    private Long formId;
    private String formCd;
    private String formNm;
    private String fieldsJson;
    private String useYn;
}
