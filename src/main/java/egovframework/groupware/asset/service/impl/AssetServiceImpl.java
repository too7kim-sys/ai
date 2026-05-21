package egovframework.groupware.asset.service.impl;

import egovframework.groupware.asset.mapper.AssetMapper;
import egovframework.groupware.asset.service.AssetHistoryVO;
import egovframework.groupware.asset.service.AssetService;
import egovframework.groupware.asset.service.AssetVO;
import egovframework.groupware.cmm.ApiException;
import egovframework.groupware.notification.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class AssetServiceImpl implements AssetService {

    private final AssetMapper mapper;
    private final NotificationService notificationService;

    public AssetServiceImpl(AssetMapper mapper, NotificationService notificationService) {
        this.mapper = mapper;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public Long create(AssetVO vo, Long actorId) {
        if (vo.getAssetNm() == null || vo.getAssetNm().isBlank())
            throw new ApiException("INVALID", "자산명을 입력하세요");
        if (vo.getCategoryCd() == null || vo.getCategoryCd().isBlank())
            throw new ApiException("INVALID", "분류를 선택하세요");
        if (vo.getAssetNo() == null || vo.getAssetNo().isBlank())
            vo.setAssetNo(generateAssetNo());
        vo.setCreatedBy(actorId);
        mapper.insert(vo);
        return vo.getAssetId();
    }

    @Override
    @Transactional
    public void update(AssetVO vo) {
        AssetVO existing = mapper.findById(vo.getAssetId());
        if (existing == null) throw new ApiException("NOT_FOUND", "자산을 찾을 수 없습니다");
        mapper.update(vo);
    }

    @Override
    @Transactional
    public void delete(Long id) { mapper.softDelete(id); }

    @Override
    @Transactional
    public void assign(Long assetId, Long userId, Long actorId, String memo) {
        AssetVO a = mapper.findById(assetId);
        if (a == null) throw new ApiException("NOT_FOUND", "자산을 찾을 수 없습니다");
        if ("DISPOSED".equals(a.getStatusCd()))
            throw new ApiException("INVALID_STATE", "폐기된 자산은 지급할 수 없습니다");
        String before = a.getStatusCd();
        mapper.updateAssignment(assetId, userId, LocalDate.now(), "IN_USE");
        logHistory(assetId, "ASSIGN", actorId, userId, before, "IN_USE", memo);
        notificationService.notify(userId, "SYSTEM",
                "자산이 지급되었습니다",
                a.getAssetNm() + " (" + a.getAssetNo() + ")",
                "/asset/detail.do?assetId=" + assetId);
    }

    @Override
    @Transactional
    public void doReturn(Long assetId, Long actorId, String memo) {
        AssetVO a = mapper.findById(assetId);
        if (a == null) return;
        String before = a.getStatusCd();
        Long prevUser = a.getAssignedUserId();
        mapper.updateAssignment(assetId, null, null, "IN_STOCK");
        logHistory(assetId, "RETURN", actorId, prevUser, before, "IN_STOCK", memo);
    }

    @Override
    @Transactional
    public void repair(Long assetId, Long actorId, String memo) {
        AssetVO a = mapper.findById(assetId);
        if (a == null) return;
        String before = a.getStatusCd();
        mapper.updateStatus(assetId, "REPAIRING");
        logHistory(assetId, "REPAIR", actorId, a.getAssignedUserId(), before, "REPAIRING", memo);
    }

    @Override
    @Transactional
    public void dispose(Long assetId, Long actorId, String memo) {
        AssetVO a = mapper.findById(assetId);
        if (a == null) return;
        String before = a.getStatusCd();
        mapper.updateAssignment(assetId, null, null, "DISPOSED");
        logHistory(assetId, "DISPOSE", actorId, a.getAssignedUserId(), before, "DISPOSED", memo);
    }

    @Override public AssetVO findById(Long id) { return mapper.findById(id); }

    @Override
    public List<AssetVO> search(String keyword, String category, String status,
                                Long assignedUserId, int offset, int limit) {
        return mapper.search(keyword, category, status, assignedUserId, offset, limit);
    }

    @Override
    public long count(String keyword, String category, String status, Long assignedUserId) {
        return mapper.count(keyword, category, status, assignedUserId);
    }

    @Override public List<AssetHistoryVO> findHistory(Long id) { return mapper.findHistory(id); }
    @Override public List<Map<String, Object>> statsByStatus() { return mapper.countByStatus(); }
    @Override public List<Map<String, Object>> statsByCategory() { return mapper.countByCategory(); }

    private void logHistory(Long assetId, String actionCd, Long actorId, Long targetUserId,
                            String before, String after, String memo) {
        AssetHistoryVO h = new AssetHistoryVO();
        h.setAssetId(assetId);
        h.setActionCd(actionCd);
        h.setActorId(actorId);
        h.setTargetUserId(targetUserId);
        h.setBeforeStatus(before);
        h.setAfterStatus(after);
        h.setMemo(memo);
        mapper.insertHistory(h);
    }

    private String generateAssetNo() {
        return "ASSET-" + String.format("%04d", (System.nanoTime() % 10000));
    }
}
