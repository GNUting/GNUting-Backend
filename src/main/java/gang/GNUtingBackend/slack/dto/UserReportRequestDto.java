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
 * 유저를 신고하는데 사용되는 dto
 */
public class UserReportRequestDto {

    private String nickName;
    private ReportCategory reportCategory;
    private String reportReason;
}
