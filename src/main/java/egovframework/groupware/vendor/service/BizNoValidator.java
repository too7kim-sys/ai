package egovframework.groupware.vendor.service;

/**
 * 한국 사업자등록번호 체크섬 검증 (10자리, 표준 알고리즘).
 */
public class BizNoValidator {

    private static final int[] WEIGHTS = {1, 3, 7, 1, 3, 7, 1, 3, 5};

    public static boolean isValid(String bizNo) {
        if (bizNo == null) return false;
        String digits = bizNo.replaceAll("[^0-9]", "");
        if (digits.length() != 10) return false;
        int sum = 0;
        for (int i = 0; i < 9; i++) sum += (digits.charAt(i) - '0') * WEIGHTS[i];
        sum += (digits.charAt(8) - '0') * 5 / 10;
        int check = (10 - sum % 10) % 10;
        return check == (digits.charAt(9) - '0');
    }
}
