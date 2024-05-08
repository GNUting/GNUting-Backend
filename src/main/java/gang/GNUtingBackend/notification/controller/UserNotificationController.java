package gang.GNUtingBackend.notification.controller;


import gang.GNUtingBackend.board.dto.ApplicationStatusResponseDto;
import gang.GNUtingBackend.board.dto.BoardApplyLeaderDto;
import gang.GNUtingBackend.board.entity.BoardApplyLeader;
import gang.GNUtingBackend.chat.dto.ChatNotificationResponseDto;
import gang.GNUtingBackend.chat.service.ChatRoomUserService;
import gang.GNUtingBackend.notification.dto.NotificationChatSettingDto;
import gang.GNUtingBackend.notification.dto.NotificationSettingDto;
import gang.GNUtingBackend.notification.dto.UserNotificationResponseDto;
import gang.GNUtingBackend.notification.entity.enums.NotificationSetting;
import gang.GNUtingBackend.notification.service.UserNotificationService;
import gang.GNUtingBackend.response.ApiResponse;
import gang.GNUtingBackend.user.repository.UserRepository;
import gang.GNUtingBackend.user.token.TokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserNotificationController {
    private final TokenProvider tokenProvider;
    private final UserNotificationService userNotificationService;
    private final ChatRoomUserService chatRoomUserService;

    @GetMapping("/notification")
    @Operation(summary = "알림 모두보기 API", description = "자신에게 온 알림을 봅니다.")
    public ResponseEntity<?> showNotification(@RequestHeader("Authorization") String token){
        String email=tokenProvider.getUserEmail(token.substring(7));
        List<UserNotificationResponseDto> notifications=userNotificationService.showNotification(email);
        return ResponseEntity.ok()
                .body(ApiResponse.onSuccess(notifications));
    }

    @GetMapping("notification/check")
    @Operation(summary = "새알림 확인 API", description = "읽지않은 새로운 알림이 있을시 N(ew)표시")
    public ResponseEntity<?> checkNotification(@RequestHeader("Authorization") String token){
        String email=tokenProvider.getUserEmail(token.substring(7));
        boolean checkNotification=userNotificationService.checkNotification(email);
        return ResponseEntity.ok()
                .body(ApiResponse.onSuccess(checkNotification));
    }

    @DeleteMapping("/notification/{id}")
    @Operation(summary = "알림삭제 API", description = "사용자가 알림을 삭제합니다.")
    public ResponseEntity<?> deleteNotification(@RequestHeader("Authorization") String token,@PathVariable Long id){
        String email=tokenProvider.getUserEmail(token.substring(7));
        String notificationdeleted=userNotificationService.deleteNotification(email,id);
        return ResponseEntity.ok()
                .body(ApiResponse.onSuccess(notificationdeleted));
    }

    @PutMapping("/notificationSetting")
    @Operation(summary = "전체 알림 켜기 / 끄기 API", description = "사용자의 전체 알림을 켜고 끕니다.")
    public ResponseEntity<?> updateNotificationSetting(
            @RequestHeader("Authorization") String token,
            @RequestBody NotificationSettingDto notificationSettingDto) {
        String email = tokenProvider.getUserEmail(token.substring(7));

        boolean setting = userNotificationService.updateNotificationSetting(email,
                notificationSettingDto.getNotificationSetting());

        return ResponseEntity.ok()
                .body(ApiResponse.onSuccess(setting));
    }

    @PutMapping("/{chatRoomId}/notificationSetting")
    @Operation(summary = "채팅 알림 켜기 / 끄기 API", description = "사용자가 참여한 채팅방의 알림을 켜고 끕니다.")
    public ResponseEntity<?> updateChatRoomNotificationSetting(
            @RequestHeader("Authorization") String token,
            @PathVariable Long chatRoomId,
            @RequestBody NotificationSettingDto notificationSettingDto) {
        String email = tokenProvider.getUserEmail(token.substring(7));

        boolean setting = chatRoomUserService.updateNotificationSetting(chatRoomId, email,
                notificationSettingDto.getNotificationSetting());

        return ResponseEntity.ok()
                .body(ApiResponse.onSuccess(setting));
    }

    @GetMapping("/notification/show/allsetting")
    @Operation(summary = "사용자 전체알림보기 세팅값 API", description = "사용자의 전체알림보기 상태를 확인합니다.")
    public ResponseEntity<?> showMyAllNotificationSetting(@RequestHeader("Authorization") String token){
        String email=tokenProvider.getUserEmail(token.substring(7));
        NotificationSettingDto myAllNotificationSetting = userNotificationService.myAllNotificationSetting(email);
        return ResponseEntity.ok()
                .body(ApiResponse.onSuccess(myAllNotificationSetting));
    }

    @GetMapping("/{chatRoomId}/show/notificationSetting")
    @Operation(summary = "사용자 채팅알림 세팅값 API", description = "사용자의 채팅알림 상태를 확인합니다.")
    public ResponseEntity<?> showMyChatNotificationSetting(@RequestHeader("Authorization") String token,
                                                           @PathVariable Long chatRoomId){
        String email = tokenProvider.getUserEmail(token.substring(7));
        NotificationSettingDto notificationSettingDto = userNotificationService.checkChatRoomNotificationSetting(
                chatRoomId, email);
        return ResponseEntity.ok()
                .body(ApiResponse.onSuccess(notificationSettingDto));
    }

    @GetMapping("/notification/application/click/{id}")
    @Operation(summary = "알림 클릭시 신청받은현황으로 이동하는 API", description = "알림을 클릭하면 ID 신청받은현황으로 넘어갑니다.")
    public ResponseEntity<?> notificationApplicationClickAction(@RequestHeader("Authorization") String token,@PathVariable Long id){
        String email = tokenProvider.getUserEmail(token.substring(7));
        ApplicationStatusResponseDto applicationStatusResponseDto =userNotificationService.notificationApplicationClickAction(email,id);
        return ResponseEntity.ok()
                .body(ApiResponse.onSuccess(applicationStatusResponseDto));
    }

    @GetMapping("/notification/chat/click/{id}")
    @Operation(summary = "알림 클릭시 채팅방으로 이동하는 API", description = "알림을 클릭하면 ID 채팅방으로 넘어갑니다.")
    public ResponseEntity<?> notifficationChatClickAction(@RequestHeader("Authorization") String token,@PathVariable Long id){
        String email = tokenProvider.getUserEmail(token.substring(7));
        ChatNotificationResponseDto applicationStatusResponseDto =userNotificationService.notificationChatClickAction(email,id);
        return ResponseEntity.ok()
                .body(ApiResponse.onSuccess(applicationStatusResponseDto));
    }

}
