package egovframework.groupware.hr.mapper;

import egovframework.groupware.hr.service.DeptVO;
import egovframework.groupware.hr.service.FamilyVO;
import egovframework.groupware.hr.service.HrHistoryVO;
import egovframework.groupware.hr.service.HrRecordVO;
import egovframework.groupware.hr.service.PositionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HrMapper {

    /* 부서 */
    List<DeptVO> listAllDepts();
    DeptVO findDept(@Param("deptId") Long deptId);

    /* 직급 */
    List<PositionVO> listAllPositions();

    /* 인사기록 카드 */
    int insertRecord(HrRecordVO vo);
    int deleteRecord(@Param("recId") Long recId);
    List<HrRecordVO> listRecordsByUser(@Param("userId") Long userId);
    List<HrRecordVO> listRecentRecords(@Param("limit") int limit);

    /* 인사이력 */
    int insertHistory(HrHistoryVO vo);
    List<HrHistoryVO> listHistoryByUser(@Param("userId") Long userId);
    List<HrHistoryVO> listAllHistory(@Param("changeTypeCd") String changeTypeCd,
                                     @Param("limit") int limit);

    /* 부양가족 */
    int insertFamily(FamilyVO vo);
    int deleteFamily(@Param("famId") Long famId);
    List<FamilyVO> listFamilyByUser(@Param("userId") Long userId);
    FamilyVO findFamily(@Param("famId") Long famId);
}
