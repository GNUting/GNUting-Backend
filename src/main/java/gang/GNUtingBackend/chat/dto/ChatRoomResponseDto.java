package gang.GNUtingBackend.chat.dto;

import gang.GNUtingBackend.chat.domain.ChatRoomUser;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRoomResponseDto {

    private Long id;
    private String title;
    private String leaderUserDepartment;
    private String applyLeaderDepartment;
    private Set<ChatRoomUser> chatroomUsers;

}
