package gang.GNUtingBackend.notification.dto;

import gang.GNUtingBackend.notification.entity.UserNotification;
import gang.GNUtingBackend.notification.entity.enums.NotificationStatus;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class UserNotificationResponseDto {
    private Long id;
    private String title;
    private String body;
    private String time;
    private NotificationStatus status;
    private String location;
    private Long locationId;

    public static UserNotificationResponseDto toDto(UserNotification notification) {

        String elapsedTime = getElapsedTime(notification.getCreatedDate());
       return UserNotificationResponseDto.builder()
               .id(notification.getId())
               .title(notification.getTitle())
               .body(notification.getBody())
               .time(elapsedTime)
               .status(notification.getStatus())
               .location(notification.getLocation())
               .locationId(notification.getLocationId())
               .build();
    }

    private static String getElapsedTime(LocalDateTime createdTime) {
        LocalDateTime currentTime = LocalDateTime.now();
        Duration duration = Duration.between(createdTime, currentTime);
        long minutes = duration.toMinutes();

        if (minutes < 1) {
            return "방금 전";
        } else if (minutes < 60) {
            return minutes + "분 전";
        } else if (minutes < 1440) {
            long hours = minutes / 60;
            return hours + "시간 전";
        } else {
            long days = minutes / 1440;
            return days + "일 전";
        }
    }

}
