package egovframework.groupware.user.service;

import java.util.List;

public interface UserService {

    UserVO findByEmail(String email);

    UserVO findById(Long userId);

    List<UserVO> search(String keyword, Long deptId, int offset, int limit);

    long count(String keyword, Long deptId);

    Long createUser(UserVO vo, String rawPassword);

    void recordLoginSuccess(Long userId, String ip);

    void recordLoginFailure(String email);

    void unlock(Long userId);

    /** 비밀번호를 새 평문으로 초기화 (해시 적용). */
    void resetPassword(Long userId, String rawPassword);

    /** Y/N. N이면 로그인 불가. */
    void setUseYn(Long userId, String useYn);

    /** 역할 코드 변경 (예: EMPLOYEE → MANAGER). */
    void setRole(Long userId, String roleCd);

    /** 이메일(로그인 ID) 변경. 본인 정보 수정에서 사용. 중복 검사는 호출자 책임. */
    void changeEmail(Long userId, String newEmail);

    List<UserVO> listByDept(Long deptId);

    List<UserVO> listAll();

    /**
     * 사용자 프로필 일괄 UPDATE — UPDATE SQL 은 다음 컬럼을 모두 unconditional 로 덮어쓴다:
     * name, phone, deptId, positionId, roleCd, hireDate, resignDate, resignReason,
     * bankCd, bankAccount.
     *
     * <p>따라서 호출자는 변경하지 않을 필드도 반드시 기존 값(예: {@code before.getHireDate()})
     * 으로 채워서 넘겨야 한다. 누락하면 해당 컬럼이 NULL 로 덮어써지면서 휴가일수 화면의 입사일,
     * 급여 일할계산, 퇴직금 기준일 등 파생 데이터가 모두 깨진다.</p>
     *
     * <p>email/password/roleCd 만 바꾸려면 각각 {@link #changeEmail}, {@link #updatePassword},
     * {@link #updateRole} 의 부분 UPDATE 를 사용한다.</p>
     */
    void update(UserVO vo);
}
