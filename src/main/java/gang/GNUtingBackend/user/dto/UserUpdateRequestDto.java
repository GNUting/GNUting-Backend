package gang.GNUtingBackend.user.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserUpdateRequestDto {

    private String profileImage;
    private String nickname;
    private String password;
    private String department;
    private String userSelfIntroduction;

    public void setPassword(String password) {
        this.password = password;
    }
}
