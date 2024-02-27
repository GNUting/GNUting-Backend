package gang.GNUtingBackend.user.controller;

import gang.GNUtingBackend.image.service.ImageService;
import gang.GNUtingBackend.response.ApiResponse;
import gang.GNUtingBackend.user.domain.enums.Gender;
import gang.GNUtingBackend.user.domain.enums.UserRole;
import gang.GNUtingBackend.user.dto.UserLoginRequestDto;
import gang.GNUtingBackend.user.dto.UserLoginResponseDto;
import gang.GNUtingBackend.user.dto.UserSignupRequestDto;
import gang.GNUtingBackend.user.dto.UserSignupResponseDto;
import gang.GNUtingBackend.user.service.UserService;
import gang.GNUtingBackend.user.token.TokenProvider;
import java.io.IOException;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
     * @param userLoginRequestDto
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserLoginResponseDto>> login(@RequestBody UserLoginRequestDto userLoginRequestDto) {
        HttpHeaders httpHeaders = new HttpHeaders();
        UserLoginResponseDto userLoginResponseDto = userService.login(userLoginRequestDto.getEmail(), userLoginRequestDto.getPassword());
        String token = tokenProvider.createToken(userLoginResponseDto.getEmail(), userLoginResponseDto.getUserRole());

        httpHeaders.add("Authorization", "Bearer " + token);

        ApiResponse<UserLoginResponseDto> apiResponse = ApiResponse.onSuccess(userLoginResponseDto);

        return ResponseEntity.ok()
                .headers(httpHeaders)
                .body(apiResponse);
    }

    /**
     * 사용자의 가입 요청을 처리하고, 가입이 성공했을 때, 응답 헤더에 토큰을 추가하여 반환한다.
     * @param
     * @return
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserSignupResponseDto>> signup(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("name") String name,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("gender") Gender gender,
            @RequestParam("birthDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDate,
            @RequestParam("nickname") String nickname,
            @RequestParam("department") String department,
            @RequestParam("studentId") String studentId,
            @RequestParam("profileImage") MultipartFile profileImage,
            @RequestParam("userSelfIntroduction") String userSelfIntroduction
            ) throws IOException {

        String mediaLink = imageService.uploadProfileImage(profileImage, email);

        UserSignupRequestDto userSignupRequestDto = new UserSignupRequestDto(
                email, password, name, phoneNumber, gender, birthDate, nickname, department, studentId, mediaLink,
                UserRole.ROLE_USER, userSelfIntroduction);

        HttpHeaders httpHeaders = new HttpHeaders();
        UserSignupResponseDto user = userService.signup(userSignupRequestDto);
        String token = tokenProvider.createToken(user.getEmail(), user.getUserRole());

        httpHeaders.add("Authorization", "Bearer " + token);

        ApiResponse<UserSignupResponseDto> apiResponse = ApiResponse.onSuccess(user);

        return ResponseEntity.ok()
                .headers(httpHeaders)
                .body(apiResponse);
    }

//    @PostMapping("/signup")
//    public ResponseEntity<ApiResponse<UserSignupResponseDto>> signup(@RequestBody UserSignupRequestDto userSignupRequestDto) {
//        HttpHeaders httpHeaders = new HttpHeaders();
//        UserSignupResponseDto user = userService.signup(userSignupRequestDto);
//        String token = tokenProvider.createToken(user.getEmail(), user.getUserRole());
//
//        httpHeaders.add("Authorization", "Bearer " + token);
//
//        ApiResponse<UserSignupResponseDto> apiResponse = ApiResponse.onSuccess(user);
//
//        return ResponseEntity.ok()
//                .headers(httpHeaders)
//                .body(apiResponse);
//    }

}
