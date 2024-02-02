package gang.GNUtingBackend.user.security;

import gang.GNUtingBackend.user.domain.User;
import gang.GNUtingBackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * 사용자 이름(이메일)을 기반으로 사용자 정보를 조회하고, 해당 정보를 바탕으로 UserDetails 객체 생성
     * @param email
     * @return PrincipalDetails 객체(사용자의 인증 정보를 담고 있다)
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다. email = " + email));

        return new PrincipalDetails(user);
    }
}