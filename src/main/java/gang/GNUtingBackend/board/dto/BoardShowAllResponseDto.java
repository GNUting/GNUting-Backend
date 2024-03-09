package gang.GNUtingBackend.board.dto;

import gang.GNUtingBackend.board.entity.Board;
import gang.GNUtingBackend.board.entity.enums.Status;
import gang.GNUtingBackend.user.domain.enums.Gender;
import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter

//내가 쓴글 보기 및 전체 글 보기
public class BoardShowAllResponseDto {
    private Long id;
    private String title;
    private String detail;
    private Status status;
    private Gender gender;
    private BoardWriterInfoDto user;
    private int inUserCount;


    public static BoardShowAllResponseDto toDto(Board board) {
        return BoardShowAllResponseDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .detail(board.getDetail())
                .status(board.getStatus())
                .gender(board.getGender())
                .inUserCount(board.getInUserCount())
                .user(BoardWriterInfoDto.toDto(board.getUserId()))
                .build();
    }

}

