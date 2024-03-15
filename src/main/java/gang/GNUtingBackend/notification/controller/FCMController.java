package gang.GNUtingBackend.notification.controller;

import gang.GNUtingBackend.notification.dto.FCMTokenSaveDto;
import gang.GNUtingBackend.notification.dto.TestDto;
import gang.GNUtingBackend.notification.service.FCMService;
import gang.GNUtingBackend.response.ApiResponse;
import gang.GNUtingBackend.user.token.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class FCMController {

    private final TokenProvider tokenProvider;
    private final FCMService fcmNotificationService;


//    @PostMapping("/fcm")
//    public ResponseEntity pushMessage(@RequestBody TestDto testDto) throws IOException {
//        System.out.println(testDto.getTargetToken() + " "
//                +testDto.getTitle() + " " + testDto.getBody());
//
//        fcmNotificationService.sendMessageTo(
//                testDto.getTargetToken(),
//                testDto.getTitle(),
//                testDto.getBody());
//        return ResponseEntity.ok().build();
//    }


    @PostMapping("/savetoken")
    public  ResponseEntity<?> saveFCMToken(@RequestBody FCMTokenSaveDto fcmEntity, @RequestHeader("Authorization") String token){
        String email = tokenProvider.getUserEmail(token.substring(7));
        String savetoken=fcmNotificationService.saveFCMToken(fcmEntity,email);
        return ResponseEntity.ok()
                .body(ApiResponse.onSuccess(savetoken));
    }
}
