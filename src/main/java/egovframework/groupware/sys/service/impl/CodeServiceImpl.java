package egovframework.groupware.sys.service.impl;

import egovframework.groupware.cmm.ApiException;
import egovframework.groupware.sys.mapper.CodeMapper;
import egovframework.groupware.sys.service.CodeGroupVO;
import egovframework.groupware.sys.service.CodeService;
import egovframework.groupware.sys.service.CodeVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CodeServiceImpl implements CodeService {

    private final CodeMapper mapper;

    public CodeServiceImpl(CodeMapper mapper) { this.mapper = mapper; }

    @Override public List<CodeGroupVO> findAllGroups() { return mapper.listGroups(); }
    @Override public CodeGroupVO findGroup(String groupCd) { return mapper.findGroup(groupCd); }

    @Override
    @Transactional
    public void saveGroup(CodeGroupVO vo, boolean isNew) {
        if (vo.getGroupCd() == null || vo.getGroupCd().isBlank())
            throw new ApiException("INVALID", "그룹 코드를 입력하세요");
        if (vo.getGroupNm() == null || vo.getGroupNm().isBlank())
            throw new ApiException("INVALID", "그룹 이름을 입력하세요");
        if (isNew) {
            if (mapper.findGroup(vo.getGroupCd()) != null)
                throw new ApiException("DUPLICATE", "이미 존재하는 그룹 코드입니다");
            mapper.insertGroup(vo);
        } else {
            mapper.updateGroup(vo);
        }
    }

    @Override
    @Transactional
    public void deleteGroup(String groupCd) {
        if (!mapper.listCodes(groupCd).isEmpty())
            throw new ApiException("HAS_CHILDREN", "하위 코드가 존재하여 삭제할 수 없습니다");
        mapper.deleteGroup(groupCd);
    }

    @Override public List<CodeVO> findCodes(String groupCd) { return mapper.listCodes(groupCd); }
    @Override public CodeVO findCode(Long codeId) { return mapper.findCode(codeId); }

    @Override
    @Transactional
    public Long saveCode(CodeVO vo, boolean isNew) {
        if (vo.getCodeVal() == null || vo.getCodeVal().isBlank())
            throw new ApiException("INVALID", "코드 값을 입력하세요");
        if (vo.getCodeNm() == null || vo.getCodeNm().isBlank())
            throw new ApiException("INVALID", "코드 이름을 입력하세요");
        if (isNew) {
            mapper.insertCode(vo);
            return vo.getCodeId();
        }
        mapper.updateCode(vo);
        return vo.getCodeId();
    }

    @Override
    @Transactional
    public void deleteCode(Long codeId) { mapper.deleteCode(codeId); }
}
