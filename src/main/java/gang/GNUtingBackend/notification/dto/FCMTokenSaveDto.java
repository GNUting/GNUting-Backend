package gang.GNUtingBackend.notification.dto;

import gang.GNUtingBackend.notification.entity.FCM;
import gang.GNUtingBackend.user.domain.User;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class FCMTokenSaveDto {

    private String fcmToken;

    public static FCM toEntity(FCMTokenSaveDto fcmToken, User user){
        return FCM.builder()
                .fcmToken(fcmToken.getFcmToken())
                .userId(user)
                .build();
    }
}
