package gang.GNUtingBackend.entity;

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
public class BoardUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name="board_id")
    private Board boardId;

    @Column
    //@ManyToOne
    //@JoinColumn(name="user_id") 유저만들어지면 이걸로 수정
    private int userId;

    @Column
    private int status;

}
