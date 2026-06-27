package egovframework.groupware.hr.service;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DeptVO {
    private Long deptId;
    private String deptNm;
    private Long parentId;
    private Integer sortNo;
    private Long managerUserId;
    private String managerName;
    private String useYn;
    private int headcount;

    private List<DeptVO> children = new ArrayList<>();
}
