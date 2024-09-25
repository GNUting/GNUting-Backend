//package gang.GNUtingBackend.meeting.controller;
//
//import gang.GNUtingBackend.meeting.dto.MeetingResponseDto;
//import gang.GNUtingBackend.meeting.service.MeetingService;
//import gang.GNUtingBackend.memoThing.dto.MemoRequestDto;
//import gang.GNUtingBackend.response.ApiResponse;
//import gang.GNUtingBackend.user.token.TokenProvider;
//import io.swagger.v3.oas.annotations.Operation;
//import lombok.Getter;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import javax.websocket.server.PathParam;
//import java.util.List;
//
////어노테이션 다 주석처리 해놓음 다시개발 필요 에러 및 테스트 아얘 안됌
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/v1")
//public class MeetingController {
//
//    private final TokenProvider tokenProvider;
//    private final MeetingService meetingService;
//
//    @GetMapping("/meeting/check")
//    @Operation(summary = "1대1 정보작성 확인 API", description = "1대1 정보를 입력했는지 확인합니다.")
//    public ResponseEntity<?> userMeetingInfo(@RequestHeader("Authorization") String token) {
//        String email = tokenProvider.getUserEmail(token.substring(7));
//        boolean userMeetingInfo=meetingService.userMeetingInfo(email);
//
//        return ResponseEntity.ok()
//                .body(ApiResponse.onSuccess(userMeetingInfo));
//    }
//
//    @PostMapping("/meeting/save")
//    @Operation(summary = "1대1 등록 API", description = "1대1 미팅을 등록합니다.")
//    public ResponseEntity<?> meetingSave(@RequestHeader("Authorization") String token) {
//        String email = tokenProvider.getUserEmail(token.substring(7));
//        String saveMeeting=meetingService.saveMeeting(email);
//
//        return ResponseEntity.ok()
//                .body(ApiResponse.onSuccess(saveMeeting));
//    }
//
//    @GetMapping("/meeting")
//    @Operation(summary = "1:1 매칭 랜덤으로 20개 항목 리스트주기", description = "1:1 매칭에서 랜덤으로 20명을 선별하여 넘겨줌")
//    public ResponseEntity<?> oneToOneRandomList(@RequestHeader("Authorization") String token){
//        String email = tokenProvider.getUserEmail(token.substring(7));
//        List<MeetingResponseDto> meetingResponseDtoList =meetingService.RandomList(email);
//        return ResponseEntity.ok()
//                .body(ApiResponse.onSuccess(meetingResponseDtoList));
//    }
//
//    @DeleteMapping("/meeting")
//    @Operation(summary = "1:1 등록매칭 취소", description = "1:1 매칭등록을 취소합니다")
//    public ResponseEntity<?> deleteOneToOne(@RequestHeader("Authorization") String token){
//        String email = tokenProvider.getUserEmail(token.substring(7));
//        String deleteOneToOne =meetingService.deleteOneToOne(email);
//        return ResponseEntity.ok()
//                .body(ApiResponse.onSuccess(deleteOneToOne));
//    }
//
//    @PostMapping("/meeting/apply/{id}")
//    @Operation(summary = "1:1 매칭 신청하기", description = "1:1 매칭을 신청합니다")
//    public ResponseEntity<?> applyMeeting(@RequestHeader("Authorization") String token, @PathVariable Long id){
//        String email = tokenProvider.getUserEmail(token.substring(7));
//        String applyMeeting =meetingService.applyMeeting(email,id);
//        return ResponseEntity.ok()
//                .body(ApiResponse.onSuccess(applyMeeting));
//    }
//
//
//
//
//
//}
