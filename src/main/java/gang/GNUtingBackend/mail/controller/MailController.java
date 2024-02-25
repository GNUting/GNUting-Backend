package gang.GNUtingBackend.mail.controller;

import gang.GNUtingBackend.mail.dto.MailSendRequestDto;
import gang.GNUtingBackend.mail.dto.MailSendResponseDto;
import gang.GNUtingBackend.mail.service.MailService;
import gang.GNUtingBackend.response.ApiResponse;
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
    public ResponseEntity<ApiResponse<MailSendResponseDto>> mailSend(@RequestBody MailSendRequestDto mailSendRequestDto) {
        int number = mailService.sendMail(mailSendRequestDto.getEmail());
        String num = Integer.toString(number);

        MailSendResponseDto mailSendResponseDto = new MailSendResponseDto();
        mailSendResponseDto.setNumber(num);

        ApiResponse<MailSendResponseDto> apiResponse = ApiResponse.onSuccess(mailSendResponseDto);

        return ResponseEntity.ok()
                .body(apiResponse);
    }
}
