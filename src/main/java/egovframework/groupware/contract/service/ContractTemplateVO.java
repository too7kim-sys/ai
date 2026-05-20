package egovframework.groupware.contract.service;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContractTemplateVO {
    private Long templateId;
    private String templateNm;
    private String contractTypeCd;
    private String bodyHtml;
    private String defaultTermsJson;
    private String useYn;
}
