package gang.GNUtingBackend.board.entity;

import gang.GNUtingBackend.board.entity.enums.ApplyShowStatus;
import gang.GNUtingBackend.board.entity.enums.ApplyStatus;
import gang.GNUtingBackend.user.domain.User;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder

public class BoardApplyLeader extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board boardId;

    @ManyToOne
    @JoinColumn(name = "leader_id")
    private User leaderId;

    @Column
    @Enumerated(EnumType.STRING)
    private ApplyStatus status;

    @Column
    @Enumerated(EnumType.STRING)
    private ApplyShowStatus applyShowStatus;

    @Column
    @Enumerated(EnumType.STRING)
    private ApplyShowStatus receiveShowStatus;

    @OneToMany(mappedBy = "boardApplyLeaderId",cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ApplyUsers> applyUsers;

    public void setStatus(ApplyStatus applyStatus){
        this.status=applyStatus;
    }

}
