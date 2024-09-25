package gang.GNUtingBackend.eventFunction.controllor;

import gang.GNUtingBackend.board.entity.enums.Status;
import gang.GNUtingBackend.eventFunction.dto.EventApplyResponseDto;
import gang.GNUtingBackend.eventFunction.dto.EventParticipateRequestDto;
import gang.GNUtingBackend.eventFunction.service.EventServerService;
import gang.GNUtingBackend.response.ApiResponse;
import gang.GNUtingBackend.user.token.TokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class EventServerControllor {

    private final EventServerService eventServerService;
    private final TokenProvider tokenProvider;

    @GetMapping("/event/server/check")
    @Operation(summary = "총학생회 이벤트 on off 확인 API", description = "총학생회 이벤트 서버 상태를 확인하는 기능.")
    public ResponseEntity<?> eventOpenCheck(){
        Status eventOpenChecked =eventServerService.checkState();
        return ResponseEntity.ok()
                .body(ApiResponse.onSuccess(eventOpenChecked));
    }

    @PostMapping("/event/participate")
    @Operation(summary = "총학생회 이벤트 참여 닉네임으로 채팅신청 API", description = "총학생회 이벤트로 닉네임으로 채팅신청 ")
    public ResponseEntity<?> eventParticipate(@RequestHeader("Authorization") String token, @RequestBody EventParticipateRequestDto eventParticipateRequestDto) {
        String email = tokenProvider.getUserEmail(token.substring(7));
        EventApplyResponseDto eventOpenChecked =eventServerService.makeChat(email,eventParticipateRequestDto);
        return ResponseEntity.ok()
                .body(ApiResponse.onSuccess(eventOpenChecked));
    }
}
