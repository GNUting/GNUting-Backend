package gang.GNUtingBackend.slack.controller;

import gang.GNUtingBackend.response.ApiResponse;
import gang.GNUtingBackend.slack.dto.UserReportRequestDto;
import gang.GNUtingBackend.slack.service.UserReportService;
import gang.GNUtingBackend.user.token.TokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserReportController {

    private final UserReportService userReportService;
    private final TokenProvider tokenProvider;

    @PostMapping("/userReport")
    @Operation(summary = "유저 신고", description = "유저를 신고합니다.")
    public ResponseEntity<?> UserReport(
            @RequestHeader("Authorization") String token,
            @RequestBody UserReportRequestDto userReportRequestDto) throws IOException {
        String email = tokenProvider.getUserEmail(token.substring(7));
        userReportService.ReportUser(email, userReportRequestDto);
        return ResponseEntity.ok()
                .body(ApiResponse.onSuccess("정상적으로 신고 처리 되었습니다."));
    }

}
