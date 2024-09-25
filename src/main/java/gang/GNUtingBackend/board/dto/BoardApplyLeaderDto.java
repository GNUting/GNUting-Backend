package gang.GNUtingBackend.board.dto;

import gang.GNUtingBackend.board.entity.ApplyUsers;
import gang.GNUtingBackend.board.entity.Board;
import gang.GNUtingBackend.board.entity.BoardApplyLeader;
import gang.GNUtingBackend.board.entity.enums.ApplyShowStatus;
import gang.GNUtingBackend.board.entity.enums.ApplyStatus;
import gang.GNUtingBackend.user.domain.User;
import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
//apply (신청할때만 사용하는(request) dto)
public class BoardApplyLeaderDto {
    //private Long id;
    private Board boardId;
    private User leaderId;
    private ApplyStatus status;
    private ApplyShowStatus applyShowStatus;
    private ApplyShowStatus receiveShowStatus;

    public BoardApplyLeader toEntity() {
        return BoardApplyLeader.builder()
                .boardId(boardId)
                .leaderId(leaderId)
                .status(this.status)
                .applyShowStatus(this.applyShowStatus)
                .receiveShowStatus(this.receiveShowStatus)
                .build();
    }

}
