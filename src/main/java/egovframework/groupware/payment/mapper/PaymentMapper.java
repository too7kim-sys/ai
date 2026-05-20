package egovframework.groupware.payment.mapper;

import egovframework.groupware.payment.service.PaymentVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaymentMapper {

    /** PaymentVO insert; paymentId가 자동 채워짐. */
    int insert(PaymentVO vo);

    /**
     * 인보이스 없는 출금 거래(환급 등) 헬퍼.
     * @return 생성된 paymentId
     */
    default Long insertOutgoing(java.math.BigDecimal amount, String bankCd, String bankAccount,
                                String counterpartNm, String memo, Long regUserId) {
        PaymentVO p = new PaymentVO();
        p.setPayTypeCd("OUTGOING");
        p.setPayDt(java.time.LocalDate.now());
        p.setAmount(amount);
        p.setMethodCd("BANK_TRANSFER");
        p.setBankCd(bankCd);
        p.setBankAccount(bankAccount);
        p.setCounterpartNm(counterpartNm);
        p.setMemo(memo);
        p.setRegUserId(regUserId);
        insert(p);
        return p.getPaymentId();
    }
}
