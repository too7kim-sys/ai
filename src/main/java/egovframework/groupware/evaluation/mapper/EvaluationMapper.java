package egovframework.groupware.evaluation.mapper;

import egovframework.groupware.evaluation.service.EvalFormVO;
import egovframework.groupware.evaluation.service.EvalPeriodVO;
import egovframework.groupware.evaluation.service.EvalVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EvaluationMapper {

    /* 평가 기간 */
    int insertPeriod(EvalPeriodVO vo);
    int updatePeriodStatus(@Param("periodId") Long periodId, @Param("statusCd") String statusCd);
    EvalPeriodVO findPeriod(@Param("periodId") Long periodId);
    EvalPeriodVO findActivePeriod();
    List<EvalPeriodVO> listPeriods();

    /* 평가 양식 */
    int insertForm(EvalFormVO vo);
    EvalFormVO findForm(@Param("formId") Long formId);
    List<EvalFormVO> listForms();

    /* 평가 결과 */
    int insertEval(EvalVO vo);
    int updateEval(EvalVO vo);
    EvalVO findEval(@Param("evalId") Long evalId);
    EvalVO findByPeriodAndPair(@Param("periodId") Long periodId,
                               @Param("evaluateeId") Long evaluateeId,
                               @Param("evaluatorId") Long evaluatorId);
    List<EvalVO> listByPeriod(@Param("periodId") Long periodId);
    List<EvalVO> listByEvaluator(@Param("periodId") Long periodId,
                                 @Param("evaluatorId") Long evaluatorId);
    List<EvalVO> listByEvaluatee(@Param("evaluateeId") Long evaluateeId);
}
