//package gang.GNUtingBackend.meeting.controller;
//
//
//import gang.GNUtingBackend.meeting.service.MeetingService;
//import gang.GNUtingBackend.meeting.service.MeetingStatusService;
//import gang.GNUtingBackend.response.ApiResponse;
//import gang.GNUtingBackend.user.token.TokenProvider;
//import io.swagger.v3.oas.annotations.Operation;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/v1")
//public class MeetingStatusController {
//
//    private final TokenProvider tokenProvider;
//    private final MeetingStatusService meetingStatusService;
//
//
//    @PostMapping("/meeting/applications/accept/{id}")
//    @Operation(summary = "1:1 매칭 승인하기 API", description = "1:1 매칭 승인하고 채팅방이 만들어집니다.")
//    public ResponseEntity<?> accept(@RequestHeader("Authorization") String token, @PathVariable Long id) {
//        String email = tokenProvider.getUserEmail(token.substring(7));
//        String accept = meetingStatusService.accept(email, id);
//        return ResponseEntity.ok()
//                .body(ApiResponse.onSuccess(accept));
//    }
//
//    //거절하기
//    @PatchMapping("/meeting/applications/refuse/{id}")
//    @Operation(summary = "1:1 매칭 거절하기 API", description = "내 글에 신청한 1:1 매칭을 거절합니다.")
//    public ResponseEntity<?> refuse(@RequestHeader("Authorization") String token, @PathVariable Long id) {
//        String email = tokenProvider.getUserEmail(token.substring(7));
//        String refuse = meetingStatusService.refuse(id, email);
//        return ResponseEntity.ok()
//                .body(ApiResponse.onSuccess(refuse));
//    }
//
//
//    //취소하기
//    @DeleteMapping("/meeting/applications/cancel/{id}")
//    @Operation(summary = "1:1 매칭 신청 취소하기 API", description = "내가 신청했던 게시물의 1:1 매칭을 취소합니다")
//    public ResponseEntity<?> cancel(@RequestHeader("Authorization") String token, @PathVariable Long id) {
//        String email = tokenProvider.getUserEmail(token.substring(7));
//        String cancel = meetingStatusService.cancel(id, email);
//        return ResponseEntity.ok()
//                .body(ApiResponse.onSuccess(cancel));
//    }
//
//    @PatchMapping("/meeting/applications/applystate/{id}")
//    @Operation(summary = "내가 신청한 현황 숨기기 API", description = "내가 신청한 현황을 숨김처리하는 API.")
//    public ResponseEntity<?> applyStateHide(@RequestHeader("Authorization") String token,@PathVariable Long id) {
//        String email = tokenProvider.getUserEmail(token.substring(7));
//        String userSearchResponseDto = meetingStatusService.applyStateHide(email,id);
//        return ResponseEntity.ok()
//                .body(ApiResponse.onSuccess(userSearchResponseDto));
//    }
//
//    @PatchMapping("/meeting/applications/receivedstate/{id}")
//    @Operation(summary = "신청받은 현황 숨기기 API", description = "신청받은 현황을 숨김처리하는 API.")
//    public ResponseEntity<?> receivedStateHide(@RequestHeader("Authorization") String token,@PathVariable Long id) {
//        String email = tokenProvider.getUserEmail(token.substring(7));
//        String userSearchResponseDto = meetingStatusService.receivedStateHide(email,id);
//        return ResponseEntity.ok()
//                .body(ApiResponse.onSuccess(userSearchResponseDto));
//    }
//
//}
