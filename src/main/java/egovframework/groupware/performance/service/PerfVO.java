package egovframework.groupware.performance.service;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class PerfVO {
    private Long perfId;
    private Long userId;
    private String userName;
    private String deptNm;
    private Long periodId;
    private String periodNm;
    private String goal;
    private String kpi;
    private String unit;
    private BigDecimal targetVal;
    private BigDecimal actualVal;
    private BigDecimal achRate;
    private BigDecimal weight;
    private LocalDateTime createdAt;
}
