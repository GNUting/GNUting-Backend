package gang.GNUtingBackend.board.entity;

import gang.GNUtingBackend.board.entity.enums.ApplyStatus;
import gang.GNUtingBackend.user.domain.User;
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
public class BoardApplyUsers {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board boardId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userId;

    @ManyToOne
    @JoinColumn(name = "leader_id")
    private User leader;

    @Column
    @Enumerated(EnumType.STRING)
    private ApplyStatus status;


}
