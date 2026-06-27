package egovframework.groupware.leave.mapper;

import egovframework.groupware.leave.service.LeaveBalanceRow;
import egovframework.groupware.leave.service.LeaveBalanceVO;
import egovframework.groupware.leave.service.LeaveRequestVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    /** 관리자용 — 연도별 전체 활성 사용자 + 해당 연도 잔여를 조회. 퇴직자도 포함(화면 표시용). */
    List<LeaveBalanceRow> listBalancesForYear(@Param("year") int year,
                                              @Param("keyword") String keyword);

    /**
     * 일괄 부여(grant-all / grant-by-tenure) 대상자만 조회 — 해당 연도 시작일 기준
     * 이미 퇴직한 사용자는 제외. 퇴직 예정이지만 그 해에 일부 재직하는 사람은 포함.
     */
    List<LeaveBalanceRow> listGrantTargetsForYear(@Param("year") int year,
                                                  @Param("yearStart") java.time.LocalDate yearStart);

    /** 일자 단위(연차/반차/병가 등) 휴가 신청의 기존 IN_PROGRESS/APPROVED 와 일자 겹침 카운트. */
    int countOverlappingDay(@Param("userId") Long userId,
                            @Param("start") LocalDate start,
                            @Param("end") LocalDate end);

    /** 시간연차의 시각 겹침 카운트. */
    int countOverlappingHourly(@Param("userId") Long userId,
                               @Param("startAt") LocalDateTime startAt,
                               @Param("endAt") LocalDateTime endAt);

    /** 특정 일자에 종일 휴가(IN_PROGRESS/APPROVED) 가 있으면 leave_type_cd 반환.
     *  반차(HALF) / 시간연차(HOURLY) 는 출근 가능한 부분이 남아 있으므로 제외. */
    String findFullDayLeaveTypeOn(@Param("userId") Long userId,
                                  @Param("date") LocalDate date);
}
