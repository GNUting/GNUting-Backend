package gang.GNUtingBackend.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder

public class ChatNotificationResponseDto {
    private String title;
    private String leaderUserDepartment;
    private String applyLeaderDepartment;
}
