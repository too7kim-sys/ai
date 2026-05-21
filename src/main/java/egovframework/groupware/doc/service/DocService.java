package egovframework.groupware.doc.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocService {

    /* 폴더 */
    Long createFolder(DocFolderVO vo, Long ownerId);
    void updateFolder(DocFolderVO vo, Long actorId, String roleCd);
    void deleteFolder(Long folderId, Long actorId, String roleCd);
    DocFolderVO findFolder(Long folderId);
    List<DocFolderVO> findAccessible(Long userId, Long deptId);

    /* 파일 */
    DocFileVO uploadFile(Long folderId, String title, String description,
                        MultipartFile file, Long ownerId);
    void deleteFile(Long docId, Long actorId, String roleCd);
    DocFileVO findFile(Long docId);
    List<DocFileVO> listFiles(Long folderId, String keyword);
    List<DocFileVO> searchAll(Long userId, Long deptId, String keyword, int limit);
    void incrementDownload(Long docId);
}
