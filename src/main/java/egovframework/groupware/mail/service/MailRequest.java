package egovframework.groupware.mail.service;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class MailRequest {
    /** 템플릿 코드 (`GW_MAIL_TEMPLATE.TEMPLATE_CD`) - null이면 subject/body 직접 사용 */
    private String templateCd;
    private String subject;
    private String bodyHtml;
    private List<String> to;
    private List<String> cc;
    private List<String> bcc;
    private Map<String, Object> vars;
    private List<MailAttachment> attachments;
    private String relatedEntity;
    private String relatedId;
}
