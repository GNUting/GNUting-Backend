package gang.GNUtingBackend.board.dto;

import gang.GNUtingBackend.board.entity.Board;
import gang.GNUtingBackend.board.entity.enums.Status;
import gang.GNUtingBackend.user.domain.enums.Gender;
import java.time.Duration;
import java.time.LocalDateTime;
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
    private String time;


    public static BoardShowAllResponseDto toDto(Board board) {

        String elapsedTime = getElapsedTime(board.getCreatedDate());

        return BoardShowAllResponseDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .detail(board.getDetail())
                .status(board.getStatus())
                .gender(board.getGender())
                .inUserCount(board.getInUserCount())
                .time(elapsedTime)
                .user(BoardWriterInfoDto.toDto(board.getUserId()))
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

