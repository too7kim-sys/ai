package egovframework.groupware.sys.service;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CodeVO {
    private Long codeId;
    private String groupCd;
    private String groupNm;
    private String codeVal;
    private String codeNm;
    private Integer sortNo;
    private String useYn;
    private String extraVal;
}
