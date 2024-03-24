package gang.GNUtingBackend.chat.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRoomResponse {

    private Long id;
    private String title;
    private String leaderUserDepartment;
    private String applyLeaderDepartment;

}
