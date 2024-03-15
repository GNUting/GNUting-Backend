package gang.GNUtingBackend.notification.controller;


import gang.GNUtingBackend.notification.dto.UserNotificationResponseDto;
import gang.GNUtingBackend.notification.service.UserNotificationService;
import gang.GNUtingBackend.response.ApiResponse;
import gang.GNUtingBackend.user.token.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserNotificationController {
    private final TokenProvider tokenProvider;
    private final UserNotificationService userNotificationService;
    @GetMapping("/notification")
    public ResponseEntity<?> showNotification(@RequestHeader("Authorization") String token){
        String email=tokenProvider.getUserEmail(token.substring(7));
        List<UserNotificationResponseDto> notifications=userNotificationService.showNotification(email);
        return ResponseEntity.ok()
                .body(ApiResponse.onSuccess(notifications));
    }
}