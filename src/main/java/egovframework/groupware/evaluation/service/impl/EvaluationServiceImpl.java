package egovframework.groupware.evaluation.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import egovframework.groupware.cmm.ApiException;
import egovframework.groupware.evaluation.mapper.EvaluationMapper;
import egovframework.groupware.evaluation.service.EvalFormVO;
import egovframework.groupware.evaluation.service.EvalPeriodVO;
import egovframework.groupware.evaluation.service.EvalVO;
import egovframework.groupware.evaluation.service.EvaluationService;
import egovframework.groupware.user.mapper.UserMapper;
import egovframework.groupware.user.service.UserVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class EvaluationServiceImpl implements EvaluationService {

    private final EvaluationMapper mapper;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public EvaluationServiceImpl(EvaluationMapper mapper, UserMapper userMapper) {
        this.mapper = mapper;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public Long createPeriod(String periodNm, LocalDate startDt, LocalDate endDt) {
        if (periodNm == null || periodNm.isBlank()) throw new ApiException("INVALID", "기간명을 입력하세요");
        if (startDt == null || endDt == null || endDt.isBefore(startDt))
            throw new ApiException("INVALID_DATE", "기간이 올바르지 않습니다");
        EvalPeriodVO vo = new EvalPeriodVO();
        vo.setPeriodNm(periodNm);
        vo.setStartDt(startDt);
        vo.setEndDt(endDt);
        vo.setStatusCd("PLANNED");
        mapper.insertPeriod(vo);
        return vo.getPeriodId();
    }

    @Override
    @Transactional
    public void changePeriodStatus(Long periodId, String statusCd) {
        if (!List.of("PLANNED", "IN_PROGRESS", "CLOSED").contains(statusCd)) {
            throw new ApiException("INVALID", "유효하지 않은 상태입니다");
        }
        mapper.updatePeriodStatus(periodId, statusCd);
    }

    @Override public EvalPeriodVO findPeriod(Long periodId) { return mapper.findPeriod(periodId); }
    @Override public EvalPeriodVO findActivePeriod() { return mapper.findActivePeriod(); }
    @Override public List<EvalPeriodVO> findAllPeriods() { return mapper.listPeriods(); }

    @Override
    @Transactional
    public Long createForm(String formNm, String itemsJson) {
        EvalFormVO vo = new EvalFormVO();
        vo.setFormNm(formNm);
        vo.setItemsJson(itemsJson);
        mapper.insertForm(vo);
        return vo.getFormId();
    }

    @Override public EvalFormVO findForm(Long formId) { return mapper.findForm(formId); }
    @Override public List<EvalFormVO> findAllForms() { return mapper.listForms(); }

    @Override
    @Transactional
    public Long saveEvaluation(Long periodId, Long evaluateeId, Long evaluatorId,
                               Map<String, Integer> itemScores, Map<String, Integer> itemWeights,
                               String comment) {
        if (periodId == null || evaluateeId == null || evaluatorId == null) {
            throw new ApiException("INVALID", "필수 항목 누락");
        }
        EvalPeriodVO period = mapper.findPeriod(periodId);
        if (period == null) throw new ApiException("NOT_FOUND", "평가 기간을 찾을 수 없습니다");
        if ("CLOSED".equals(period.getStatusCd())) {
            throw new ApiException("CLOSED", "마감된 평가 기간은 수정할 수 없습니다");
        }
        if (evaluateeId.equals(evaluatorId)) {
            throw new ApiException("SELF_EVAL", "자기 자신을 평가할 수 없습니다");
        }

        BigDecimal weightedSum = BigDecimal.ZERO;
        BigDecimal totalWeight = BigDecimal.ZERO;
        Map<String, Integer> scores = itemScores == null ? Collections.emptyMap() : itemScores;
        Map<String, Integer> weights = itemWeights == null ? Collections.emptyMap() : itemWeights;
        for (Map.Entry<String, Integer> e : scores.entrySet()) {
            int score = e.getValue() == null ? 0 : e.getValue();
            int weight = weights.getOrDefault(e.getKey(), 1);
            weightedSum = weightedSum.add(BigDecimal.valueOf((long) score * weight));
            totalWeight = totalWeight.add(BigDecimal.valueOf(weight));
        }
        BigDecimal finalScore = totalWeight.signum() == 0
                ? BigDecimal.ZERO
                : weightedSum.divide(totalWeight, 2, RoundingMode.HALF_UP);
        String gradeCd = gradeFor(finalScore);
        String scoresJson;
        try {
            scoresJson = objectMapper.writeValueAsString(scores);
        } catch (Exception ex) {
            scoresJson = "{}";
        }

        EvalVO existing = mapper.findByPeriodAndPair(periodId, evaluateeId, evaluatorId);
        if (existing != null) {
            existing.setScoresJson(scoresJson);
            existing.setFinalScore(finalScore);
            existing.setGradeCd(gradeCd);
            existing.setComment(comment);
            mapper.updateEval(existing);
            return existing.getEvalId();
        }
        EvalVO vo = new EvalVO();
        vo.setPeriodId(periodId);
        vo.setEvaluateeId(evaluateeId);
        vo.setEvaluatorId(evaluatorId);
        vo.setScoresJson(scoresJson);
        vo.setFinalScore(finalScore);
        vo.setGradeCd(gradeCd);
        vo.setComment(comment);
        mapper.insertEval(vo);
        return vo.getEvalId();
    }

    @Override
    public EvalVO findEvaluation(Long evalId) { return mapper.findEval(evalId); }

    @Override
    public EvalVO findOrInit(Long periodId, Long evaluateeId, Long evaluatorId) {
        EvalVO existing = mapper.findByPeriodAndPair(periodId, evaluateeId, evaluatorId);
        if (existing != null) return existing;
        EvalVO empty = new EvalVO();
        empty.setPeriodId(periodId);
        empty.setEvaluateeId(evaluateeId);
        empty.setEvaluatorId(evaluatorId);
        UserVO target = userMapper.findById(evaluateeId);
        if (target != null) {
            empty.setEvaluateeName(target.getName());
            empty.setEvaluateeDept(target.getDeptNm());
        }
        EvalPeriodVO p = mapper.findPeriod(periodId);
        if (p != null) empty.setPeriodNm(p.getPeriodNm());
        return empty;
    }

    @Override
    public List<EvalVO> findMyEvaluations(Long periodId, Long evaluatorId) {
        UserVO me = userMapper.findById(evaluatorId);
        if (me == null) return Collections.emptyList();
        List<UserVO> targets;
        if ("ADMIN".equals(me.getRoleCd()) || "HR_MANAGER".equals(me.getRoleCd())) {
            targets = userMapper.listAll();
        } else if ("MANAGER".equals(me.getRoleCd()) && me.getDeptId() != null) {
            targets = userMapper.listByDept(me.getDeptId());
        } else {
            return Collections.emptyList();
        }
        Map<Long, EvalVO> existingByEvaluatee = new LinkedHashMap<>();
        for (EvalVO e : mapper.listByEvaluator(periodId, evaluatorId)) {
            existingByEvaluatee.put(e.getEvaluateeId(), e);
        }
        List<EvalVO> result = new ArrayList<>();
        for (UserVO t : targets) {
            if (t.getUserId().equals(evaluatorId)) continue;
            EvalVO ev = existingByEvaluatee.get(t.getUserId());
            if (ev == null) {
                ev = new EvalVO();
                ev.setPeriodId(periodId);
                ev.setEvaluateeId(t.getUserId());
                ev.setEvaluatorId(evaluatorId);
                ev.setEvaluateeName(t.getName());
                ev.setEvaluateeDept(t.getDeptNm());
            }
            result.add(ev);
        }
        return result;
    }

    @Override
    public List<EvalVO> findMyResults(Long evaluateeId) {
        return mapper.listByEvaluatee(evaluateeId);
    }

    @Override
    public List<EvalVO> findAllByPeriod(Long periodId) {
        return mapper.listByPeriod(periodId);
    }

    @Override
    public List<Map<String, Object>> parseItems(String itemsJson) {
        if (itemsJson == null || itemsJson.isBlank()) return Collections.emptyList();
        try {
            return objectMapper.readValue(itemsJson, new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    private String gradeFor(BigDecimal score) {
        double s = score.doubleValue();
        if (s >= 4.5) return "S";
        if (s >= 3.5) return "A";
        if (s >= 2.5) return "B";
        if (s >= 1.5) return "C";
        return "D";
    }
}
