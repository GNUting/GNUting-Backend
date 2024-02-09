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
public class BoardUserDto {
    private Long id;
    private Board boradId;
    private User userId;

    public BoardParticipant toEntity(){
       return BoardParticipant.builder()
               .boardId(boradId)
               .userId(userId)
               .build();
    }

    public static BoardUserDto toDto(Board board, User user){
        return BoardUserDto.builder()
                .boradId(board)
                .userId(user)
                .build();
    }
}
