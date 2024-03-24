package gang.GNUtingBackend.chat.dto;

import gang.GNUtingBackend.chat.domain.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ChatRequestDto {
    MessageType messageType;
    private String message;
}
