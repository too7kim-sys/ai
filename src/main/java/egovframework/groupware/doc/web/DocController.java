package egovframework.groupware.doc.web;

import egovframework.groupware.attach.service.AttachService;
import egovframework.groupware.attach.service.AttachVO;
import egovframework.groupware.auth.security.CustomUserDetails;
import egovframework.groupware.doc.service.DocFileVO;
import egovframework.groupware.doc.service.DocFolderVO;
import egovframework.groupware.doc.service.DocService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Controller
public class DocController {

    private final DocService service;
    private final AttachService attachService;

    public DocController(DocService service, AttachService attachService) {
        this.service = service;
        this.attachService = attachService;
    }

    @GetMapping("/doc/list.do")
    public String list(@AuthenticationPrincipal CustomUserDetails me,
                       @RequestParam(required = false) Long folderId,
                       @RequestParam(required = false) String keyword,
                       Model model) {
        List<DocFolderVO> folders = service.findAccessible(me.getUserId(), me.getDeptId());
        model.addAttribute("folders", folders);
        if (folderId == null && !folders.isEmpty()) {
            folderId = folders.get(0).getFolderId();
        }
        if (folderId != null) {
            model.addAttribute("folder", service.findFolder(folderId));
            model.addAttribute("files", service.listFiles(folderId, keyword));
        }
        model.addAttribute("folderId", folderId);
        model.addAttribute("keyword", keyword);
        return "doc/list";
    }

    @GetMapping("/doc/search.do")
    public String search(@AuthenticationPrincipal CustomUserDetails me,
                         @RequestParam(required = false) String keyword,
                         Model model) {
        model.addAttribute("keyword", keyword);
        model.addAttribute("results",
                service.searchAll(me.getUserId(), me.getDeptId(), keyword, 200));
        return "doc/search";
    }

    @PostMapping("/doc/folder/create.do")
    public String createFolder(@AuthenticationPrincipal CustomUserDetails me,
                               @RequestParam String folderNm,
                               @RequestParam(required = false) Long parentId,
                               @RequestParam(required = false) String accessScope,
                               @RequestParam(required = false) String description) {
        DocFolderVO vo = new DocFolderVO();
        vo.setFolderNm(folderNm);
        vo.setParentId(parentId);
        vo.setAccessScope(accessScope);
        vo.setDescription(description);
        if ("DEPT".equals(accessScope)) vo.setDeptId(me.getDeptId());
        Long newId = service.createFolder(vo, me.getUserId());
        return "redirect:/doc/list.do?folderId=" + newId;
    }

    @PostMapping("/doc/folder/delete.do")
    public String deleteFolder(@AuthenticationPrincipal CustomUserDetails me,
                               @RequestParam Long folderId) {
        service.deleteFolder(folderId, me.getUserId(), me.getRoleCd());
        return "redirect:/doc/list.do";
    }

    @PostMapping("/doc/upload.do")
    public String upload(@AuthenticationPrincipal CustomUserDetails me,
                         @RequestParam Long folderId,
                         @RequestParam(value = "title", required = false) String title,
                         @RequestParam(value = "description", required = false) String description,
                         @RequestParam("file") MultipartFile file) {
        service.uploadFile(folderId, title, description, file, me.getUserId());
        return "redirect:/doc/list.do?folderId=" + folderId;
    }

    @PostMapping("/doc/delete.do")
    public String deleteFile(@AuthenticationPrincipal CustomUserDetails me,
                             @RequestParam Long docId,
                             @RequestParam Long folderId) {
        service.deleteFile(docId, me.getUserId(), me.getRoleCd());
        return "redirect:/doc/list.do?folderId=" + folderId;
    }

    @GetMapping("/doc/download.do")
    public void download(@AuthenticationPrincipal CustomUserDetails me,
                         @RequestParam Long docId, HttpServletResponse resp) throws IOException {
        DocFileVO doc = service.findFile(docId);
        if (doc == null) { resp.sendError(404); return; }
        // 자료실 폴더 접근 권한 검증 — docId 열거로 부서 제한 폴더의 문서를 받는 것을 차단.
        if (!service.canAccessFile(docId, me.getUserId(), me.getDeptId())) {
            resp.sendError(403);
            return;
        }
        AttachVO att = attachService.findById(doc.getAttachId());
        if (att == null) { resp.sendError(404); return; }
        service.incrementDownload(docId);
        streamAttachment(resp, att, attachService.resolvePath(att));
    }

    /** Tomcat 7 호환을 위해 HttpServletResponse 에 직접 스트림. */
    public static void streamAttachment(HttpServletResponse resp, AttachVO att, Path path) throws IOException {
        String fn = URLEncoder.encode(att.getFileNm(), StandardCharsets.UTF_8).replace("+", "%20");
        resp.setStatus(200);
        resp.setContentType(safeContentType(att.getMimeType()));
        // 브라우저 MIME 스니핑으로 인한 다운로드 첨부의 스크립트 실행 방지.
        resp.setHeader("X-Content-Type-Options", "nosniff");
        resp.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fn);
        resp.setContentLength((int) Math.min(Integer.MAX_VALUE, att.getFileSize() == null ? 0 : att.getFileSize()));
        Files.copy(path, resp.getOutputStream());
        resp.getOutputStream().flush();
    }

    /** HTML/SVG 등 브라우저가 렌더링·실행할 수 있는 타입은 octet-stream 으로 강제. */
    private static String safeContentType(String mime) {
        if (mime == null || mime.isBlank()) return "application/octet-stream";
        String m = mime.toLowerCase();
        if (m.contains("html") || m.contains("svg") || m.contains("xml")
                || m.contains("javascript")) {
            return "application/octet-stream";
        }
        return mime;
    }
}
