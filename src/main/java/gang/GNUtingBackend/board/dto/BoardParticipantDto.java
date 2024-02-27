package gang.GNUtingBackend.board.dto;

import gang.GNUtingBackend.board.entity.BoardParticipant;
import gang.GNUtingBackend.board.entity.Board;
import gang.GNUtingBackend.user.domain.User;
import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class BoardParticipantDto {
    private Long id;
    private Board boardId;
    private User userId;

    public BoardParticipant toEntity() {
        return BoardParticipant.builder()
                .boardId(boardId)
                .userId(userId)
                .build();
    }

    public static BoardParticipantDto toDto(Board board, User user) {
        return BoardParticipantDto.builder()
                .boardId(board)
                .userId(user)
                .build();
    }
}
