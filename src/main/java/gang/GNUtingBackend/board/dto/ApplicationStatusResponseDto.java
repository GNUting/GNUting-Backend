package gang.GNUtingBackend.board.dto;

import gang.GNUtingBackend.user.dto.UserSearchResponseDto;
import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class ApplicationStatusResponseDto {
    private String applyUserDepartment;
    private String participantUserDepartment;
    private List<UserSearchResponseDto> applyUser;
    private List<UserSearchResponseDto> participantUser;
    private int applyUserCount;
    private int participantUserCount;

    public static ApplicationStatusResponseDto toDto(List<UserSearchResponseDto> participantUser, List<UserSearchResponseDto> applyUsers, String applyDepartment, String participantDepartment) {
        return ApplicationStatusResponseDto.builder()
                .applyUser(applyUsers)
                .applyUserDepartment(applyDepartment)
                .participantUser(participantUser)
                .participantUserDepartment(participantDepartment)
                .applyUserCount(applyUsers.size())
                .participantUserCount(participantUser.size())
                .build();
    }
}
