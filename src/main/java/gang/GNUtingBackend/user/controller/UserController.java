package gang.GNUtingBackend.user.controller;

import gang.GNUtingBackend.response.ApiResponse;
import gang.GNUtingBackend.user.dto.UserLoginRequestDto;
import gang.GNUtingBackend.user.dto.UserLoginResponseDto;
import gang.GNUtingBackend.user.service.UserService;
import gang.GNUtingBackend.user.token.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.SelectBeforeUpdate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {

    private final TokenProvider tokenProvider;
    private final UserService userService;

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



}
