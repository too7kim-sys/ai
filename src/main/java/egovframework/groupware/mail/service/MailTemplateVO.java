package egovframework.groupware.mail.service;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MailTemplateVO {
    private Long templateId;
    private String templateCd;
    private String templateNm;
    private String subject;
    private String bodyHtml;
    private String attachTypeCd;
    private String useYn;
}
