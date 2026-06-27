package egovframework.groupware.sys.service;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MenuVO {
    private Long menuId;
    private String menuNm;
    private String url;
    private Long parentId;
    private String parentNm;
    private Integer sortNo;
    private String icon;
    private String useYn;
    /** 쉼표 구분 ROLE_CD 목록 (조회용). */
    private String roleCsv;
    /** 폼 바인딩용 역할 코드 배열. */
    private List<String> roles;
}
