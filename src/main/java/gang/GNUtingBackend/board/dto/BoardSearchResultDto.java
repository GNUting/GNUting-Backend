package gang.GNUtingBackend.board.dto;

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

    public BoardSearchResultDto(Long boardId, String title, String department, String studentId) {
        this.boardId = boardId;
        this.title = title;
        this.department = department;
        this.studentId = studentId.substring(2, 4) + "학번";
    }
}
