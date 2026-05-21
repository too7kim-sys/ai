package egovframework.groupware.sys.service.impl;

import egovframework.groupware.cmm.ApiException;
import egovframework.groupware.sys.mapper.MenuMapper;
import egovframework.groupware.sys.service.MenuService;
import egovframework.groupware.sys.service.MenuVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class MenuServiceImpl implements MenuService {

    private final MenuMapper mapper;

    public MenuServiceImpl(MenuMapper mapper) { this.mapper = mapper; }

    @Override public List<MenuVO> findAll() { return mapper.listMenus(); }
    @Override public MenuVO findById(Long menuId) { return mapper.findMenu(menuId); }
    @Override public List<String> findRoles(Long menuId) {
        return menuId == null ? Collections.emptyList() : mapper.rolesForMenu(menuId);
    }

    @Override
    @Transactional
    public Long save(MenuVO vo, List<String> roles, boolean isNew) {
        if (vo.getMenuNm() == null || vo.getMenuNm().isBlank())
            throw new ApiException("INVALID", "메뉴명을 입력하세요");
        if (isNew) {
            mapper.insert(vo);
        } else {
            mapper.update(vo);
        }
        // 권한 재설정
        mapper.deleteRoles(vo.getMenuId());
        if (roles != null) {
            for (String r : roles) {
                if (r != null && !r.isBlank()) mapper.insertRole(vo.getMenuId(), r);
            }
        }
        return vo.getMenuId();
    }

    @Override
    @Transactional
    public void delete(Long menuId) {
        mapper.deleteRoles(menuId);
        mapper.delete(menuId);
    }
}
