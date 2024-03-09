package gang.GNUtingBackend.slack.controller;

import gang.GNUtingBackend.response.ApiResponse;
import gang.GNUtingBackend.slack.dto.BoardReportRequestDto;
import gang.GNUtingBackend.slack.service.BoardReportService;
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
public class BoardReportController {

    private final BoardReportService boardReportService;
    private final TokenProvider tokenProvider;

    @PostMapping("/boardReport")
    @Operation(summary = "게시글 신고", description = "해당 게시글 작성자를 신고합니다.")
    public ResponseEntity<?> postBoardReport(
            @RequestHeader("Authorization") String token,
            @RequestBody BoardReportRequestDto boardReportRequestDto) throws IOException {
        String email = tokenProvider.getUserEmail(token.substring(7));
        boardReportService.postReport(email, boardReportRequestDto);
        return ResponseEntity.ok()
                .body(ApiResponse.onSuccess("정상적으로 신고 처리 되었습니다."));
    }
}
