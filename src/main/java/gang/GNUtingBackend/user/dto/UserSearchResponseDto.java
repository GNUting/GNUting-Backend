package gang.GNUtingBackend.user.dto;

import gang.GNUtingBackend.user.domain.User;
import gang.GNUtingBackend.user.domain.enums.Gender;
import gang.GNUtingBackend.user.domain.enums.UserRole;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class UserSearchResponseDto {

    private final Long id;
    private final Gender gender;
    private final LocalDate birthDate;
    private final String nickname;
    private final String department;
    private final String profileImage;
    private UserRole userRole;


    public static UserSearchResponseDto toDto(User user){
        return UserSearchResponseDto.builder()
                .id(user.getId())
                .gender(user.getGender())
                .birthDate(user.getBirthDate())
                .nickname(user.getNickname())
                .department(user.getDepartment())
                .profileImage(user.getProfileImage())
                .userRole(user.getUserRole())
                .build();
    }
}
