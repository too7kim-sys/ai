package egovframework.groupware.vendor.service;

import egovframework.groupware.cmm.BaseVO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VendorVO extends BaseVO {
    private Long vendorId;
    private String vendorTypeCd;
    private String bizNo;
    private String companyNm;
    private String ceoNm;
    private String bizKind;
    private String bizItem;
    private String address;
    private String zipcode;
    private String contactNm;
    private String contactPhone;
    private String contactEmail;
    private String bankCd;
    private String bankAccount;
    private String bankHolder;
    private String taxTypeCd;
    private String memo;
    private String useYn;
}
