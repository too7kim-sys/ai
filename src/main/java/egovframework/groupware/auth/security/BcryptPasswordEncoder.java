package egovframework.groupware.auth.security;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;

public class BcryptPasswordEncoder implements PasswordEncoder {

    private static final int STRENGTH = 10;

    @Override
    public String encode(CharSequence rawPassword) {
        return BCrypt.withDefaults().hashToString(STRENGTH, rawPassword.toString().toCharArray());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) return false;
        try {
            return BCrypt.verifyer().verify(rawPassword.toString().toCharArray(), encodedPassword).verified;
        } catch (Exception ex) {
            return false;
        }
    }
}
