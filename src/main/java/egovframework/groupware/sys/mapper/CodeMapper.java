package egovframework.groupware.sys.mapper;

import egovframework.groupware.sys.service.CodeGroupVO;
import egovframework.groupware.sys.service.CodeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CodeMapper {

    /* 코드 그룹 */
    List<CodeGroupVO> listGroups();
    CodeGroupVO findGroup(@Param("groupCd") String groupCd);
    int insertGroup(CodeGroupVO vo);
    int updateGroup(CodeGroupVO vo);
    int deleteGroup(@Param("groupCd") String groupCd);

    /* 코드 */
    List<CodeVO> listCodes(@Param("groupCd") String groupCd);
    CodeVO findCode(@Param("codeId") Long codeId);
    int insertCode(CodeVO vo);
    int updateCode(CodeVO vo);
    int deleteCode(@Param("codeId") Long codeId);
}
