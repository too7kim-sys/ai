package egovframework.groupware.leave.mapper;

import egovframework.groupware.leave.service.LeaveBalanceRow;
import egovframework.groupware.leave.service.LeaveBalanceVO;
import egovframework.groupware.leave.service.LeaveRequestVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface LeaveMapper {

    int insertRequest(LeaveRequestVO vo);
    int updateRequestStatus(@Param("leaveId") Long leaveId,
                            @Param("statusCd") String statusCd,
                            @Param("approvalDocId") Long approvalDocId);

    LeaveRequestVO findById(@Param("leaveId") Long leaveId);
    LeaveRequestVO findByApprovalDoc(@Param("docId") Long docId);

    List<LeaveRequestVO> listByUser(@Param("userId") Long userId);

    LeaveBalanceVO findBalance(@Param("userId") Long userId, @Param("year") int year);
    int upsertBalance(@Param("userId") Long userId, @Param("year") int year,
                      @Param("given") BigDecimal given);
    int addUsed(@Param("userId") Long userId, @Param("year") int year,
                @Param("days") BigDecimal days);

    /** 관리자용 — 연도별 전체 활성 사용자 + 해당 연도 잔여를 조회. */
    List<LeaveBalanceRow> listBalancesForYear(@Param("year") int year,
                                              @Param("keyword") String keyword);
}
