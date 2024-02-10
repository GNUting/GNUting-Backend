package gang.GNUtingBackend.board.dto;

import gang.GNUtingBackend.board.entity.Board;
import gang.GNUtingBackend.board.entity.enums.Status;
import gang.GNUtingBackend.user.domain.User;
import gang.GNUtingBackend.user.domain.enums.Gender;
import lombok.*;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class BoardRequestDto {
    private Long id;
    private User userId;
    private String title;
    private String detail;
    private List<User> inUser;
    private Status status;
    private Gender gender;



    public Board toEntity(){
        return Board.builder()
                .id(id)
                .userId(userId)
                .title(title)
                .detail(detail)
                .status(status)
                .gender(gender)
                .build();
    }

    public static BoardRequestDto toDto(Board board){
        return BoardRequestDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .detail(board.getDetail())
                .status(board.getStatus())
                .gender(board.getGender())
                .build();
    }

}

