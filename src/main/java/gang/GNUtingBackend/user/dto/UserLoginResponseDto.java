package gang.GNUtingBackend.user.dto;

import gang.GNUtingBackend.user.domain.enums.UserRole;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserLoginResponseDto {

    private final Long id;
    private final String email;
    private final String name;
    private final String department;
    private final String studentId;
    private final UserRole userRole;
    private final LocalDateTime createDate;
}
