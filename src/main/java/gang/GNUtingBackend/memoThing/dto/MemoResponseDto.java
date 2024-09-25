package gang.GNUtingBackend.memoThing.dto;

import gang.GNUtingBackend.board.dto.BoardResponseDto;
import gang.GNUtingBackend.memoThing.entity.Memo;
import gang.GNUtingBackend.user.domain.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class MemoResponseDto {
    private Long id;
    private String content;
    private Gender gender;
//    private String time;

    public static MemoResponseDto toDto(Memo memo){
//        String elapsedTime = BoardResponseDto.getElapsedTime(memo.getCreatedDate());

        return MemoResponseDto.builder()
                .id(memo.getId())
                .content(memo.getContent())
                .gender(memo.getGender())
//                .time(elapsedTime)
                .build();

    }
}
