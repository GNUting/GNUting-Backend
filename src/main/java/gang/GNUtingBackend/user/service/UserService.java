package gang.GNUtingBackend.user.service;

import gang.GNUtingBackend.exception.handler.TokenHandler;
import gang.GNUtingBackend.exception.handler.UserHandler;
import gang.GNUtingBackend.response.code.status.ErrorStatus;
import gang.GNUtingBackend.user.domain.Token;
import gang.GNUtingBackend.user.domain.User;
import gang.GNUtingBackend.user.dto.UserDetailResponseDto;
import gang.GNUtingBackend.user.dto.UserSignupRequestDto;
import gang.GNUtingBackend.user.dto.UserSignupResponseDto;
import gang.GNUtingBackend.user.dto.token.ReIssueTokenResponseDto;
import gang.GNUtingBackend.user.dto.token.TokenResponseDto;
import gang.GNUtingBackend.user.repository.UserRepository;
import gang.GNUtingBackend.user.token.RefreshTokenService;
import gang.GNUtingBackend.user.token.TokenProvider;
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
    private final RefreshTokenService refreshTokenService;

    /**
     * 사용자 회원가입 하기
     * @param userSignupRequestDto
     * @return
     */
    @Transactional
    public UserSignupResponseDto signup(UserSignupRequestDto userSignupRequestDto) {
        // 이메일로 이미 가입된 사용자가 있는지 확인
        userRepository.findByEmail(userSignupRequestDto.getEmail())
                .ifPresent(user -> {
                    throw new UserHandler(ErrorStatus.USER_ALREADY_EXIST);
                });

        // 경상국립대학교 이메일을 사용하였는지 확인
        if(!userSignupRequestDto.getEmail().endsWith("@gnu.ac.kr")) {
            throw new UserHandler(ErrorStatus.INVALID_MAIL_ADDRESS);
        }

        // 닉네임으로 이미 가입된 사용자가 있는지 확인
        userRepository.findByNickname(userSignupRequestDto.getNickname())
                .ifPresent(user -> {
                    throw new UserHandler(ErrorStatus.DUPLICATE_NICKNAME);
                });

        // 사용자가 존재하지 않으면 비밀번호를 암호화하여 저장
        String encodedPassword = bCryptPasswordEncoder.encode(userSignupRequestDto.getPassword());
        userSignupRequestDto.setPassword(encodedPassword);

        // UserSignupRequestDto를 User 엔티티로 변환하여 저장
        User user = userSignupRequestDto.toEntity();
        userRepository.save(user);

        return UserSignupResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .gender(user.getGender())
                .birthDate(user.getBirthDate())
                .nickname(user.getNickname())
                .department(user.getDepartment())
                .studentId(user.getStudentId())
                .profileImage(user.getProfileImage())
                .userRole(user.getUserRole())
                .userSelfIntroduction(user.getUserSelfIntroduction())
                .build();
    }

    /**
     * 사용자 로그인 하기
     * @param email
     * @param password
     * @return UserLoginResponseDto
     */
    @Transactional(readOnly = true)
    public TokenResponseDto login(String email, String password) {
        // 이메일로 사용자 정보를 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        // 입력한 비밀번호를 암호화된 비밀번호와 비교
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new UserHandler(ErrorStatus.PASSWORD_NOT_MATCH);
        }

        String accessToken = issueAccessToken(user);
        String refreshToken = issueRefreshToken();

        refreshTokenService.saveToken(user.getEmail(), refreshToken, accessToken);


        return new TokenResponseDto(accessToken, refreshToken);
    }

    /**
     * 토큰으로 사용자 정보 조회하기
     * @param token
     * @return UserDetailResponseDto
     */
    @Transactional(readOnly = true)
    public UserDetailResponseDto userDetail(String token) {
        String email = tokenProvider.getUserEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        return UserDetailResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .gender(user.getGender())
                .birthDate(user.getBirthDate())
                .nickname(user.getNickname())
                .department(user.getDepartment())
                .studentId(user.getStudentId())
                .profileImage(user.getProfileImage())
                .userRole(user.getUserRole())
                .userSelfIntroduction(user.getUserSelfIntroduction())
                .build();
    }

    /**
     * 닉네임이 사용가능한지 여부를 판단한다.
     * @param nickname
     * @return 닉네임이 사용가능하면 true, 사용 불가능하면 false
     */
    @Transactional(readOnly = true)
    public boolean isNicknameAvailable(String nickname) {
        return userRepository.findByNickname(nickname).isEmpty();
    }

    /**
     * 사용자의 정보를 업데이트 한다.
     * @param profileImage
     * @param nickname
     * @param password
     * @param department
     * @param userSelfIntroduction
     * @param token
     * @return
     */
    @Transactional
    public UserDetailResponseDto userInfoUpdate(String profileImage, String nickname, String password, String department, String userSelfIntroduction, String token) {
        String email = tokenProvider.getUserEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        String encodedPassword = bCryptPasswordEncoder.encode(password);

        user.update(profileImage, nickname, encodedPassword, department, userSelfIntroduction);

        return UserDetailResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .gender(user.getGender())
                .birthDate(user.getBirthDate())
                .nickname(user.getNickname())
                .department(user.getDepartment())
                .studentId(user.getStudentId())
                .profileImage(user.getProfileImage())
                .userRole(user.getUserRole())
                .userSelfIntroduction(user.getUserSelfIntroduction())
                .build();
    }

    private String issueAccessToken(User user) {
        return tokenProvider.createToken(user.getEmail(), user.getUserRole());
    }

    private String issueRefreshToken() {
        return Token.createRefreshToken();
    }

    public ReIssueTokenResponseDto reissueAccessToken(String refreshToken) {
        User user = refreshTokenService.getUserByRefreshToken(refreshToken);
        Token token = refreshTokenService.findTokenByRefreshToken(refreshToken);
        String oldAccessToken = token.getAccessToken();

        if (tokenProvider.isExpiredAccessToken(oldAccessToken)) {
            String newAccessToken = issueAccessToken(user);
            token.setAccessToken(newAccessToken);
            refreshTokenService.updateToken(token);
            return new ReIssueTokenResponseDto(newAccessToken);
        }
        throw new TokenHandler(ErrorStatus.NOT_EXPIRED_ACCESS_TOKEN);
    }
}
