package egovframework.groupware.asset.service;

import java.util.List;
import java.util.Map;

public interface AssetService {

    Long create(AssetVO vo, Long actorId);
    void update(AssetVO vo);
    void delete(Long assetId);

    /** 자산을 사용자에게 지급 (IN_STOCK → IN_USE). */
    void assign(Long assetId, Long userId, Long actorId, String memo);

    /** 자산 회수 (IN_USE → IN_STOCK). */
    void doReturn(Long assetId, Long actorId, String memo);

    /** 수리 의뢰 (* → REPAIRING). */
    void repair(Long assetId, Long actorId, String memo);

    /** 폐기 (* → DISPOSED). */
    void dispose(Long assetId, Long actorId, String memo);

    AssetVO findById(Long assetId);
    List<AssetVO> search(String keyword, String categoryCd, String statusCd,
                         Long assignedUserId, int offset, int limit);
    long count(String keyword, String categoryCd, String statusCd, Long assignedUserId);

    List<AssetHistoryVO> findHistory(Long assetId);

    List<Map<String, Object>> statsByStatus();
    List<Map<String, Object>> statsByCategory();
}
