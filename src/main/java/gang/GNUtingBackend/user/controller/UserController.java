package gang.GNUtingBackend.user.controller;

import gang.GNUtingBackend.exception.handler.UserHandler;
import gang.GNUtingBackend.image.service.S3Uploader;
import gang.GNUtingBackend.response.ApiResponse;
import gang.GNUtingBackend.response.code.status.ErrorStatus;
import gang.GNUtingBackend.user.domain.enums.Gender;
import gang.GNUtingBackend.user.domain.enums.UserRole;
import gang.GNUtingBackend.user.dto.UserDetailResponseDto;
import gang.GNUtingBackend.user.dto.UserLoginRequestDto;
import gang.GNUtingBackend.user.dto.UserSignupRequestDto;
import gang.GNUtingBackend.user.dto.token.LogoutRequestDto;
import gang.GNUtingBackend.user.dto.token.ReIssueTokenRequestDto;
import gang.GNUtingBackend.user.dto.token.ReIssueTokenResponseDto;
import gang.GNUtingBackend.user.dto.token.TokenResponseDto;
import gang.GNUtingBackend.user.service.UserService;
import gang.GNUtingBackend.user.token.TokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.regex.Pattern;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {

    private final TokenProvider tokenProvider;
    private final UserService userService;
    private final S3Uploader s3Uploader;
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,15}$");


    /**
     * 사용자 로그인 요청을 처리하고, 로그인이 성공했을 때 토큰을 반환한다.
     *
     * @param userLoginRequestDto
     * @return
     */
    @PostMapping("/login")
    @Operation(summary = "로그인 API", description = "이메일과 비밀번호를 사용하여 로그인합니다.")
    public ResponseEntity<ApiResponse<TokenResponseDto>> login(
            @RequestBody UserLoginRequestDto userLoginRequestDto) {
        TokenResponseDto response = userService.login(userLoginRequestDto.getEmail(),
                userLoginRequestDto.getPassword());

        ApiResponse<TokenResponseDto> apiResponse = ApiResponse.onSuccess(response);

        return ResponseEntity.ok()
                .body(apiResponse);
    }

    /**
     * 사용자의 가입 요청을 처리하고, 가입이 성공했을 때, 토큰을 반환한다.
     *
     * @param
     * @return
     */

    //비밀번호 정규화
    public static boolean isValidPassword(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    @PostMapping("/signup")
    @Operation(summary = "회원가입 API", description = "사용자의 정보를 바탕으로 회원가입을 진행합니다.")
    public ResponseEntity<ApiResponse<TokenResponseDto>> signup(
            @RequestParam("email") @Parameter(description = "경상국립대학교 이메일") String email,
            @RequestParam("password") @Parameter(description = "비밀번호") String password,
            @RequestParam("name") @Parameter(description = "이름") String name,
            @RequestParam("phoneNumber") @Parameter(description = "전화번호") String phoneNumber,
            @RequestParam("gender") @Parameter(description = "성별") Gender gender,
            @RequestParam("birthDate") @Parameter(description = "생년 월일") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDate,
            @RequestParam("nickname") @Parameter(description = "닉네임") String nickname,
            @RequestParam("department") @Parameter(description = "학과") String department,
            @RequestParam("studentId") @Parameter(description = "학번") String studentId,
            @RequestParam(value = "profileImage", required = false) @Parameter(description = "프로필 이미지") MultipartFile profileImage,
            @RequestParam("userSelfIntroduction") @Parameter(description = "한 줄 소개") String userSelfIntroduction
    ) throws IOException {
        if (!isValidPassword(password)) {
            throw new UserHandler(ErrorStatus.PASSWORD_IS_NOT_VALID);
        }

        String mediaLink = null;
        if (profileImage != null && !profileImage.isEmpty()) {
            mediaLink = s3Uploader.uploadProfileImage(profileImage, email);
        }

        UserSignupRequestDto userSignupRequestDto = new UserSignupRequestDto(
                email, password, name, phoneNumber, gender, birthDate, nickname, department, studentId, mediaLink,
                UserRole.ROLE_USER, userSelfIntroduction);

        TokenResponseDto response = userService.signup(userSignupRequestDto);

        ApiResponse<TokenResponseDto> apiResponse = ApiResponse.onSuccess(response);

        return ResponseEntity.ok()
                .body(apiResponse);
    }

    /**
     * 닉네임이 사용가능한지의 여부를 반환한다.
     *
     * @param nickname
     * @return
     */
    @GetMapping("/check-nickname")
    @Operation(summary = "회원가입 시 닉네임 중복 체크 API", description = "이미 사용중인 닉네임인지 확인합니다.")
    public ResponseEntity<ApiResponse<Boolean>> checkNicknameAvailability(
            @RequestParam("nickname") @Parameter(description = "닉네임") String nickname) {
        boolean isAvailable = userService.isNicknameAvailable(nickname);

        if (!isAvailable) {
            throw new UserHandler(ErrorStatus.DUPLICATE_NICKNAME);
        }
        ApiResponse<Boolean> apiResponse = ApiResponse.onSuccess(true, "사용 가능한 닉네임입니다.");
        return ResponseEntity.ok().body(apiResponse);
    }

    /**
     * 사용자 프로필사진, 비밀번호, 닉네임, 학과, 한줄소개를 수정한다.
     *
     * @param token
     * @param profileImage
     * @param nickname
     * @param department
     * @param userSelfIntroduction
     * @return
     * @throws IOException
     */
    @PatchMapping("/update")
    @Operation(summary = "사용자 프로필 수정 API", description = "프로필 이미지, 닉네임, 학과, 한 줄 소개를 수정합니다.")
    public ResponseEntity<ApiResponse<UserDetailResponseDto>> userInfoUpdate(
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "profileImage", required = false) @Parameter(description = "프로필 이미지") MultipartFile profileImage,
            @RequestParam("nickname") @Parameter(description = "닉네임") String nickname,
            @RequestParam("department") @Parameter(description = "학과") String department,
            @RequestParam("userSelfIntroduction") @Parameter(description = "한 줄 소개") String userSelfIntroduction)
            throws IOException {
        token = token.substring(7);
        String email = tokenProvider.getUserEmail(token);

        String mediaLink = null;
        if (profileImage != null && !profileImage.isEmpty()) {
            mediaLink = s3Uploader.uploadProfileImage(profileImage, email);
        }

        ApiResponse<UserDetailResponseDto> apiResponse = ApiResponse.onSuccess(
                userService.userInfoUpdate(mediaLink, nickname, department, userSelfIntroduction, token));
        return ResponseEntity.ok()
                .body(apiResponse);
    }

    /**
     * accessToken이 만료되면 refreshToken을 통해 accessToken을 재발급한다.
     * @param token
     * @param reIssueTokenRequestDto
     * @return
     */
    @PostMapping("/reIssueAccessToken")
    @Operation(summary = "토큰 재발급 API", description = "refresh 토큰으로 accessToken을 재발급합니다.")
    public ResponseEntity<ApiResponse<ReIssueTokenResponseDto>> reIssueAccessToken(
            @RequestHeader("Authorization") String token,
            @RequestBody ReIssueTokenRequestDto reIssueTokenRequestDto) {
        String email = tokenProvider.getUserEmail(token.substring(7));
        ReIssueTokenResponseDto response = userService.reissueAccessToken(
                reIssueTokenRequestDto.getRefreshToken(), email);

        ApiResponse<ReIssueTokenResponseDto> apiResponse = ApiResponse.onSuccess(response);

        return ResponseEntity.ok()
                .body(apiResponse);
    }

    /**
     * 로그아웃 API
     *
     * @param token
     * @param logoutRequestDto 로그아웃 요청에 사용된 리프레시 토큰
     * @return 로그아웃 성공 메시지
     */
    @PostMapping("/logout")
    @Operation(summary = "로그아웃 API", description = "사용자 로그아웃을 처리합니다.")
    public ResponseEntity<ApiResponse<String>> logout(@RequestHeader("Authorization") String token,
                                                      @RequestBody LogoutRequestDto logoutRequestDto) {
        String email = tokenProvider.getUserEmail(token.substring(7));
        userService.logout(logoutRequestDto.getRefreshToken(), email);
        return ResponseEntity.ok()
                .body(ApiResponse.onSuccess("정상적으로 로그아웃 처리 되었습니다."));
    }

    /**
     * 회원 탈퇴 API
     *
     * @param token 회원 탈퇴 요청에 사용된 액세스 토큰
     * @return 회원 탈퇴 성공 메시지
     */
    @DeleteMapping("/deleteUser")
    @Operation(summary = "회원 탈퇴 API", description = "사용자 회원 탈퇴를 처리합니다.")
    public ResponseEntity<ApiResponse<String>> deleteUser(@RequestHeader("Authorization") String token) {
        String email = tokenProvider.getUserEmail(token.substring(7));
        userService.deleteUser(email);
        return ResponseEntity.ok()
                .body(ApiResponse.onSuccess("정상적으로 회원 탈퇴 되었습니다."));
    }
}

