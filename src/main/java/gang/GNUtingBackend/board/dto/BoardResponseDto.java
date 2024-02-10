package gang.GNUtingBackend.board.dto;

import gang.GNUtingBackend.board.entity.Board;
import gang.GNUtingBackend.board.entity.BoardParticipant;
import gang.GNUtingBackend.board.entity.enums.Status;
import gang.GNUtingBackend.user.domain.User;
import gang.GNUtingBackend.user.domain.enums.Gender;
import gang.GNUtingBackend.user.dto.UserSearchResponseDto;
import lombok.*;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class BoardResponseDto {
    private Long id;
    private String title;
    private String detail;
    private List<UserSearchResponseDto> inUser;
    private String nickname;
    private Status status;
    private Gender gender;



    public static BoardResponseDto toDto(Board board,List<UserSearchResponseDto> user){

        return BoardResponseDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .inUser(user)
                .detail(board.getDetail())
                .status(board.getStatus())
                .nickname(board.getUserId().getNickname())
                .gender(board.getGender())
                .build();
    }

}

