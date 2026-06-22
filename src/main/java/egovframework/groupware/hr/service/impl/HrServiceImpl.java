package egovframework.groupware.hr.service.impl;

import egovframework.groupware.attach.service.AttachService;
import egovframework.groupware.cmm.ApiException;
import egovframework.groupware.hr.mapper.HrMapper;
import egovframework.groupware.hr.service.DeptFlatVO;
import egovframework.groupware.hr.service.DeptVO;
import egovframework.groupware.hr.service.FamilyVO;
import egovframework.groupware.hr.service.HrAwardVO;
import egovframework.groupware.hr.service.HrCareerVO;
import egovframework.groupware.hr.service.HrEducationVO;
import egovframework.groupware.hr.service.HrHistoryVO;
import egovframework.groupware.hr.service.HrProjectVO;
import egovframework.groupware.hr.service.HrRecordVO;
import egovframework.groupware.hr.service.HrService;
import egovframework.groupware.hr.service.HrTrainingVO;
import egovframework.groupware.hr.service.PositionVO;
import egovframework.groupware.user.mapper.UserMapper;
import egovframework.groupware.user.service.UserVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class HrServiceImpl implements HrService {

    private final HrMapper hrMapper;
    private final UserMapper userMapper;
    private final AttachService attachService;

    public HrServiceImpl(HrMapper hrMapper, UserMapper userMapper, AttachService attachService) {
        this.hrMapper = hrMapper;
        this.userMapper = userMapper;
        this.attachService = attachService;
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

    @Override
    @Transactional
    public HrHistoryVO rollbackHistory(Long hisId, Long actorUserId) {
        HrHistoryVO target = hrMapper.findHistory(hisId);
        if (target == null) throw new ApiException("NOT_FOUND", "인사이력을 찾을 수 없습니다");

        // 가장 최신 발령만 취소 허용 — 중간 이력을 지우면 사용자의 현재 상태와 이력이 어긋남.
        HrHistoryVO latest = hrMapper.findLatestHistoryByUser(target.getUserId());
        if (latest == null || !latest.getHisId().equals(hisId)) {
            throw new ApiException("NOT_LATEST",
                "가장 최근 발령만 취소할 수 있습니다. 이후 발령을 먼저 취소하세요.");
        }

        // applyHrChange 가 만든 발령만 롤백 가능 — before_json 에 deptId/positionId/roleCd 가
        // 모두 들어있어야 한다. HIRE/TERMINATION 등 수기 입력은 사용자 정보 복원 정보가 없어
        // 자동 롤백 불가.
        com.fasterxml.jackson.databind.JsonNode node;
        try {
            node = new com.fasterxml.jackson.databind.ObjectMapper().readTree(target.getBeforeJson());
        } catch (Exception ex) {
            throw new ApiException("UNSUPPORTED", "이 이력은 자동 취소를 지원하지 않습니다(데이터 형식)");
        }
        if (!node.hasNonNull("deptId") || !node.hasNonNull("positionId") || !node.hasNonNull("roleCd")) {
            throw new ApiException("UNSUPPORTED",
                "이 이력은 자동 취소를 지원하지 않습니다(부서/직급/역할 정보 부재)");
        }

        UserVO before = userMapper.findById(target.getUserId());
        if (before == null) throw new ApiException("NOT_FOUND", "대상자를 찾을 수 없습니다");

        // UserMapper.update 는 모든 컬럼을 덮어쓰므로, 복원하지 않는 필드(이름/연락처/입사일/퇴사일
        // /은행계좌 등) 도 before 값으로 모두 채워야 한다. — 이전 동일 결함 fix 참고.
        UserVO restore = new UserVO();
        restore.setUserId(target.getUserId());
        restore.setName(before.getName());
        restore.setPhone(before.getPhone());
        restore.setDeptId(node.get("deptId").asLong());
        restore.setPositionId(node.get("positionId").asLong());
        restore.setRoleCd(node.get("roleCd").asText());
        restore.setHireDate(before.getHireDate());
        restore.setResignDate(before.getResignDate());
        restore.setResignReason(before.getResignReason());
        restore.setBankCd(before.getBankCd());
        restore.setBankAccount(before.getBankAccount());
        userMapper.update(restore);

        hrMapper.deleteHistory(hisId);
        return target;
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
        // UserMapper.update SQL 은 모든 컬럼을 unconditional 로 덮어쓰므로,
        // 변경 대상이 아닌 필드도 반드시 before 값으로 보존해야 한다.
        // (입사일/퇴사일/퇴사사유 누락 시 NULL 로 덮어써져 휴가일수·급여 일할계산이 깨짐)
        update.setHireDate(before.getHireDate());
        update.setResignDate(before.getResignDate());
        update.setResignReason(before.getResignReason());
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

    /* ===== 경력 ===== */
    @Override @Transactional
    public Long createCareer(HrCareerVO vo) { hrMapper.insertCareer(vo); return vo.getCareerId(); }
    @Override @Transactional
    public void deleteCareer(Long careerId) { hrMapper.deleteCareer(careerId); }
    @Override
    public List<HrCareerVO> findCareerByUser(Long userId) { return hrMapper.listCareerByUser(userId); }

    /* ===== 학력 ===== */
    @Override @Transactional
    public Long createEducation(HrEducationVO vo) { hrMapper.insertEducation(vo); return vo.getEduId(); }
    @Override @Transactional
    public void deleteEducation(Long eduId) { hrMapper.deleteEducation(eduId); }
    @Override
    public List<HrEducationVO> findEducationByUser(Long userId) { return hrMapper.listEducationByUser(userId); }

    /* ===== 교육이수 ===== */
    @Override @Transactional
    public Long createTraining(HrTrainingVO vo) { hrMapper.insertTraining(vo); return vo.getTrnId(); }
    @Override @Transactional
    public void deleteTraining(Long trnId) { hrMapper.deleteTraining(trnId); }
    @Override
    public List<HrTrainingVO> findTrainingByUser(Long userId) { return hrMapper.listTrainingByUser(userId); }

    /* ===== 상벌 ===== */
    @Override @Transactional
    public Long createAward(HrAwardVO vo) { hrMapper.insertAward(vo); return vo.getAwardId(); }
    @Override @Transactional
    public void deleteAward(Long awardId) { hrMapper.deleteAward(awardId); }
    @Override
    public List<HrAwardVO> findAwardByUser(Long userId) { return hrMapper.listAwardByUser(userId); }

    /* ===== 프로젝트 수행 경력 ===== */
    @Override
    @Transactional
    public Long createProject(HrProjectVO vo, MultipartFile[] files) {
        // 첨부가 1개 이상 있으면 attach group 을 미리 만들어 ID 를 연결.
        if (hasFiles(files)) {
            Long groupId = attachService.createGroup("HR_PROJECT", null);
            vo.setAttachGroupId(groupId);
            storeFiles(groupId, files, vo.getCreatedBy());
        }
        hrMapper.insertProject(vo);
        return vo.getProjectId();
    }

    @Override
    @Transactional
    public void updateProject(HrProjectVO vo, MultipartFile[] files) {
        HrProjectVO existing = hrMapper.findProject(vo.getProjectId());
        if (existing == null) throw new ApiException("NOT_FOUND", "프로젝트가 존재하지 않습니다");
        if (hasFiles(files)) {
            Long groupId = existing.getAttachGroupId();
            if (groupId == null) {
                groupId = attachService.createGroup("HR_PROJECT", String.valueOf(vo.getProjectId()));
                vo.setAttachGroupId(groupId);
            }
            storeFiles(groupId, files, vo.getCreatedBy());
        }
        hrMapper.updateProject(vo);
    }

    @Override @Transactional
    public void deleteProject(Long projectId) { hrMapper.deleteProject(projectId); }

    @Override
    public HrProjectVO findProject(Long projectId) {
        HrProjectVO p = hrMapper.findProject(projectId);
        if (p != null && p.getAttachGroupId() != null) {
            p.setAttachments(attachService.findByGroup(p.getAttachGroupId()));
        }
        return p;
    }

    @Override
    public List<HrProjectVO> findProjectByUser(Long userId) {
        List<HrProjectVO> list = hrMapper.listProjectByUser(userId);
        for (HrProjectVO p : list) {
            if (p.getAttachGroupId() != null) {
                p.setAttachments(attachService.findByGroup(p.getAttachGroupId()));
            }
        }
        return list;
    }

    @Override
    public HrProjectVO findProjectByAttachGroup(Long groupId) {
        return hrMapper.findProjectByAttachGroup(groupId);
    }

    private static boolean hasFiles(MultipartFile[] files) {
        if (files == null) return false;
        for (MultipartFile f : files) {
            if (f != null && !f.isEmpty()) return true;
        }
        return false;
    }

    private void storeFiles(Long groupId, MultipartFile[] files, Long userId) {
        for (MultipartFile f : files) {
            if (f == null || f.isEmpty()) continue;
            try {
                attachService.store(groupId, f, userId);
            } catch (IOException ex) {
                throw new ApiException("UPLOAD_FAIL", "첨부 업로드 실패: " + ex.getMessage());
            }
        }
    }

    private String nullSafe(String s) { return s == null ? "" : s; }
}
