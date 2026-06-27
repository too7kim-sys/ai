package egovframework.groupware.evaluation.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface EvaluationService {

    /* 평가 기간 */
    Long createPeriod(String periodNm, LocalDate startDt, LocalDate endDt);
    void changePeriodStatus(Long periodId, String statusCd);
    EvalPeriodVO findPeriod(Long periodId);
    EvalPeriodVO findActivePeriod();
    List<EvalPeriodVO> findAllPeriods();

    /* 평가 양식 */
    Long createForm(String formNm, String itemsJson);
    EvalFormVO findForm(Long formId);
    List<EvalFormVO> findAllForms();

    /* 평가 결과 (자동 점수/등급 계산) */
    Long saveEvaluation(Long periodId, Long evaluateeId, Long evaluatorId,
                        Map<String, Integer> itemScores, Map<String, Integer> itemWeights,
                        String comment);
    EvalVO findEvaluation(Long evalId);
    EvalVO findOrInit(Long periodId, Long evaluateeId, Long evaluatorId);

    /** 평가자(현재 사용자) 기준 본인이 평가해야 할 부하 직원 목록. */
    List<EvalVO> findMyEvaluations(Long periodId, Long evaluatorId);

    /** 피평가자(현재 사용자) 본인의 과거 평가 결과. */
    List<EvalVO> findMyResults(Long evaluateeId);

    /** HR 관리자 전체 평가 결과 (기간 기준). */
    List<EvalVO> findAllByPeriod(Long periodId);

    /** 평가표 항목 JSON 파싱 — UI 렌더링용 List<Map>. */
    List<Map<String, Object>> parseItems(String itemsJson);
}
