package gang.GNUtingBackend.board.dto;

import gang.GNUtingBackend.board.entity.enums.Status;
import java.awt.image.TileObserver;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BoardSearchResultDto {

    private Long boardId;

    // 게시글 제목
    private String title;

    // 작성자 학과
    private String department;

    // 작성자 학번
    private String studentId;

    // 참여 인원 수
    private int inUserCount;

    // 현재 글의 상태
    private Status status;

    private String time;

    public BoardSearchResultDto(Long boardId, String title, String department, String studentId, int inUserCount,
                                Status status, LocalDateTime createdTime) {
        this.boardId = boardId;
        this.title = title;
        this.department = department;
        this.studentId = studentId + "학번";
        this.inUserCount = inUserCount;
        this.status = status;
        this.time = getElapsedTime(createdTime);
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
