package egovframework.groupware.attendance.mapper;

import egovframework.groupware.attendance.service.AttendanceVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface AttendanceMapper {

    int insert(AttendanceVO vo);

    int updateCheckOut(@Param("attId") Long attId,
                       @Param("checkOut") java.time.LocalDateTime checkOut,
                       @Param("workMin") int workMin,
                       @Param("otMin") int otMin,
                       @Param("nightMin") int nightMin,
                       @Param("statusCd") String statusCd);

    AttendanceVO findByUserAndDate(@Param("userId") Long userId, @Param("workDt") LocalDate workDt);

    List<AttendanceVO> listByUserAndMonth(@Param("userId") Long userId,
                                          @Param("from") LocalDate from,
                                          @Param("to") LocalDate to);

    List<AttendanceVO> listByMonth(@Param("from") LocalDate from,
                                   @Param("to") LocalDate to,
                                   @Param("deptId") Long deptId);
}
