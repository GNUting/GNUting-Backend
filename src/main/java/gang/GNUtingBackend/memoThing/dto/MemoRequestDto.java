package gang.GNUtingBackend.memoThing.dto;

import gang.GNUtingBackend.board.entity.enums.Status;
import gang.GNUtingBackend.memoThing.entity.Memo;
import gang.GNUtingBackend.user.domain.User;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class MemoRequestDto {
    private String content;


    public static Memo toEntity(User user,MemoRequestDto memoRequestDto){
        return Memo.builder()
                .userId(user)
                .gender(user.getGender())
                .content(memoRequestDto.content)
                .status(Status.OPEN)
                .build();
    }
}
