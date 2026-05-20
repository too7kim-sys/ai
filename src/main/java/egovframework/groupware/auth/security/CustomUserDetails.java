package egovframework.groupware.auth.security;

import egovframework.groupware.user.service.UserVO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final UserVO user;

    public CustomUserDetails(UserVO user) {
        this.user = user;
    }

    public UserVO getUser() { return user; }
    public Long getUserId() { return user.getUserId(); }
    public String getName() { return user.getName(); }
    public Long getDeptId() { return user.getDeptId(); }
    public String getRoleCd() { return user.getRoleCd(); }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRoleCd()));
    }

    @Override public String getPassword() { return user.getPasswordHash(); }
    @Override public String getUsername() { return user.getEmail(); }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return !"Y".equals(user.getLockedYn()); }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return "Y".equals(user.getUseYn()); }
}
