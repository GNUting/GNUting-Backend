package gang.GNUtingBackend.user.dto.token;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class ReIssueTokenResponseDto {
    String accessToken;
}
