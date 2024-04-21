package gang.GNUtingBackend.notification.dto;

import gang.GNUtingBackend.chat.domain.ChatRoomUser;
import gang.GNUtingBackend.chat.dto.ChatRoomUserDto;
import gang.GNUtingBackend.notification.entity.enums.NotificationSetting;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class NotificationChatSettingDto {

    private Long chatRoomId;
    private NotificationSetting notificationSetting;

    public static NotificationChatSettingDto toDto(ChatRoomUser chatRoomUser) {
        return NotificationChatSettingDto.builder()
                .chatRoomId(chatRoomUser.getChatRoom().getId())
                .notificationSetting(chatRoomUser.getNotificationSetting())
                .build();

    }
}
