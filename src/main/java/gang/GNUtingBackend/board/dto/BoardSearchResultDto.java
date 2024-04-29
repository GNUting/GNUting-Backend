package gang.GNUtingBackend.board.dto;

import gang.GNUtingBackend.board.entity.enums.Status;
import java.awt.image.TileObserver;
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

    public BoardSearchResultDto(Long boardId, String title, String department, String studentId, int inUserCount) {
        this.boardId = boardId;
        this.title = title;
        this.department = department;
        this.studentId = studentId + "학번";
        this.inUserCount = inUserCount;
    }
}
