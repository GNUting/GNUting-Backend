package gang.GNUtingBackend.user.domain;

import gang.GNUtingBackend.user.domain.enums.Gender;
import gang.GNUtingBackend.user.domain.enums.UserRole;
import gang.GNUtingBackend.user.dto.UserUpdateRequestDto;
import java.time.LocalDate;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
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
    @Column(nullable = false)
    private String nickname;

    // 학과
    @Column(nullable = false)
    private String department;

    // 학번
    @Column(nullable = false)
    private String studentId;

    // 프로필 이미지
    private String profileImage;

    // 사용자 역할
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    // 사용자 한줄 소개
    private String userSelfIntroduction;

    public void update(String profileImage, String nickname, String password, String department, String userSelfIntroduction) {
        this.profileImage = profileImage;
        this.nickname = nickname;
        this.password = password;
        this.department = department;
        this.userSelfIntroduction = userSelfIntroduction;
    }
}
