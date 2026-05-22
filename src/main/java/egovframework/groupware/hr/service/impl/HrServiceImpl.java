package egovframework.groupware.hr.service.impl;

import egovframework.groupware.cmm.ApiException;
import egovframework.groupware.hr.mapper.HrMapper;
import egovframework.groupware.hr.service.DeptFlatVO;
import egovframework.groupware.hr.service.DeptVO;
import egovframework.groupware.hr.service.FamilyVO;
import egovframework.groupware.hr.service.HrHistoryVO;
import egovframework.groupware.hr.service.HrRecordVO;
import egovframework.groupware.hr.service.HrService;
import egovframework.groupware.hr.service.PositionVO;
import egovframework.groupware.user.mapper.UserMapper;
import egovframework.groupware.user.service.UserVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class HrServiceImpl implements HrService {

    private final HrMapper hrMapper;
    private final UserMapper userMapper;

    public HrServiceImpl(HrMapper hrMapper, UserMapper userMapper) {
        this.hrMapper = hrMapper;
        this.userMapper = userMapper;
    }

    @Override
    public List<DeptVO> findDeptTree() {
        List<DeptVO> all = hrMapper.listAllDepts();
        Map<Long, DeptVO> byId = new LinkedHashMap<>();
        for (DeptVO d : all) byId.put(d.getDeptId(), d);
        List<DeptVO> roots = new ArrayList<>();
        for (DeptVO d : all) {
            if (d.getParentId() == null || !byId.containsKey(d.getParentId())) {
                roots.add(d);
            } else {
                byId.get(d.getParentId()).getChildren().add(d);
            }
        }
        return roots;
    }

    @Override
    public List<DeptFlatVO> findDeptFlat() {
        List<DeptFlatVO> out = new ArrayList<>();
        for (DeptVO root : findDeptTree()) flatten(root, 0, out);
        return out;
    }

    private void flatten(DeptVO node, int depth, List<DeptFlatVO> out) {
        out.add(new DeptFlatVO(node, depth));
        for (DeptVO child : node.getChildren()) flatten(child, depth + 1, out);
    }

    @Override public List<DeptVO> findAllDepts() { return hrMapper.listAllDepts(); }
    @Override public List<PositionVO> findAllPositions() { return hrMapper.listAllPositions(); }

    @Override
    @Transactional
    public Long createRecord(HrRecordVO vo) {
        if (vo.getUserId() == null) throw new ApiException("INVALID", "대상자가 필요합니다");
        if (vo.getCategoryCd() == null || vo.getCategoryCd().isBlank()) vo.setCategoryCd("ETC");
        if (vo.getEventDt() == null) vo.setEventDt(LocalDate.now());
        hrMapper.insertRecord(vo);
        return vo.getRecId();
    }

    @Override
    @Transactional
    public void deleteRecord(Long recId, Long currentUserId, String currentRoleCd) {
        if (!"ADMIN".equals(currentRoleCd) && !"HR_MANAGER".equals(currentRoleCd)) {
            throw new ApiException("FORBIDDEN", "인사기록 삭제 권한이 없습니다");
        }
        hrMapper.deleteRecord(recId);
    }

    @Override public List<HrRecordVO> findRecordsByUser(Long userId) { return hrMapper.listRecordsByUser(userId); }
    @Override public List<HrRecordVO> findRecentRecords(int limit) { return hrMapper.listRecentRecords(limit); }

    @Override
    @Transactional
    public Long createHistory(HrHistoryVO vo) {
        if (vo.getEffectiveDt() == null) vo.setEffectiveDt(LocalDate.now());
        hrMapper.insertHistory(vo);
        return vo.getHisId();
    }

    @Override public List<HrHistoryVO> findHistoryByUser(Long userId) {
        return decorateHistory(hrMapper.listHistoryByUser(userId));
    }
    @Override public List<HrHistoryVO> findAllHistory(String changeTypeCd, int limit) {
        return decorateHistory(hrMapper.listAllHistory(changeTypeCd, limit > 0 ? limit : 200));
    }

    /** 각 이력의 before/after JSON 을 사람이 읽을 수 있는 요약 텍스트로 변환한다. */
    private List<HrHistoryVO> decorateHistory(List<HrHistoryVO> list) {
        if (list == null || list.isEmpty()) return list;
        Map<Long, String> deptNm = new java.util.HashMap<>();
        for (DeptVO d : hrMapper.listAllDepts()) deptNm.put(d.getDeptId(), d.getDeptNm());
        Map<Long, String> posNm = new java.util.HashMap<>();
        for (PositionVO p : hrMapper.listAllPositions()) posNm.put(p.getPositionId(), p.getPositionNm());
        for (HrHistoryVO h : list) {
            h.setBeforeText(jsonToText(h.getBeforeJson(), deptNm, posNm));
            h.setAfterText(jsonToText(h.getAfterJson(), deptNm, posNm));
        }
        return list;
    }

    private static final Map<String, String> ROLE_LABEL = Map.of(
            "ADMIN", "시스템 관리자", "HR_MANAGER", "인사 관리자",
            "FINANCE_MANAGER", "재무 관리자", "MANAGER", "부서 매니저",
            "EMPLOYEE", "일반 직원");

    /** {"deptId":6,"positionId":1,"roleCd":"EMPLOYEE"} → "개발1팀 · 사원 · 일반 직원" */
    private String jsonToText(String json, Map<Long, String> deptNm, Map<Long, String> posNm) {
        if (json == null || json.isBlank() || json.trim().equals("{}")) return "-";
        try {
            com.fasterxml.jackson.databind.JsonNode node =
                    new com.fasterxml.jackson.databind.ObjectMapper().readTree(json);
            List<String> parts = new ArrayList<>();
            if (node.hasNonNull("deptId")) {
                long id = node.get("deptId").asLong();
                parts.add(deptNm.getOrDefault(id, "부서#" + id));
            }
            if (node.hasNonNull("positionId")) {
                long id = node.get("positionId").asLong();
                parts.add(posNm.getOrDefault(id, "직급#" + id));
            }
            if (node.hasNonNull("roleCd")) {
                String r = node.get("roleCd").asText();
                parts.add(ROLE_LABEL.getOrDefault(r, r));
            }
            if (node.hasNonNull("salary")) {
                parts.add(String.format("%,d원", node.get("salary").asLong()));
            }
            return parts.isEmpty() ? "-" : String.join(" · ", parts);
        } catch (Exception e) {
            return "-";
        }
    }

    @Override
    @Transactional
    public void applyHrChange(Long targetUserId, Long newDeptId, Long newPositionId, String newRoleCd,
                              LocalDate effectiveDt, Long actorUserId) {
        UserVO before = userMapper.findById(targetUserId);
        if (before == null) throw new ApiException("NOT_FOUND", "대상자를 찾을 수 없습니다");
        boolean deptChanged = newDeptId != null && !newDeptId.equals(before.getDeptId());
        boolean posChanged = newPositionId != null && !newPositionId.equals(before.getPositionId());
        boolean roleChanged = newRoleCd != null && !newRoleCd.equals(before.getRoleCd());
        if (!deptChanged && !posChanged && !roleChanged) return;

        UserVO update = new UserVO();
        update.setUserId(targetUserId);
        update.setName(before.getName());
        update.setPhone(before.getPhone());
        update.setDeptId(deptChanged ? newDeptId : before.getDeptId());
        update.setPositionId(posChanged ? newPositionId : before.getPositionId());
        update.setRoleCd(roleChanged ? newRoleCd : before.getRoleCd());
        update.setBankCd(before.getBankCd());
        update.setBankAccount(before.getBankAccount());
        userMapper.update(update);

        String changeTypeCd = deptChanged ? "DEPT_CHANGE"
                : posChanged ? "POSITION_CHANGE"
                : "ROLE_CHANGE";
        HrHistoryVO h = new HrHistoryVO();
        h.setUserId(targetUserId);
        h.setChangeTypeCd(changeTypeCd);
        h.setBeforeJson(String.format("{\"deptId\":%s,\"positionId\":%s,\"roleCd\":\"%s\"}",
                before.getDeptId(), before.getPositionId(), nullSafe(before.getRoleCd())));
        h.setAfterJson(String.format("{\"deptId\":%s,\"positionId\":%s,\"roleCd\":\"%s\"}",
                update.getDeptId(), update.getPositionId(), nullSafe(update.getRoleCd())));
        h.setEffectiveDt(effectiveDt != null ? effectiveDt : LocalDate.now());
        h.setCreatedBy(actorUserId);
        hrMapper.insertHistory(h);
    }

    @Override
    @Transactional
    public Long createFamily(FamilyVO vo) {
        if (vo.getUserId() == null || vo.getName() == null || vo.getName().isBlank()) {
            throw new ApiException("INVALID", "사용자/이름은 필수입니다");
        }
        if (vo.getRelationCd() == null || vo.getRelationCd().isBlank()) vo.setRelationCd("OTHER");
        if (vo.getDependentYn() == null) vo.setDependentYn("Y");
        if (vo.getElderlyYn() == null) vo.setElderlyYn("N");
        if (vo.getDisabledYn() == null) vo.setDisabledYn("N");
        hrMapper.insertFamily(vo);
        return vo.getFamId();
    }

    @Override
    @Transactional
    public void deleteFamily(Long famId, Long currentUserId) {
        FamilyVO f = hrMapper.findFamily(famId);
        if (f == null) return;
        if (!f.getUserId().equals(currentUserId)) {
            throw new ApiException("FORBIDDEN", "본인 부양가족만 삭제할 수 있습니다");
        }
        hrMapper.deleteFamily(famId);
    }

    @Override public List<FamilyVO> findFamilyByUser(Long userId) { return hrMapper.listFamilyByUser(userId); }

    private String nullSafe(String s) { return s == null ? "" : s; }
}
