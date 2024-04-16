package gang.GNUtingBackend.notification.dto;

import gang.GNUtingBackend.notification.entity.enums.NotificationSetting;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationSettingDto {

    private NotificationSetting notificationSetting;
}
