package gang.GNUtingBackend.board.dto;

import gang.GNUtingBackend.board.entity.ApplyUsers;
import gang.GNUtingBackend.board.entity.Board;
import gang.GNUtingBackend.board.entity.BoardApplyLeader;
import gang.GNUtingBackend.board.entity.enums.ApplyStatus;
import gang.GNUtingBackend.user.domain.User;
import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class BoardApplyLeaderDto {
    private Long id;
    private Board boardId;
    private User leaderId;
    private ApplyStatus status;

    public BoardApplyLeader toEntity() {
        return BoardApplyLeader.builder()
                .boardId(boardId)
                .leaderId(leaderId)
                .status(this.status)
                .build();
    }


}
