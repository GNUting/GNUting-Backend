package gang.GNUtingBackend.user.auth;

import gang.GNUtingBackend.user.domain.User;
import java.util.ArrayList;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * User 클래스를 기반으로 PrincipalDetails 클래스를 구현한다. 이 경우, Spring Security의 UserDetails 인터페이스를 구현해야 한다. UesrDetails 인터페이스는
 * Spring Security가 사용자 정보를 관리하는데 사용하는 주요 인터페이스이다. 이 인터페이스를 구현함으로써, Spring Security 내의 사용자 인증 및 권한 부여 정보를 제공한다.
 */
public class PrincipalDetails implements UserDetails {

    private final User user;

    public PrincipalDetails(User user) {
        this.user = user;
    }

    /**
     * 사용자의 권한을 반환한다.
     *
     * @return 권한 목록
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getUserRole().name();
            }
        });
        return collect;
    }

    /**
     * 사용자의 비밀번호를 반환한다.
     *
     * @return
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * 사용자의 이메일을 반환한다.
     *
     * @return 사용자 이메일
     */
    public String getEmail() {
        return user.getEmail();
    }

    /**
     * 사용자의 이름을 반환한다.
     *
     * @return 사용자 이름
     */
    @Override
    public String getUsername() {
        return user.getName();
    }

    /**
     * 계정이 만료되었는지 여부를 반환한다.
     *
     * @return true
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 계정이 잠겨 있는지 여부를 반환한다.
     *
     * @return true
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 자격 증명이 만료되었는지 여부를 반환한다.
     *
     * @return true
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 계정이 활성화되어 있는지 여부를 반환한다.
     *
     * @return 사용자의 활성화 여부에 따라
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
