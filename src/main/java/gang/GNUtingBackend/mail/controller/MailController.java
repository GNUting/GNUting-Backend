package gang.GNUtingBackend.mail.controller;

import gang.GNUtingBackend.mail.dto.MailSendRequestDto;
import gang.GNUtingBackend.mail.dto.MailSendResponseDto;
import gang.GNUtingBackend.mail.dto.MailVerifyRequestDto;
import gang.GNUtingBackend.mail.service.MailService;
import gang.GNUtingBackend.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MailController {

    private final MailService mailService;

    @PostMapping("/mail")
    @Operation(summary = "이메일 인증 API", description = "회원가입 시, 작성한 경상국립대 이메일로 인증 번호 메일을 전송합니다.")
    public ResponseEntity<ApiResponse<MailSendResponseDto>> mailSend(@RequestBody MailSendRequestDto mailSendRequestDto) {
        int number = mailService.sendMail(mailSendRequestDto.getEmail());
        String num = Integer.toString(number);

        MailSendResponseDto mailSendResponseDto = new MailSendResponseDto();
        mailSendResponseDto.setNumber(num);

        ApiResponse<MailSendResponseDto> apiResponse = ApiResponse.onSuccess(mailSendResponseDto);

        return ResponseEntity.ok()
                .body(apiResponse);
    }

    @PostMapping("/mail/verify")
    @Operation(summary = "이메일 인증번호 검증 API", description = "사용자가 입력한 인증번호를 검증합니다.")
    public ResponseEntity<ApiResponse<?>> verifyMail(@RequestBody MailVerifyRequestDto mailVerifyRequestDto) {
        boolean isVerified = mailService.verifyNumber(mailVerifyRequestDto.getEmail(), mailVerifyRequestDto.getNumber());

        return ResponseEntity.ok()
                .body(ApiResponse.onSuccess("인증되었습니다."));

    }
}
