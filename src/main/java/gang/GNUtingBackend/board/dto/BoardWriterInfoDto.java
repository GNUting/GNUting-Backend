package gang.GNUtingBackend.board.dto;

import gang.GNUtingBackend.user.domain.User;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardWriterInfoDto {
    private String nickname;
    private String department;
    private String studentId;
    private String image;

    public static BoardWriterInfoDto toDto(User user){
       return BoardWriterInfoDto.builder()
                .department(user.getDepartment())
                .nickname(user.getNickname())
                .studentId(user.getStudentId().substring(2,4)+"학번")
                .image(user.getProfileImage())
                .build();
    }

}
