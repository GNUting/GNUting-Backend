package gang.GNUtingBackend.chat.dto;

import gang.GNUtingBackend.chat.domain.ChatRoom;
import gang.GNUtingBackend.chat.domain.ChatRoomUser;
import gang.GNUtingBackend.user.domain.User;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRoomUserDto {

    private Long id;
    private ChatRoom chatRoom;
    private User user;

    public List<ChatRoomUserDto> toDto(List<ChatRoomUser> chatRoomUsers) {
        return chatRoomUsers.stream()
                .map(cru -> ChatRoomUserDto.builder()
                        .id(cru.getId())
                        .chatRoom(cru.getChatRoom())
                        .user(cru.getUser())
                        .build())
                .collect(Collectors.toList());
    }
}
