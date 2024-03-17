package gang.GNUtingBackend.board.entity;

import gang.GNUtingBackend.board.entity.enums.Status;
import gang.GNUtingBackend.user.domain.User;
import gang.GNUtingBackend.user.domain.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Board extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "user_id")
    @ManyToOne
    private User userId;

    // 글쓰기 제목
    @Column(nullable = false)
    private String title;

    // 글쓰기 내용
    @Column(nullable = false)
    private String detail;

    /**
     * 현재 글 상태
     * OPEN - 아무나 이 글에 채팅 신청 가능한 상태
     * CLOSE - 해당 글에 채팅방이 생성되어서 더 이상 채팅 신청이 불가능한 상태
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column
    @Enumerated(EnumType.STRING)
    private Gender gender;

    // 글쓰기 추가된 총 인원 수
    @Column
    private int inUserCount;

    @OneToMany(mappedBy = "boardId",cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<BoardApplyLeader> boardApplyLeader;

    @OneToMany(mappedBy = "boardId",cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<BoardParticipant> boardParticipant;

    public void updateBoard(Long id,String title,String detail){
        this.id=id;
        this.title=title;
        this.detail=detail;
    }

}
