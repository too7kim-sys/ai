package egovframework.groupware.sys.service;

import java.util.List;

public interface CodeService {

    List<CodeGroupVO> findAllGroups();
    CodeGroupVO findGroup(String groupCd);
    void saveGroup(CodeGroupVO vo, boolean isNew);
    void deleteGroup(String groupCd);

    List<CodeVO> findCodes(String groupCd);
    CodeVO findCode(Long codeId);
    Long saveCode(CodeVO vo, boolean isNew);
    void deleteCode(Long codeId);
}
