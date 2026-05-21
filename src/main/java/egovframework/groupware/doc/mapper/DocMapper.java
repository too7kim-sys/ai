package egovframework.groupware.doc.mapper;

import egovframework.groupware.doc.service.DocFileVO;
import egovframework.groupware.doc.service.DocFolderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DocMapper {

    /* 폴더 */
    int insertFolder(DocFolderVO vo);
    int updateFolder(DocFolderVO vo);
    int softDeleteFolder(@Param("folderId") Long folderId);

    DocFolderVO findFolder(@Param("folderId") Long folderId);

    /** 접근 가능한 모든 폴더 (트리 좌측 표시용). */
    List<DocFolderVO> listAccessibleFolders(@Param("userId") Long userId,
                                            @Param("deptId") Long deptId);

    /* 파일 */
    int insertFile(DocFileVO vo);
    int softDeleteFile(@Param("docId") Long docId);
    int incrementDownload(@Param("docId") Long docId);

    DocFileVO findFile(@Param("docId") Long docId);

    List<DocFileVO> listFiles(@Param("folderId") Long folderId, @Param("keyword") String keyword);

    /** 검색 (전체 폴더 대상). */
    List<DocFileVO> searchAll(@Param("userId") Long userId,
                              @Param("deptId") Long deptId,
                              @Param("keyword") String keyword,
                              @Param("limit") int limit);

    long countAccessibleFiles(@Param("userId") Long userId, @Param("deptId") Long deptId);
}
