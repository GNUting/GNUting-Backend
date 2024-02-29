package gang.GNUtingBackend.board.dto;


import gang.GNUtingBackend.board.entity.ApplyUsers;
import gang.GNUtingBackend.board.entity.Board;
import gang.GNUtingBackend.board.entity.BoardApplyLeader;
import gang.GNUtingBackend.board.entity.BoardParticipant;
import gang.GNUtingBackend.user.domain.User;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class ApplyUsersDto {
    private BoardApplyLeader boardApplyLeaderId;
    private User userId;

    public ApplyUsers toEntity() {
        return ApplyUsers.builder()
                .boardApplyLeaderId(boardApplyLeaderId)
                .userId(userId)
                .build();
    }
}
