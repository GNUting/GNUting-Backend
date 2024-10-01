package gang.GNUtingBackend.user.domain;

import gang.GNUtingBackend.board.entity.ApplyUsers;
import gang.GNUtingBackend.board.entity.Board;
import gang.GNUtingBackend.board.entity.BoardApplyLeader;
import gang.GNUtingBackend.board.entity.BoardParticipant;
import gang.GNUtingBackend.chat.domain.ChatRoomUser;
import gang.GNUtingBackend.eventFunction.entity.EventApply;
//import gang.GNUtingBackend.meeting.entity.Meeting;
//import gang.GNUtingBackend.meeting.entity.MeetingApplyRemaining;
import gang.GNUtingBackend.memoThing.entity.Memo;
import gang.GNUtingBackend.memoThing.entity.MemoApplyRemaining;
import gang.GNUtingBackend.notification.entity.FCM;
import gang.GNUtingBackend.notification.entity.UserNotification;
import gang.GNUtingBackend.notification.entity.enums.NotificationSetting;
import gang.GNUtingBackend.user.domain.enums.Gender;
import gang.GNUtingBackend.user.domain.enums.UserRole;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.*;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@Setter
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 이메일
    @Column(nullable = false, unique = true)
    private String email;

    // 비밀번호
    @Column(nullable = false)
    private String password;

    // 이름
    @Column(nullable = false)
    private String name;

    // 전화번호
    @Column(nullable = false)
    private String phoneNumber;

    // 성별
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    // 생년월일
    @Column(nullable = false)
    private LocalDate birthDate;

    // 닉네임
    @Column(nullable = false, length = 10)
    @Size(max = 10, message = "닉네임은 최대 10자까지 가능합니다.")
    private String nickname;

    // 학과
    @Column(nullable = false)
    private String department;

    // 학번
    @Column(nullable = false, length = 4)
    @Pattern(regexp = "^\\d{2}$", message = "학번은 2자리 숫자여야 합니다.")
    private String studentId;

    // 프로필 이미지
    private String profileImage;

    // 사용자 역할
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Column(nullable = true)
    private String mbti;

    @Column(nullable = true)
    private String hobby;

    @Column(nullable = true)
    private String drink;

    @Column(nullable = true)
    private String smoke;



    // 사용자 한줄 소개
    @Column(length = 30)
    @Size(max = 30, message = "한 줄 소개는 최대 30자까지 가능합니다.")
    private String userSelfIntroduction;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationSetting notificationSetting;

    

    @OneToMany(mappedBy = "userId",cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<UserNotification> userNotifications;

    @OneToMany(mappedBy = "userId",cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Board> boards;

    @OneToMany(mappedBy = "userId",cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<FCM> fcms;

    @OneToMany(mappedBy = "userId",cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<BoardParticipant> boardParticipants;

    @OneToMany(mappedBy = "userId",cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ApplyUsers> applyUsersList;

    @OneToMany(mappedBy = "leaderId",cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<BoardApplyLeader> boardApplyLeaders;

    @OneToMany(mappedBy = "user",cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ChatRoomUser> chatRoomUsers;

    @OneToMany(mappedBy = "userId",cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Memo> memos;

    @OneToOne(mappedBy = "userId",cascade = CascadeType.REMOVE, orphanRemoval = true)
    private MemoApplyRemaining memoApplyRemaining;

//    @OneToMany(mappedBy = "userId",cascade = CascadeType.REMOVE, orphanRemoval = true)
//    private List<Meeting> meetings;

//    @OneToOne(mappedBy = "userId",cascade = CascadeType.REMOVE, orphanRemoval = true)
//    private MeetingApplyRemaining meetingApplyRemaining;

    @OneToOne(mappedBy = "userId",cascade = CascadeType.REMOVE, orphanRemoval = true)
    private EventApply eventApply;



    public void update(String profileImage, String nickname, String department, String userSelfIntroduction,String drink,String hobby,String mbti,String smoke) {
        this.profileImage = profileImage;
        this.nickname = nickname;
        this.department = department;
        this.userSelfIntroduction = userSelfIntroduction;
        this.smoke=smoke;
        this.drink=drink;
        this.hobby=hobby;
        this.mbti=mbti;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateNotificationSetting(NotificationSetting notificationSetting) {
        this.notificationSetting = notificationSetting;
    }
}
