package gang.GNUtingBackend.board.dto;

import gang.GNUtingBackend.user.domain.User;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

//유저 이미지를 포함한 작성자 정보 (특정글 보기 할때 사용)
public class BoardWriterImageInfoDto {
    private String nickname;
    private String department;
    private String studentId;
    private String image;

    public static BoardWriterImageInfoDto toDto(User user){
       return BoardWriterImageInfoDto.builder()
                .department(user.getDepartment())
                .nickname(user.getNickname())
                .studentId(user.getStudentId().substring(2,4)+"학번")
                .image(user.getProfileImage())
                .build();
    }

}
