package gang.GNUtingBackend.chat.dto;

import gang.GNUtingBackend.chat.domain.ChatRoom;
import gang.GNUtingBackend.chat.domain.ChatRoomUser;
import gang.GNUtingBackend.user.domain.User;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomUserDto {

    private Long id;
    private Long userId;
    private Long chatRoomId;
    private String nickname;
    private String profileImage;

    public List<ChatRoomUserDto> toDto(List<ChatRoomUser> chatRoomUsers) {
        return chatRoomUsers.stream()
                .map(cru -> ChatRoomUserDto.builder()
                        .id(cru.getId())
                        .userId(cru.getUser().getId())
                        .chatRoomId(cru.getChatRoom().getId())
                        .nickname(cru.getUser().getNickname())
                        .profileImage(cru.getUser().getProfileImage())
                        .build())
                .collect(Collectors.toList());
    }
}
