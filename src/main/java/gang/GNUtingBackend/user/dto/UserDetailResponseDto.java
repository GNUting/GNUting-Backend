package gang.GNUtingBackend.user.dto;

import gang.GNUtingBackend.user.domain.enums.Gender;
import gang.GNUtingBackend.user.domain.enums.UserRole;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDetailResponseDto {

    private final Long id;
    private final String email;
    private final String name;
    private final String phoneNumber;
    private final Gender gender;
    private final LocalDate birthDate;
    private final String nickname;
    private final String department;
    private final String studentId;
    private final String profileImage;
    private UserRole userRole;
    private String userSelfIntroduction;
}
