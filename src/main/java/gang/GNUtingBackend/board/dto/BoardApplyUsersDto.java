package gang.GNUtingBackend.board.dto;

import gang.GNUtingBackend.board.entity.Board;
import gang.GNUtingBackend.board.entity.BoardApplyUsers;
import gang.GNUtingBackend.board.entity.BoardParticipant;
import gang.GNUtingBackend.board.entity.enums.ApplyStatus;
import gang.GNUtingBackend.user.domain.User;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class BoardApplyUsersDto {
    private Long id;
    private Board boardId;
    private User userId;
    private User leader;
    private ApplyStatus status;

    public BoardApplyUsers toEntity(Board board,User leader,User userId){
       return BoardApplyUsers.builder()
               .boardId(board)
               .userId(userId)
               .leader(leader)
               .status(this.status)
               .build();
    }


}
