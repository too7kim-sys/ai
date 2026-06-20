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
 * 휴가 결재 완료 시 처리.
 *
 * <p>정책: 신청 시점에 LeaveServiceImpl.apply 가 잔여를 임시 차감한다(이중 신청 방지).
 * 따라서 Hook 은 다음과 같이 동작한다.
 * <ul>
 *   <li>승인: 상태만 APPROVED 로 전이 — 잔여는 신청 시 이미 차감</li>
 *   <li>반려: 상태 REJECTED 로 전이 + 잔여 복구 (음수 차감)</li>
 * </ul>
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
            log.info("Leave approved (leaveId={}, days={})", req.getLeaveId(), req.getDays());
        } else {
            leaveMapper.updateRequestStatus(req.getLeaveId(), "REJECTED", null);
            String t = req.getLeaveTypeCd();
            if ("ANNUAL".equals(t) || "HALF".equals(t) || "HOURLY".equals(t)) {
                int year = req.getStartDt() != null
                        ? req.getStartDt().getYear() : Year.now().getValue();
                leaveMapper.addUsed(req.getUserId(), year, req.getDays().negate());
                log.info("Leave rejected — balance restored (leaveId={}, days=-{})",
                        req.getLeaveId(), req.getDays());
            }
        }
    }
}
