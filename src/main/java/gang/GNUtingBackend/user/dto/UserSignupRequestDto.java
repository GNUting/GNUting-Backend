package gang.GNUtingBackend.user.dto;

import gang.GNUtingBackend.user.domain.enums.Gender;
import gang.GNUtingBackend.user.domain.enums.UserRole;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSignupRequestDto {

    private String email;
    private String password;
    private String name;
    private String phoneNumber;
    private Gender gender;
    private LocalDate birthDate;
    private String nickname;
    private String department;
    private String studentId;
    private String profileImage;
    private UserRole userRole;
}
