package egovframework.groupware.doc.service;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DocFolderVO {
    private Long folderId;
    private Long parentId;
    private String folderNm;
    private String folderPath;
    /** PRIVATE | DEPT | COMPANY */
    private String accessScope;
    private Long deptId;
    private String deptNm;
    private Long ownerId;
    private String ownerName;
    private String description;
    private LocalDateTime createdAt;
}
