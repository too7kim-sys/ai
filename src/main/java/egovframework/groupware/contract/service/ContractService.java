package egovframework.groupware.contract.service;

import java.util.List;

public interface ContractService {

    List<ContractTemplateVO> listTemplates();

    Long create(EmploymentContractVO vo);

    void update(EmploymentContractVO vo);

    EmploymentContractVO findById(Long contractId);

    List<EmploymentContractVO> listAll(String keyword, String status);

    List<EmploymentContractVO> listByUser(Long userId);

    /** 메일 발송 → SENT 상태 전이. */
    Long sendSignRequest(Long contractId);

    /** 사원 서명 → SIGNED 상태 전이. */
    void sign(Long contractId, Long userId, String signatureBase64);

    /** HR 활성화 → ACTIVE */
    void activate(Long contractId);

    /** 본문 변수 치환된 HTML 미리보기. */
    String renderBody(Long contractId);

    byte[] generatePdf(Long contractId);
}
