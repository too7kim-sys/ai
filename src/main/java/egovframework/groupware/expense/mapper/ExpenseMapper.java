package egovframework.groupware.expense.mapper;

import egovframework.groupware.expense.service.ExpenseItemVO;
import egovframework.groupware.expense.service.ExpenseReportVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ExpenseMapper {

    int insertReport(ExpenseReportVO vo);
    int updateReport(ExpenseReportVO vo);
    int updateReportStatus(@Param("reportId") Long reportId,
                           @Param("statusCd") String statusCd,
                           @Param("reimbursePaymentId") Long reimbursePaymentId,
                           @Param("reimbursedAt") LocalDateTime reimbursedAt);

    ExpenseReportVO findById(@Param("reportId") Long reportId);
    ExpenseReportVO findByApprovalDoc(@Param("docId") Long docId);

    int deleteItems(@Param("reportId") Long reportId);
    int insertItem(ExpenseItemVO item);
    List<ExpenseItemVO> findItems(@Param("reportId") Long reportId);

    List<ExpenseReportVO> listMine(@Param("userId") Long userId);
    List<ExpenseReportVO> listAdmin(@Param("status") String status,
                                    @Param("deptId") Long deptId);

    /** 부서 예산 USED_AMOUNT 갱신 (계정 ALL 또는 특정 계정에) */
    int addBudgetUsed(@Param("deptId") Long deptId,
                      @Param("accountCd") String accountCd,
                      @Param("year") int year,
                      @Param("month") int month,
                      @Param("amount") BigDecimal amount);
}
