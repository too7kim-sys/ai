package egovframework.groupware.sys.service;

import java.util.List;

public interface MenuService {

    List<MenuVO> findAll();
    MenuVO findById(Long menuId);
    Long save(MenuVO vo, List<String> roles, boolean isNew);
    void delete(Long menuId);
    List<String> findRoles(Long menuId);
}
