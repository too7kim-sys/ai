package egovframework.groupware.doc.service;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DocFileVO {
    private Long docId;
    private Long folderId;
    private String folderNm;
    private Long attachId;
    private String fileNm;
    private Long fileSize;
    private String mimeType;
    private String title;
    private String description;
    private Long ownerId;
    private String ownerName;
    private Integer downloadCnt;
    private LocalDateTime createdAt;
}
