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
    private final String age;
    private final String nickname;
    private final String department;
    private final String profileImage;
    private final String studentId;
    private UserRole userRole;

    // 한줄소개 추가
    public static UserSearchResponseDto toDto(User user){
        return UserSearchResponseDto.builder()
                .id(user.getId())
                .gender(user.getGender())
                .age(LocalDate.now().getYear()-user.getBirthDate().getYear()+1+"살")
                .nickname(user.getNickname())
                .department(user.getDepartment())
                .profileImage(user.getProfileImage())
                .userRole(user.getUserRole())
                .studentId(user.getStudentId().substring(2,4)+"학번")
                .build();
    }
}
