package egovframework.groupware.approval.mapper;

import egovframework.groupware.approval.service.ApprovalDocVO;
import egovframework.groupware.approval.service.ApprovalFormVO;
import egovframework.groupware.approval.service.ApprovalLineVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ApprovalMapper {

    List<ApprovalFormVO> listForms();
    ApprovalFormVO findForm(@Param("formCd") String formCd);

    int insertDoc(ApprovalDocVO vo);
    int updateDocStatus(@Param("docId") Long docId,
                        @Param("statusCd") String statusCd);
    int submit(@Param("docId") Long docId);
    int complete(@Param("docId") Long docId,
                 @Param("statusCd") String statusCd);

    /** 회수 — 단일 SQL 로 race-free. 이미 승인된 단계가 하나라도 있으면 0 rows. */
    int cancelDoc(@Param("docId") Long docId,
                  @Param("requesterId") Long requesterId);

    int insertLine(ApprovalLineVO line);
    int updateLineApproval(@Param("lineId") Long lineId,
                           @Param("statusCd") String statusCd,
                           @Param("comment") String comment);

    /** 반려/회수 후 잔여 PENDING 라인을 일괄 SKIPPED 처리 (UI 잔존 표시 정리). */
    int skipPendingLines(@Param("docId") Long docId);

    /** 문서에 등록된 결재선 라인 수 (submit 시 검증용). */
    int countLines(@Param("docId") Long docId);

    ApprovalDocVO findDoc(@Param("docId") Long docId);
    List<ApprovalLineVO> findLines(@Param("docId") Long docId);

    List<ApprovalDocVO> listByDrafter(@Param("drafterId") Long drafterId,
                                      @Param("status") String status);
    List<ApprovalDocVO> listPending(@Param("approverId") Long approverId);
    List<ApprovalDocVO> listCompleted(@Param("approverId") Long approverId,
                                      @Param("status") String status);
}
