package egovframework.groupware.vendor.service.impl;

import egovframework.groupware.cmm.ApiException;
import egovframework.groupware.vendor.mapper.VendorMapper;
import egovframework.groupware.vendor.service.BizNoValidator;
import egovframework.groupware.vendor.service.VendorService;
import egovframework.groupware.vendor.service.VendorVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VendorServiceImpl implements VendorService {

    private final VendorMapper mapper;

    public VendorServiceImpl(VendorMapper mapper) { this.mapper = mapper; }

    @Override
    @Transactional
    public Long create(VendorVO vo) {
        if (!BizNoValidator.isValid(vo.getBizNo())) {
            throw new ApiException("INVALID_BIZ_NO", "사업자등록번호 형식이 올바르지 않습니다");
        }
        if (mapper.findByBizNo(vo.getBizNo()) != null) {
            throw new ApiException("DUPLICATE_BIZ_NO", "이미 등록된 사업자등록번호입니다");
        }
        mapper.insert(vo);
        return vo.getVendorId();
    }

    @Override
    @Transactional
    public void update(VendorVO vo) { mapper.update(vo); }

    @Override public VendorVO findById(Long vendorId) { return mapper.findById(vendorId); }
    @Override public VendorVO findByBizNo(String bizNo) { return mapper.findByBizNo(bizNo); }
    @Override public List<VendorVO> list(String keyword, String type) { return mapper.list(keyword, type); }
}
