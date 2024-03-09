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

    public void updateBoard(Long id,String title,String detail){
        this.id=id;
        this.title=title;
        this.detail=detail;
    }

}
