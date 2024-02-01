package gang.GNUtingBackend.dto;

import gang.GNUtingBackend.entity.Board;
import gang.GNUtingBackend.entity.BoardUser;
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
    private int userId;

    public BoardUser toEntity(){
       return BoardUser.builder()
               .boardId(boradId)
               .userId(userId)
               .build();
    }

    public static BoardUserDto toDto(Board board,int user){
        return BoardUserDto.builder()
                .boradId(board)
                .userId(user)
                .build();
    }
}
