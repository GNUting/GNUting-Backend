package gang.GNUtingBackend.chat.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import gang.GNUtingBackend.user.auth.PrincipalDetails;
import gang.GNUtingBackend.user.auth.PrincipalDetailsService;
import gang.GNUtingBackend.user.domain.User;
import gang.GNUtingBackend.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

@ExtendWith(MockitoExtension.class)
public class PrincipalDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PrincipalDetailsService principalDetailsService;

    @Test
    public void 입력받은_파라미터_이메일과_PrincipalDetails의_이메일과_일치하는지_검증() {
        // given
        String expectedEmail = "test@example.com";
        User mockUser = new User();
        mockUser.setEmail(expectedEmail);
        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(mockUser));

        // when
        UserDetails userDetails = principalDetailsService.loadUserByUsername(expectedEmail);

        // then
        assertEquals(expectedEmail, ((PrincipalDetails) userDetails).getEmail());
    }

}