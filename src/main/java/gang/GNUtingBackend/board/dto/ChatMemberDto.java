package gang.GNUtingBackend.board.dto;

import gang.GNUtingBackend.board.entity.Board;
import gang.GNUtingBackend.board.entity.enums.ApplyStatus;
import gang.GNUtingBackend.user.domain.User;
import gang.GNUtingBackend.user.dto.UserSearchResponseDto;
import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class ChatMemberDto {
    private Board board;
    private String applyUserDepartment;
    private String participantUserDepartment;
    private List<User> applyUser;
    private List<User> participantUser;

    public static ChatMemberDto toDto(Board board, String applyUserDepartment, String participantUserDepartment,
                                      List<User> applyUser, List<User> participantUser) {
        return ChatMemberDto.builder()
                .board(board)
                .applyUserDepartment(applyUserDepartment)
                .participantUserDepartment(participantUserDepartment)
                .applyUser(applyUser)
                .participantUser(participantUser)
                .build();
    }
}
