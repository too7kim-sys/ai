package egovframework.groupware.payroll.mapper;

import egovframework.groupware.payroll.service.BonusVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BonusMapper {

    int insert(BonusVO vo);
    int delete(@Param("bonusId") Long bonusId);
    int updateStatus(@Param("bonusId") Long bonusId, @Param("statusCd") String statusCd);

    BonusVO findById(@Param("bonusId") Long bonusId);

    List<BonusVO> search(@Param("payMonth") String payMonth,
                         @Param("bonusTypeCd") String bonusTypeCd);

    /** 급여 산정용 — 해당 직원·해당 월의 PLANNED 상여. */
    List<BonusVO> findPlannedByUserMonth(@Param("userId") Long userId,
                                         @Param("payMonth") String payMonth);

    /** 급여 반영 완료 시 PLANNED → APPLIED. */
    int markApplied(@Param("userId") Long userId, @Param("payMonth") String payMonth);
}
