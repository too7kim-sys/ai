package egovframework.groupware.attach.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface AttachService {

    /** 새 그룹을 생성한다. 게시글/문서 등에 연결. */
    Long createGroup(String ownerEntity, String ownerEntityId);

    /** 그룹에 파일 1건 업로드. 빈 파일은 무시. */
    AttachVO store(Long groupId, MultipartFile file, Long userId) throws IOException;

    /** 메모리 바이트 배열을 그룹에 직접 저장. (시드/내부 생성용) */
    AttachVO storeBytes(Long groupId, String fileNm, String mimeType,
                        byte[] content, Long userId) throws IOException;

    AttachVO findById(Long attachId);
    List<AttachVO> findByGroup(Long groupId);

    /** 실제 파일 경로 반환 (다운로드용). */
    Path resolvePath(AttachVO vo);

    void delete(Long attachId) throws IOException;
}
