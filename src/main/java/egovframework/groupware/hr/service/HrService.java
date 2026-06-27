package egovframework.groupware.hr.service;

import java.util.List;

public interface HrService {

    /** 모든 부서를 트리 형태로 반환 (루트만 최상위로 노출, 하위는 children). */
    List<DeptVO> findDeptTree();

    /** 트리를 깊이 정보와 함께 평탄화한 결과. UI에서 들여쓰기 렌더링용. */
    List<DeptFlatVO> findDeptFlat();

    List<DeptVO> findAllDepts();

    List<PositionVO> findAllPositions();

    /* 인사기록 카드 */
    Long createRecord(HrRecordVO vo);
    void deleteRecord(Long recId, Long currentUserId, String currentRoleCd);
    List<HrRecordVO> findRecordsByUser(Long userId);
    List<HrRecordVO> findRecentRecords(int limit);

    /* 인사이력 */
    Long createHistory(HrHistoryVO vo);
    List<HrHistoryVO> findHistoryByUser(Long userId);
    List<HrHistoryVO> findAllHistory(String changeTypeCd, int limit);

    /** 사용자 인사 정보(부서/직책/직급) 변경 + 인사이력에 자동 기록. */
    void applyHrChange(Long targetUserId, Long newDeptId, Long newPositionId, String newRoleCd,
                       java.time.LocalDate effectiveDt, Long actorUserId);

    /**
     * 인사발령 취소(롤백) — 가장 최신 발령에 한해 허용. before_json 의 deptId/positionId/roleCd
     * 로 사용자 정보를 복원하고 이력 행을 삭제한다.
     *
     * @return 롤백 직전 이력 (감사 로그 기록용 — 컨트롤러가 사용)
     * @throws egovframework.groupware.cmm.ApiException
     *         · NOT_FOUND : 해당 이력이 없음
     *         · NOT_LATEST : 그 사용자의 가장 최신 이력이 아님(중간 이력은 롤백 불가)
     *         · UNSUPPORTED : applyHrChange 가 만든 형식(deptId/positionId/roleCd 포함) 이 아닌
     *                         외부 입력 이력(HIRE/TERMINATION 등) 은 롤백 불가
     */
    HrHistoryVO rollbackHistory(Long hisId, Long actorUserId);

    /* 부양가족 */
    Long createFamily(FamilyVO vo);
    void deleteFamily(Long famId, Long currentUserId);
    List<FamilyVO> findFamilyByUser(Long userId);

    /* 경력 */
    Long createCareer(HrCareerVO vo);
    void deleteCareer(Long careerId);
    List<HrCareerVO> findCareerByUser(Long userId);

    /* 학력 */
    Long createEducation(HrEducationVO vo);
    void deleteEducation(Long eduId);
    List<HrEducationVO> findEducationByUser(Long userId);

    /* 교육이수 */
    Long createTraining(HrTrainingVO vo);
    void deleteTraining(Long trnId);
    List<HrTrainingVO> findTrainingByUser(Long userId);

    /* 상벌 */
    Long createAward(HrAwardVO vo);
    void deleteAward(Long awardId);
    List<HrAwardVO> findAwardByUser(Long userId);

    /* 프로젝트 수행 경력 (KOSA 표준 + 첨부) */
    Long createProject(HrProjectVO vo, org.springframework.web.multipart.MultipartFile[] files);

    void updateProject(HrProjectVO vo, org.springframework.web.multipart.MultipartFile[] files);

    void deleteProject(Long projectId);

    HrProjectVO findProject(Long projectId);

    List<HrProjectVO> findProjectByUser(Long userId);

    /** 첨부 다운로드 권한 검증용 — attach group → 소유 프로젝트. */
    HrProjectVO findProjectByAttachGroup(Long groupId);
}
