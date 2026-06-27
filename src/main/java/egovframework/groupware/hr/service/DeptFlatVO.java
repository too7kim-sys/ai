package egovframework.groupware.hr.service;

import lombok.Getter;

@Getter
public class DeptFlatVO {
    private final DeptVO dept;
    private final int depth;

    public DeptFlatVO(DeptVO dept, int depth) {
        this.dept = dept;
        this.depth = depth;
    }

    public Long getDeptId() { return dept.getDeptId(); }
    public String getDeptNm() { return dept.getDeptNm(); }
    public int getHeadcount() { return dept.getHeadcount(); }
    public String getManagerName() { return dept.getManagerName(); }
}
