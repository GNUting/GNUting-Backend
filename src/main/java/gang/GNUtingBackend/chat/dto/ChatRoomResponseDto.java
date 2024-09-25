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


    //사람들 이름 , 타이틀 대신 과팅인지 메모팅인지
    private Long id;
    private String title;
    private String leaderUserDepartment;
    private String applyLeaderDepartment;
    private List<String> ChatRoomUserProfileImages;
    private List<ChatRoomUserDto> chatRoomUsers;
    private boolean hasNewMessage;
    private LocalDateTime lastMessageTime;
    private String lastMessage;
}
