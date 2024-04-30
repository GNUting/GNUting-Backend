package gang.GNUtingBackend.chat.dto;

import gang.GNUtingBackend.chat.domain.ChatRoomUser;
import java.time.LocalDateTime;
import java.util.List;

import gang.GNUtingBackend.notification.entity.enums.NotificationSetting;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRoomResponseDto {

    private Long id;
    private String title;
    private String leaderUserDepartment;
    private String applyLeaderDepartment;
    private List<String> ChatRoomUserProfileImages;
    private List<ChatRoomUserDto> chatRoomUsers;
    private boolean hasNewMessage;
    private LocalDateTime lastMessageTime;
}
