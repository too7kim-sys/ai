package egovframework.groupware.attach.service;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AttachVO {
    private Long attachId;
    private Long groupId;
    private String fileNm;
    private String storedNm;
    private Long fileSize;
    private String mimeType;
    private String storageType;
    private String storedPath;
    private String checksum;
    private LocalDateTime createdAt;
    private Long createdBy;
}
