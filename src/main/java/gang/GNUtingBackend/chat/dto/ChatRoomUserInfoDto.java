package gang.GNUtingBackend.chat.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRoomUserInfoDto {
    private Long chatRoomId;
    private List<ChatRoomUserDto> chatRoomUsers;
}
