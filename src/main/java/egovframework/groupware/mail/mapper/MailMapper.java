package egovframework.groupware.mail.mapper;

import egovframework.groupware.mail.service.MailLogVO;
import egovframework.groupware.mail.service.MailTemplateVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MailMapper {

    MailTemplateVO findTemplate(@Param("templateCd") String templateCd);

    List<MailTemplateVO> listTemplates();

    int insertLog(MailLogVO log);

    int updateLogSent(@Param("mailId") Long mailId);

    int updateLogFailed(@Param("mailId") Long mailId,
                        @Param("errorMessage") String errorMessage,
                        @Param("retryCnt") int retryCnt);

    List<MailLogVO> searchLogs(@Param("status") String status,
                               @Param("keyword") String keyword,
                               @Param("offset") int offset,
                               @Param("limit") int limit);

    long countLogs(@Param("status") String status,
                   @Param("keyword") String keyword);

    int updateTemplate(MailTemplateVO vo);
}
