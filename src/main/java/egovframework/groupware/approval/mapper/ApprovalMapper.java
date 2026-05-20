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

    int insertLine(ApprovalLineVO line);
    int updateLineApproval(@Param("lineId") Long lineId,
                           @Param("statusCd") String statusCd,
                           @Param("comment") String comment);

    ApprovalDocVO findDoc(@Param("docId") Long docId);
    List<ApprovalLineVO> findLines(@Param("docId") Long docId);

    List<ApprovalDocVO> listByDrafter(@Param("drafterId") Long drafterId,
                                      @Param("status") String status);
    List<ApprovalDocVO> listPending(@Param("approverId") Long approverId);
    List<ApprovalDocVO> listCompleted(@Param("approverId") Long approverId,
                                      @Param("status") String status);
}
