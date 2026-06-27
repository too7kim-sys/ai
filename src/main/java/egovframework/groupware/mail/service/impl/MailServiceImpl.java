package egovframework.groupware.mail.service.impl;

import egovframework.groupware.mail.mapper.MailMapper;
import egovframework.groupware.mail.service.*;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MailServiceImpl implements MailService {

    private static final Logger log = LoggerFactory.getLogger(MailServiceImpl.class);
    private static final int MAX_RETRY = 3;

    private final MailMapper mailMapper;
    private final JavaMailSender mailSender;
    private final TemplateRenderer renderer;
    private final String fromAddress;

    public MailServiceImpl(MailMapper mailMapper,
                           JavaMailSender mailSender,
                           TemplateRenderer renderer,
                           @Value("${mail.from:noreply@company.com}") String fromAddress) {
        this.mailMapper = mailMapper;
        this.mailSender = mailSender;
        this.renderer = renderer;
        this.fromAddress = fromAddress;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Long enqueue(MailRequest request) {
        Map<String, Object> vars = request.getVars() != null ? new HashMap<>(request.getVars()) : new HashMap<>();
        vars.putIfAbsent("companyName", "사내 그룹웨어");

        String subject = request.getSubject();
        String body = request.getBodyHtml();
        if (request.getTemplateCd() != null) {
            MailTemplateVO tpl = mailMapper.findTemplate(request.getTemplateCd());
            if (tpl != null) {
                if (subject == null) subject = renderer.render(tpl.getSubject(), vars);
                if (body == null) body = renderer.render(tpl.getBodyHtml(), vars);
            }
        }

        MailLogVO logRow = new MailLogVO();
        logRow.setToEmail(String.join(",", request.getTo()));
        if (request.getCc() != null) logRow.setCcEmail(String.join(",", request.getCc()));
        if (request.getBcc() != null) logRow.setBccEmail(String.join(",", request.getBcc()));
        logRow.setSubject(subject);
        logRow.setBodyPreview(truncate(body, 480));
        logRow.setTemplateCd(request.getTemplateCd());
        logRow.setRelatedEntity(request.getRelatedEntity());
        logRow.setRelatedId(request.getRelatedId());
        if (request.getAttachments() != null && !request.getAttachments().isEmpty()) {
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < request.getAttachments().size(); i++) {
                if (i > 0) sb.append(',');
                MailAttachment a = request.getAttachments().get(i);
                sb.append('"').append(a.getFileName()).append('"');
            }
            sb.append(']');
            logRow.setAttachInfoJson(sb.toString());
        }
        mailMapper.insertLog(logRow);

        sendAsync(logRow.getMailId(), request, subject, body);
        return logRow.getMailId();
    }

    @Async("mailExecutor")
    public void sendAsync(Long mailId, MailRequest request, String subject, String body) {
        int retry = 0;
        Exception lastEx = null;
        while (retry < MAX_RETRY) {
            try {
                MimeMessage msg = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
                helper.setFrom(fromAddress);
                helper.setTo(request.getTo().toArray(new String[0]));
                if (request.getCc() != null && !request.getCc().isEmpty())
                    helper.setCc(request.getCc().toArray(new String[0]));
                if (request.getBcc() != null && !request.getBcc().isEmpty())
                    helper.setBcc(request.getBcc().toArray(new String[0]));
                helper.setSubject(subject);
                helper.setText(body, true);
                if (request.getAttachments() != null) {
                    for (MailAttachment a : request.getAttachments()) {
                        helper.addAttachment(a.getFileName(), new ByteArrayResource(a.getData()), a.getContentType());
                    }
                }
                mailSender.send(msg);
                mailMapper.updateLogSent(mailId);
                log.info("Mail sent (mailId={}, subject={})", mailId, subject);
                return;
            } catch (Exception ex) {
                lastEx = ex;
                retry++;
                log.warn("Mail send failed (attempt {}/{}, mailId={}): {}", retry, MAX_RETRY, mailId, ex.getMessage());
                try {
                    Thread.sleep(1000L * (1L << (retry - 1)));
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        mailMapper.updateLogFailed(mailId, lastEx == null ? null : truncate(lastEx.getMessage(), 1900), retry);
    }

    @Override public List<MailTemplateVO> listTemplates() { return mailMapper.listTemplates(); }
    @Override public MailTemplateVO findTemplate(String templateCd) { return mailMapper.findTemplate(templateCd); }
    @Override public void updateTemplate(MailTemplateVO vo) { mailMapper.updateTemplate(vo); }
    @Override public List<MailLogVO> searchLogs(String status, String keyword, int offset, int limit) {
        return mailMapper.searchLogs(status, keyword, offset, limit);
    }
    @Override public long countLogs(String status, String keyword) {
        return mailMapper.countLogs(status, keyword);
    }

    private String truncate(String s, int max) {
        if (s == null) return null;
        return s.length() > max ? s.substring(0, max) : s;
    }
}
