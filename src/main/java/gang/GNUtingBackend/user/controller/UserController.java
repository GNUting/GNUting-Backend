package gang.GNUtingBackend.user.controller;

import gang.GNUtingBackend.exception.handler.UserHandler;
import gang.GNUtingBackend.image.service.ImageService;
import gang.GNUtingBackend.response.ApiResponse;
import gang.GNUtingBackend.response.code.status.ErrorStatus;
import gang.GNUtingBackend.user.domain.enums.Gender;
import gang.GNUtingBackend.user.domain.enums.UserRole;
import gang.GNUtingBackend.user.dto.UserDetailResponseDto;
import gang.GNUtingBackend.user.dto.UserLoginRequestDto;
import gang.GNUtingBackend.user.dto.UserLoginResponseDto;
import gang.GNUtingBackend.user.dto.UserSignupRequestDto;
import gang.GNUtingBackend.user.dto.UserSignupResponseDto;
import gang.GNUtingBackend.user.dto.token.ReIssueTokenRequestDto;
import gang.GNUtingBackend.user.dto.token.ReIssueTokenResponseDto;
import gang.GNUtingBackend.user.dto.token.TokenResponseDto;
import gang.GNUtingBackend.user.service.UserService;
import gang.GNUtingBackend.user.token.TokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.io.IOException;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
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
    private final ImageService imageService;

    /**
     * 사용자 로그인 요청을 처리하고, 로그인이 성공했을 때 응답 헤더에 토큰을 추가하여 반환한다.
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
     * 사용자의 가입 요청을 처리하고, 가입이 성공했을 때, 응답 헤더에 토큰을 추가하여 반환한다.
     *
     * @param
     * @return
     */
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

        String mediaLink = null;
        if (profileImage != null && !profileImage.isEmpty()) {
            mediaLink = imageService.uploadProfileImage(profileImage, email);
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
    public ResponseEntity<ApiResponse<Boolean>> checkNicknameAvailability(@RequestParam("nickname") @Parameter(description = "닉네임") String nickname) {
        boolean isAvailable = userService.isNicknameAvailable(nickname);

        if (!isAvailable) {
            throw new UserHandler(ErrorStatus.DUPLICATE_NICKNAME);
        }
        ApiResponse<Boolean> apiResponse = ApiResponse.onSuccess(true, "사용 가능한 닉네임입니다.");
        return ResponseEntity.ok().body(apiResponse);
    }

    /**
     * 사용자 프로필사진, 비밀번호, 닉네임, 학과, 한줄소개를 수정한다.
     * @param token
     * @param profileImage
     * @param nickname
     * @param password
     * @param department
     * @param userSelfIntroduction
     * @return
     * @throws IOException
     */
    @PatchMapping("/update")
    @Operation(summary = "사용자 프로필 수정 API", description = "프로필 이미지, 닉네임, 비밀번호, 학과, 한 줄 소개를 수정합니다.")
    public ResponseEntity<ApiResponse<UserDetailResponseDto>> userInfoUpdate(
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "profileImage", required = false) @Parameter(description = "프로필 이미지") MultipartFile profileImage,
            @RequestParam("nickname") @Parameter(description = "닉네임") String nickname,
            @RequestParam("password") @Parameter(description = "비밀번호") String password,
            @RequestParam("department") @Parameter(description = "학과") String department,
            @RequestParam("userSelfIntroduction") @Parameter(description = "한 줄 소개") String userSelfIntroduction) throws IOException {
        token = token.substring(7);
        String email = tokenProvider.getUserEmail(token);

        String mediaLink = null;
        if (profileImage != null && !profileImage.isEmpty()) {
            mediaLink = imageService.uploadProfileImage(profileImage, email);
        }

        ApiResponse<UserDetailResponseDto> apiResponse = ApiResponse.onSuccess(userService.userInfoUpdate(mediaLink, nickname, password, department, userSelfIntroduction, token));

        return ResponseEntity.ok()
                .body(apiResponse);
    }

    @PostMapping("/reIssueAccessToken")
    @Operation(summary = "토큰 재발급 API", description = "refresh 토큰으로 accessToken을 재발급합니다.")
    public ResponseEntity<ApiResponse<ReIssueTokenResponseDto>> reIssueAccessToken(@RequestBody ReIssueTokenRequestDto reIssueTokenRequestDto) {
        ReIssueTokenResponseDto response = userService.reissueAccessToken(
                reIssueTokenRequestDto.getRefreshToken());

        ApiResponse<ReIssueTokenResponseDto> apiResponse = ApiResponse.onSuccess(response);

        return ResponseEntity.ok()
                .body(apiResponse);
    }
}

