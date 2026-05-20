package egovframework.groupware.leave.service.impl;

import egovframework.groupware.approval.service.ApprovalCompletionHook;
import egovframework.groupware.approval.service.ApprovalDocVO;
import egovframework.groupware.leave.mapper.LeaveMapper;
import egovframework.groupware.leave.service.LeaveRequestVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Year;

/**
 * 휴가 결재 완료 시 처리:
 * - 승인: LeaveRequest 상태 APPROVED + 연차 사용 차감
 * - 반려: LeaveRequest 상태 REJECTED
 */
@Component
public class LeaveApprovalHook implements ApprovalCompletionHook {

    private static final Logger log = LoggerFactory.getLogger(LeaveApprovalHook.class);

    private final LeaveMapper leaveMapper;

    public LeaveApprovalHook(LeaveMapper leaveMapper) { this.leaveMapper = leaveMapper; }

    @Override public String supportedFormCd() { return "LEAVE"; }

    @Override
    public void onCompleted(ApprovalDocVO doc, boolean approved) {
        LeaveRequestVO req = leaveMapper.findByApprovalDoc(doc.getDocId());
        if (req == null) {
            log.warn("Leave request not found for approval doc {}", doc.getDocId());
            return;
        }
        if (approved) {
            leaveMapper.updateRequestStatus(req.getLeaveId(), "APPROVED", null);
            if ("ANNUAL".equals(req.getLeaveTypeCd()) || "HALF".equals(req.getLeaveTypeCd())) {
                leaveMapper.addUsed(req.getUserId(), Year.now().getValue(), req.getDays());
            }
            log.info("Leave approved (leaveId={}, days={})", req.getLeaveId(), req.getDays());
        } else {
            leaveMapper.updateRequestStatus(req.getLeaveId(), "REJECTED", null);
        }
    }
}
