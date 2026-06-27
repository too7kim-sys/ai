package egovframework.groupware.contract.mapper;

import egovframework.groupware.contract.service.ContractTemplateVO;
import egovframework.groupware.contract.service.EmploymentContractVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ContractMapper {

    List<ContractTemplateVO> listTemplates();

    ContractTemplateVO findTemplate(@Param("templateId") Long templateId);

    int insert(EmploymentContractVO vo);

    int update(EmploymentContractVO vo);

    int updateStatus(@Param("contractId") Long contractId,
                     @Param("status") String status);

    int markSigned(@Param("contractId") Long contractId,
                   @Param("signature") String signature);

    EmploymentContractVO findById(@Param("contractId") Long contractId);

    List<EmploymentContractVO> listAll(@Param("keyword") String keyword,
                                       @Param("status") String status);

    List<EmploymentContractVO> listByUser(@Param("userId") Long userId);
}
