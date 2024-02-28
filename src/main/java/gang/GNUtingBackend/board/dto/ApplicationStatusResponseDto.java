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
    private Long id;
    private String applyUserDepartment;
    private String participantUserDepartment;
    private List<UserSearchResponseDto> applyUser;
    private List<UserSearchResponseDto> participantUser;
    private int applyUserCount;
    private int participantUserCount;

    public static ApplicationStatusResponseDto toDto(Long id,List<UserSearchResponseDto> participantUser, List<UserSearchResponseDto> applyUsers, String applyDepartment, String participantDepartment) {
        return ApplicationStatusResponseDto.builder()
                .id(id)
                .applyUser(applyUsers)
                .applyUserDepartment(applyDepartment)
                .participantUser(participantUser)
                .participantUserDepartment(participantDepartment)
                .applyUserCount(applyUsers.size())
                .participantUserCount(participantUser.size())
                .build();
    }
}
