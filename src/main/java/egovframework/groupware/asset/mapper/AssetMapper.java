package egovframework.groupware.asset.mapper;

import egovframework.groupware.asset.service.AssetHistoryVO;
import egovframework.groupware.asset.service.AssetVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface AssetMapper {

    /* 자산 */
    int insert(AssetVO vo);
    int update(AssetVO vo);
    int softDelete(@Param("assetId") Long id);

    int updateAssignment(@Param("assetId") Long id,
                         @Param("userId") Long userId,
                         @Param("assignedDt") LocalDate assignedDt,
                         @Param("statusCd") String statusCd);

    int updateStatus(@Param("assetId") Long id, @Param("statusCd") String statusCd);

    AssetVO findById(@Param("assetId") Long id);

    List<AssetVO> search(@Param("keyword") String keyword,
                         @Param("categoryCd") String categoryCd,
                         @Param("statusCd") String statusCd,
                         @Param("assignedUserId") Long assignedUserId,
                         @Param("offset") int offset,
                         @Param("limit") int limit);

    long count(@Param("keyword") String keyword,
               @Param("categoryCd") String categoryCd,
               @Param("statusCd") String statusCd,
               @Param("assignedUserId") Long assignedUserId);

    /* 자산 이력 */
    int insertHistory(AssetHistoryVO h);
    List<AssetHistoryVO> findHistory(@Param("assetId") Long assetId);

    /* 통계 */
    List<Map<String, Object>> countByStatus();
    List<Map<String, Object>> countByCategory();
}
