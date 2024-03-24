package gang.GNUtingBackend.chat.dto;

import gang.GNUtingBackend.chat.domain.enums.MessageType;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatResponseDto {
    private Long id;
    private Long chatRoomId;
    private MessageType messageType;
    private String email;
    private String profileImage;
    private String nickname;
    private String message;
    private LocalDateTime createdDate;
}
