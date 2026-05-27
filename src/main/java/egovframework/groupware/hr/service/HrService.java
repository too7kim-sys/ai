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
}
