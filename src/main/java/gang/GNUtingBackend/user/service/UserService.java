package gang.GNUtingBackend.user.service;

import gang.GNUtingBackend.exception.handler.UserHandler;
import gang.GNUtingBackend.response.code.status.ErrorStatus;
import gang.GNUtingBackend.user.domain.User;
import gang.GNUtingBackend.user.dto.UserLoginResponseDto;
import gang.GNUtingBackend.user.dto.UserSignupRequestDto;
import gang.GNUtingBackend.user.repository.UserRepository;
import gang.GNUtingBackend.user.token.TokenProvider;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    /**
     * 사용자 회원가입하기
     * @param userSignupRequestDto
     * @return
     */
    @Transactional
    public User signUp(UserSignupRequestDto userSignupRequestDto) {
        // 이메일로 이미 가입된 사용자가 있는지 확인
        Optional<User> existingUser = userRepository.findByEmail(userSignupRequestDto.getEmail());

        if (existingUser.isEmpty()) {
            // 사용자가 존재하지 않으면 비밀번호를 암호화하여 저장
            String encodedPassword = bCryptPasswordEncoder.encode(userSignupRequestDto.getPassword());
            userSignupRequestDto.setPassword(encodedPassword);

            // UserSignupRequestDto를 User 엔티티로 변환하여 저장
            User newUser = userSignupRequestDto.toEntity();
            return userRepository.save(newUser);
        } else {
            // 이미 가입된 사용자가 있으면 예외 발생
            throw new UserHandler(ErrorStatus.USER_ALREADY_EXIST);
        }
    }

    @Transactional(readOnly = true)
    public UserLoginResponseDto login(String email, String password) {
        // 이메일로 사용자 정보를 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        // 입력한 비밀번호를 암호화된 비밀번호와 비교
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new UserHandler(ErrorStatus.PASSWORD_NOT_MATCH);
        }
    }
}
