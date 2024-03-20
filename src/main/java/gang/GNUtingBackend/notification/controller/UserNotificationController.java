package gang.GNUtingBackend.notification.controller;


import gang.GNUtingBackend.notification.dto.UserNotificationResponseDto;
import gang.GNUtingBackend.notification.service.UserNotificationService;
import gang.GNUtingBackend.response.ApiResponse;
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
    @GetMapping("/notification")
    @Operation(summary = "알림 모두보기 API", description = "자신에게 온 알림을 봅니다.")
    public ResponseEntity<?> showNotification(@RequestHeader("Authorization") String token){
        String email=tokenProvider.getUserEmail(token.substring(7));
        List<UserNotificationResponseDto> notifications=userNotificationService.showNotification(email);
        return ResponseEntity.ok()
                .body(ApiResponse.onSuccess(notifications));
    }

    @DeleteMapping("/notification/{id}")
    public ResponseEntity<?> deleteNotification(@RequestHeader("Authorization") String token,@PathVariable Long id){
        String email=tokenProvider.getUserEmail(token.substring(7));
        String notificationdeleted=userNotificationService.deleteNotification(email,id);
        return ResponseEntity.ok()
                .body(ApiResponse.onSuccess(notificationdeleted));
    }
}
