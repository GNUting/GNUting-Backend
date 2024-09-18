package gang.GNUtingBackend.meeting.entity;

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
public class Meeting extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "user_id")
    @ManyToOne
    private User userId;
    @Column
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Column
    private String mbti;
    @Column
    private String birthday;
    @Column
    private String smoke;
    @Column
    private String drink;
    @Column
    private String hobby;
    @Column
    private String user_self_introduction;
    @Column
    @Enumerated(EnumType.STRING)

    private Status status;








}
