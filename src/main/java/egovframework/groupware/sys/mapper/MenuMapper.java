package egovframework.groupware.sys.mapper;

import egovframework.groupware.sys.service.MenuVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MenuMapper {

    List<MenuVO> listMenus();
    MenuVO findMenu(@Param("menuId") Long menuId);
    int insert(MenuVO vo);
    int update(MenuVO vo);
    int delete(@Param("menuId") Long menuId);

    /* 메뉴 권한 매핑 */
    List<String> rolesForMenu(@Param("menuId") Long menuId);
    int deleteRoles(@Param("menuId") Long menuId);
    int insertRole(@Param("menuId") Long menuId, @Param("roleCd") String roleCd);
}
