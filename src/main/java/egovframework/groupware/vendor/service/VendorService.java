package egovframework.groupware.vendor.service;

import java.util.List;

public interface VendorService {
    Long create(VendorVO vo);
    void update(VendorVO vo);
    VendorVO findById(Long vendorId);
    VendorVO findByBizNo(String bizNo);
    List<VendorVO> list(String keyword, String type);
}
