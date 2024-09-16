package gang.GNUtingBackend.meeting.controller;

import gang.GNUtingBackend.meeting.service.MeetingService;
import gang.GNUtingBackend.memoThing.dto.MemoRequestDto;
import gang.GNUtingBackend.response.ApiResponse;
import gang.GNUtingBackend.user.token.TokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MeetingController {

    private final TokenProvider tokenProvider;
    private final MeetingService meetingService;

    @GetMapping("/meeting")
    @Operation(summary = "1대1 정보작성 확인 API", description = "1대1 정보를 입력했는지 확인합니다.")
    public ResponseEntity<?> userMeetingInfo(@RequestHeader("Authorization") String token) {
        String email = tokenProvider.getUserEmail(token.substring(7));
        boolean userMeetingInfo=meetingService.userMeetingInfo(email);

        return ResponseEntity.ok()
                .body(ApiResponse.onSuccess(userMeetingInfo));
    }



}
