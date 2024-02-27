package gang.GNUtingBackend.user.dto;

import gang.GNUtingBackend.user.domain.User;
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
    private String userSelfIntroduction;

    public User toEntity() {
        return User.builder()
                .email(email)
                .password(password)
                .name(name)
                .phoneNumber(phoneNumber)
                .gender(gender)
                .birthDate(birthDate)
                .nickname(nickname)
                .department(department)
                .studentId(studentId)
                .profileImage(profileImage)
                .userRole(UserRole.ROLE_USER)
                .userSelfIntroduction(userSelfIntroduction)
                .build();
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
