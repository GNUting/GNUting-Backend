//package gang.GNUtingBackend.meeting.entity;
//
//import gang.GNUtingBackend.board.entity.ApplyUsers;
//import gang.GNUtingBackend.board.entity.BaseTime;
//import gang.GNUtingBackend.board.entity.Board;
//import gang.GNUtingBackend.board.entity.enums.ApplyShowStatus;
//import gang.GNUtingBackend.board.entity.enums.ApplyStatus;
//import gang.GNUtingBackend.user.domain.User;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//import javax.persistence.*;
//import java.util.List;
//
//@Entity
//@AllArgsConstructor
//@NoArgsConstructor
//@Getter
//@Builder
//
//public class MeetingApplyLeader extends BaseTime {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "meeting_Id")
//    private Meeting meeting;
//
//    @JoinColumn(name = "meeting_user_Id")
//    @ManyToOne
//    private User meetingUserId;
//
//    @JoinColumn(name = "leader_id")
//    @ManyToOne
//    private User leaderId;
//
//    @Column
//    @Enumerated(EnumType.STRING)
//    private ApplyStatus status;
//
//    @Column
//    @Enumerated(EnumType.STRING)
//    private ApplyShowStatus applyShowStatus;
//
//    @Column
//    @Enumerated(EnumType.STRING)
//    private ApplyShowStatus receiveShowStatus;
//
//
//    public void setStatus(ApplyStatus applyStatus){
//        this.status=applyStatus;
//    }
//
//}