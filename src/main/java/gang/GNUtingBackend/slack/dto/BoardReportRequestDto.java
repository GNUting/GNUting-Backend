package gang.GNUtingBackend.slack.dto;

import gang.GNUtingBackend.slack.domain.ReportCategory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
/**
 * 게시물에 신고 버튼을 눌렀을때 해당 게시물의 작성자를 신고하는데 사용되는 dto
 */
public class BoardReportRequestDto {

    private Long postId;
    private ReportCategory reportCategory;
    private String reportReason;
}
