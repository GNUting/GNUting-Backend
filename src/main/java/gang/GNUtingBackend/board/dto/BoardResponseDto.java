package gang.GNUtingBackend.board.dto;

import gang.GNUtingBackend.board.entity.Board;
import gang.GNUtingBackend.board.entity.enums.Status;
import gang.GNUtingBackend.user.domain.enums.Gender;
import gang.GNUtingBackend.user.dto.UserSearchResponseDto;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;
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
    private BoardWriterImageInfoDto user;
    private Status status;
    private Gender gender;
    private int inUserCount;
    private String time;

    public static BoardResponseDto toDto(Board board, List<UserSearchResponseDto> user) {

        String elapsedTime = getElapsedTime(board.getCreatedDate());

        return BoardResponseDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .inUser(user)
                .detail(board.getDetail())
                .status(board.getStatus())
                .user(BoardWriterImageInfoDto.toDto(board.getUserId()))
                .gender(board.getGender())
                .inUserCount(board.getInUserCount())
                .time(elapsedTime)
                .build();
    }


    //반환할때 시간설정 메소드
    private static String getElapsedTime(LocalDateTime createdTime) {
        LocalDateTime currentTime = LocalDateTime.now();
        Duration duration = Duration.between(createdTime, currentTime);
        long minutes = duration.toMinutes();

        if (minutes < 1) {
            return "방금 전";
        } else if (minutes < 60) {
            return minutes + "분 전";
        } else if (minutes < 1440) {
            long hours = minutes / 60;
            return hours + "시간 전";
        } else {
            long days = minutes / 1440;
            return days + "일 전";
        }
    }

}

