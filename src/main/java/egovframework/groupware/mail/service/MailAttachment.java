package egovframework.groupware.mail.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MailAttachment {
    private String fileName;
    private byte[] data;
    private String contentType;
}
