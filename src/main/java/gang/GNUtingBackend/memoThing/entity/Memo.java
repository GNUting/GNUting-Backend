package gang.GNUtingBackend.memoThing.entity;

import gang.GNUtingBackend.board.entity.BaseTime;
import gang.GNUtingBackend.board.entity.enums.Status;
import gang.GNUtingBackend.user.domain.User;
import gang.GNUtingBackend.user.domain.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Memo extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @JoinColumn(name = "user_id")
    @ManyToOne
    private User userId;

    /**
     * 현재 글 상태
     * OPEN - 아무나 이 글에 채팅 신청 가능한 상태
     * CLOSE - 해당 글에 채팅방이 생성되어서 더 이상 채팅 신청이 불가능한 상태
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    public void closeState(){
        this.status=Status.CLOSE;
    }




}
