package egovframework.groupware.mail.service;

import java.util.List;

public interface MailService {

    /**
     * 메일을 큐에 등록하고 비동기 발송. MailLog에 즉시 QUEUED 행 생성.
     * 반환 mailId로 발송 결과 추적 가능.
     */
    Long enqueue(MailRequest request);

    List<MailTemplateVO> listTemplates();

    MailTemplateVO findTemplate(String templateCd);

    void updateTemplate(MailTemplateVO vo);

    List<MailLogVO> searchLogs(String status, String keyword, int offset, int limit);

    long countLogs(String status, String keyword);
}
