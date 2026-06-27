package egovframework.groupware.doc.service.impl;

import egovframework.groupware.attach.service.AttachService;
import egovframework.groupware.attach.service.AttachVO;
import egovframework.groupware.cmm.ApiException;
import egovframework.groupware.doc.mapper.DocMapper;
import egovframework.groupware.doc.service.DocFileVO;
import egovframework.groupware.doc.service.DocFolderVO;
import egovframework.groupware.doc.service.DocService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class DocServiceImpl implements DocService {

    private final DocMapper mapper;
    private final AttachService attachService;

    public DocServiceImpl(DocMapper mapper, AttachService attachService) {
        this.mapper = mapper;
        this.attachService = attachService;
    }

    @Override
    @Transactional
    public Long createFolder(DocFolderVO vo, Long ownerId) {
        if (vo.getFolderNm() == null || vo.getFolderNm().isBlank())
            throw new ApiException("INVALID", "폴더명을 입력하세요");
        vo.setOwnerId(ownerId);
        String parentPath = "";
        if (vo.getParentId() != null) {
            DocFolderVO parent = mapper.findFolder(vo.getParentId());
            if (parent != null) parentPath = parent.getFolderPath();
        }
        vo.setFolderPath((parentPath == null ? "" : parentPath) + "/" + vo.getFolderNm());
        mapper.insertFolder(vo);
        return vo.getFolderId();
    }

    @Override
    @Transactional
    public void updateFolder(DocFolderVO vo, Long actorId, String roleCd) {
        DocFolderVO existing = mapper.findFolder(vo.getFolderId());
        if (existing == null) throw new ApiException("NOT_FOUND", "폴더가 없습니다");
        if (!canManageFolder(existing, actorId, roleCd))
            throw new ApiException("FORBIDDEN", "수정 권한이 없습니다");
        mapper.updateFolder(vo);
    }

    @Override
    @Transactional
    public void deleteFolder(Long folderId, Long actorId, String roleCd) {
        DocFolderVO existing = mapper.findFolder(folderId);
        if (existing == null) return;
        if (!canManageFolder(existing, actorId, roleCd))
            throw new ApiException("FORBIDDEN", "삭제 권한이 없습니다");
        mapper.softDeleteFolder(folderId);
    }

    @Override public DocFolderVO findFolder(Long folderId) { return mapper.findFolder(folderId); }

    @Override
    public List<DocFolderVO> findAccessible(Long userId, Long deptId) {
        return mapper.listAccessibleFolders(userId, deptId);
    }

    @Override
    @Transactional
    public DocFileVO uploadFile(Long folderId, String title, String description,
                                MultipartFile file, Long ownerId) {
        if (file == null || file.isEmpty()) throw new ApiException("INVALID", "파일을 선택하세요");
        DocFolderVO folder = mapper.findFolder(folderId);
        if (folder == null) throw new ApiException("NOT_FOUND", "폴더가 없습니다");

        Long groupId = attachService.createGroup("DOC", folderId.toString());
        AttachVO att;
        try { att = attachService.store(groupId, file, ownerId); }
        catch (IOException e) { throw new ApiException("UPLOAD_FAIL", "파일 업로드 실패: " + e.getMessage()); }

        DocFileVO doc = new DocFileVO();
        doc.setFolderId(folderId);
        doc.setAttachId(att.getAttachId());
        doc.setTitle(title == null || title.isBlank() ? att.getFileNm() : title);
        doc.setDescription(description);
        doc.setOwnerId(ownerId);
        mapper.insertFile(doc);
        return mapper.findFile(doc.getDocId());
    }

    @Override
    @Transactional
    public void deleteFile(Long docId, Long actorId, String roleCd) {
        DocFileVO existing = mapper.findFile(docId);
        if (existing == null) return;
        if (!canManageFile(existing, actorId, roleCd))
            throw new ApiException("FORBIDDEN", "삭제 권한이 없습니다");
        mapper.softDeleteFile(docId);
    }

    @Override public DocFileVO findFile(Long docId) { return mapper.findFile(docId); }

    @Override
    public boolean canAccessFile(Long docId, Long userId, Long deptId) {
        DocFileVO doc = mapper.findFile(docId);
        if (doc == null) return false;
        return findAccessible(userId, deptId).stream()
                .anyMatch(f -> f.getFolderId() != null
                        && f.getFolderId().equals(doc.getFolderId()));
    }

    @Override public List<DocFileVO> listFiles(Long folderId, String keyword) {
        return mapper.listFiles(folderId, keyword);
    }

    @Override public List<DocFileVO> searchAll(Long userId, Long deptId, String keyword, int limit) {
        return mapper.searchAll(userId, deptId, keyword, limit);
    }

    @Override
    @Transactional
    public void incrementDownload(Long docId) { mapper.incrementDownload(docId); }

    private boolean canManageFolder(DocFolderVO folder, Long actorId, String roleCd) {
        if ("ADMIN".equals(roleCd) || "HR_MANAGER".equals(roleCd)) return true;
        return folder.getOwnerId().equals(actorId);
    }

    private boolean canManageFile(DocFileVO file, Long actorId, String roleCd) {
        if ("ADMIN".equals(roleCd) || "HR_MANAGER".equals(roleCd)) return true;
        return file.getOwnerId().equals(actorId);
    }
}
