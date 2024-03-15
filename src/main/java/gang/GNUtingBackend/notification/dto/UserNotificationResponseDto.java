package gang.GNUtingBackend.notification.dto;

import gang.GNUtingBackend.notification.entity.UserNotification;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class UserNotificationResponseDto {
    private String notification;

    public static UserNotificationResponseDto toDto(UserNotification notification) {
       return UserNotificationResponseDto.builder()
                .notification(notification.getNotification())
               .build();
    }

}
