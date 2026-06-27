package egovframework.groupware.evaluation.service;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EvalFormVO {
    private Long formId;
    private String formNm;
    /** [{"k":"comm","l":"커뮤니케이션","w":10},{"k":"...","w":...}] */
    private String itemsJson;
}
