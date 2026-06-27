package egovframework.groupware.mail.service;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MailLogVO {
    private Long mailId;
    private String toEmail;
    private String ccEmail;
    private String bccEmail;
    private String subject;
    private String bodyPreview;
    private String templateCd;
    private String attachInfoJson;
    private String relatedEntity;
    private String relatedId;
    private String statusCd;
    private LocalDateTime queuedAt;
    private LocalDateTime sentAt;
    private String errorMessage;
    private Integer retryCnt;
}
