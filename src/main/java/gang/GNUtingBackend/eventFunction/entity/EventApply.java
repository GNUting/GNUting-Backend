package gang.GNUtingBackend.eventFunction.entity;

import gang.GNUtingBackend.board.entity.enums.Status;
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
public class EventApply {
    @Id
    @GeneratedValue
    private Long id;

    @JoinColumn(name = "user_id")
    @OneToOne
    private User userId;

}
