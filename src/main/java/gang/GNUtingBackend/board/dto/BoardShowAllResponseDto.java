package gang.GNUtingBackend.board.dto;

import com.google.firebase.database.core.UserWriteRecord;
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

