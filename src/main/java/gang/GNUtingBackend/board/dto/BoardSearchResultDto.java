package gang.GNUtingBackend.board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardSearchResultDto {

    private Long BoardId;

    // 게시글 제목
    private String title;

    // 작성자 학과
    private String department;

    // 작성자 학번
    private String studentId;
}
