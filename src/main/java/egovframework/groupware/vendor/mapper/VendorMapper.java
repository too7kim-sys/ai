package egovframework.groupware.vendor.mapper;

import egovframework.groupware.vendor.service.VendorVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VendorMapper {

    int insert(VendorVO vo);

    int update(VendorVO vo);

    VendorVO findById(@Param("vendorId") Long vendorId);

    VendorVO findByBizNo(@Param("bizNo") String bizNo);

    List<VendorVO> list(@Param("keyword") String keyword,
                        @Param("type") String type);
}
