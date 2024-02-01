package gang.GNUtingBackend.dto;

import gang.GNUtingBackend.entity.Board;
import lombok.*;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class BoardDto {
    private Long id;
    private int userId; //외래키로 변경
    private String title;
    private String detail;
    private List<Integer> inUser;
    private int status; //추후 enum으로 바꿀지 생각


    public Board toEntity(){
        return Board.builder()
                .id(id)
                .userId(userId)
                .title(title)
                .detail(detail)
                .status(status)
                .build();
    }

    public static BoardDto toDto(Board board){
        return BoardDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .detail(board.getDetail())
                .status(board.getStatus())
                .build();
    }

}

