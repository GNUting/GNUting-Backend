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
public class BoardShowAllResponseDto {
    private Long id;
    private User userId;
    private String title;
    private String detail;
    private List<User> inUser;
    private Status status;
    private Gender gender;
    private int inUserCount;


    public static BoardShowAllResponseDto toDto(Board board) {
        return BoardShowAllResponseDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .detail(board.getDetail())
                .status(board.getStatus())
                .gender(board.getGender())
                .build();
    }

}

