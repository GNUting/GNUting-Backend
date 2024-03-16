package gang.GNUtingBackend.board.dto;

import gang.GNUtingBackend.user.domain.User;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
//게시물 리스트들 볼 때 유저 이미지가 필요없을때 반환 dto
public class BoardWriterInfoDto {
    private String nickname;
    private String department;
    private String studentId;

    public static BoardWriterInfoDto toDto(User user){
       return BoardWriterInfoDto.builder()
                .department(user.getDepartment())
                .nickname(user.getNickname())
                .studentId(user.getStudentId()+"학번")
                .build();
    }

}
