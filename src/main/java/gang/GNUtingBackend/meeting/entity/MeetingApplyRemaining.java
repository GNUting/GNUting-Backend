//package gang.GNUtingBackend.meeting.entity;
//
//
//import gang.GNUtingBackend.board.entity.BaseTime;
//import gang.GNUtingBackend.user.domain.User;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//import javax.persistence.*;
//
//@Entity
//@AllArgsConstructor
//@NoArgsConstructor
//@Getter
//@Builder
//public class MeetingApplyRemaining extends BaseTime {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @JoinColumn(name = "user_id")
//    @OneToOne
//    private User userId;
//
//    @JoinColumn(name = "meeting_id")
//    @OneToOne
//    private Meeting meetingId;
//
//    @Column(nullable = false)
//    private int remaining;
//
//    public void minusRemaining(){
//        remaining--;
//    }
//}
