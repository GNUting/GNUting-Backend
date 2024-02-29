package gang.GNUtingBackend.board.entity;

import gang.GNUtingBackend.board.entity.enums.ApplyStatus;
import gang.GNUtingBackend.user.domain.User;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Setter
public class BoardApplyLeader {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    @OneToMany(mappedBy = "boardApplyLeaderId",cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ApplyUsers> applyUsers;


}
