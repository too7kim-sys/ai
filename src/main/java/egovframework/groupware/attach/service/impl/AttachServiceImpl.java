package egovframework.groupware.attach.service.impl;

import egovframework.groupware.attach.mapper.AttachMapper;
import egovframework.groupware.attach.service.AttachService;
import egovframework.groupware.attach.service.AttachVO;
import egovframework.groupware.cmm.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
public class AttachServiceImpl implements AttachService, InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(AttachServiceImpl.class);

    /** 다운로드 시 브라우저에서 스크립트로 실행될 수 있는 확장자 — 업로드 차단. */
    private static final Set<String> BLOCKED_EXT = Set.of(
            ".html", ".htm", ".xhtml", ".shtml", ".mhtml", ".svg",
            ".js", ".jsp", ".jspx", ".php", ".phtml", ".asp", ".aspx", ".jar");

    private final AttachMapper mapper;
    private final Path baseDir;

    public AttachServiceImpl(AttachMapper mapper,
                             @Value("${storage.local.path}") String baseDir) {
        this.mapper = mapper;
        this.baseDir = Paths.get(baseDir);
    }

    @Override
    public void afterPropertiesSet() throws IOException {
        Files.createDirectories(baseDir);
        log.info("File storage base directory: {}", baseDir);
    }

    @Override
    @Transactional
    public Long createGroup(String ownerEntity, String ownerEntityId) {
        mapper.insertGroup(ownerEntity, ownerEntityId);
        return mapper.lastGroupId();
    }

    @Override
    @Transactional
    public AttachVO store(Long groupId, MultipartFile file, Long userId) throws IOException {
        if (file == null || file.isEmpty()) return null;
        String origName = Objects.requireNonNullElse(file.getOriginalFilename(), "untitled");
        validateUploadName(origName);
        return persist(groupId, origName, file.getContentType(),
                file.getSize(),
                dest -> file.transferTo(dest.toFile()),
                userId);
    }

    private void validateUploadName(String name) {
        // 더블 확장자 우회(예: shell.php.jpg) 차단 — 모든 dot 이후 토큰을 각각 검사한다.
        // Apache 등 일부 서버 설정에서는 .php.jpg 가 PHP 로 해석될 수 있고, 다운로드
        // 후 사용자가 확장자를 손으로 잘라 실행할 위험도 있다. 안전한 확장자가 마지막에
        // 붙어 있어도 중간 토큰 중 하나라도 위험하면 업로드 거부.
        String lower = name.toLowerCase(java.util.Locale.ROOT);
        String[] parts = lower.split("\\.");
        for (int i = 1; i < parts.length; i++) {
            String ext = "." + parts[i];
            if (BLOCKED_EXT.contains(ext)) {
                throw new ApiException("INVALID_FILE", "허용되지 않는 파일 형식입니다: " + ext);
            }
        }
    }

    @Override
    @Transactional
    public AttachVO storeBytes(Long groupId, String fileNm, String mimeType,
                               byte[] content, Long userId) throws IOException {
        if (content == null || content.length == 0) return null;
        return persist(groupId, fileNm, mimeType, (long) content.length,
                dest -> Files.write(dest, content),
                userId);
    }

    private AttachVO persist(Long groupId, String origName, String mimeType, long size,
                             FileWriter writer, Long userId) throws IOException {
        if (groupId == null) throw new ApiException("INVALID", "그룹이 없습니다");

        String ext = "";
        int dot = origName.lastIndexOf('.');
        if (dot > -1 && dot < origName.length() - 1) ext = origName.substring(dot);

        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        Path dir = baseDir.resolve(datePart);
        Files.createDirectories(dir);

        String stored = UUID.randomUUID().toString().replace("-", "") + ext;
        Path dest = dir.resolve(stored);
        writer.write(dest);

        AttachVO vo = new AttachVO();
        vo.setGroupId(groupId);
        vo.setFileNm(origName);
        vo.setStoredNm(stored);
        vo.setFileSize(size);
        vo.setMimeType(mimeType);
        vo.setStorageType("LOCAL");
        vo.setStoredPath(datePart + "/" + stored);
        vo.setCreatedBy(userId);
        mapper.insert(vo);
        return vo;
    }

    @FunctionalInterface
    private interface FileWriter {
        void write(Path dest) throws IOException;
    }

    @Override public AttachVO findById(Long attachId) { return mapper.findById(attachId); }
    @Override public String findOwnerEntity(Long attachId) { return mapper.findOwnerEntity(attachId); }
    @Override public List<AttachVO> findByGroup(Long groupId) { return mapper.findByGroup(groupId); }

    @Override
    public Path resolvePath(AttachVO vo) {
        if (vo == null || vo.getStoredPath() == null)
            throw new ApiException("NOT_FOUND", "파일이 없습니다");
        return baseDir.resolve(vo.getStoredPath());
    }

    @Override
    @Transactional
    public void delete(Long attachId) throws IOException {
        AttachVO vo = mapper.findById(attachId);
        if (vo == null) return;
        Path p = resolvePath(vo);
        Files.deleteIfExists(p);
        mapper.delete(attachId);
    }
}
