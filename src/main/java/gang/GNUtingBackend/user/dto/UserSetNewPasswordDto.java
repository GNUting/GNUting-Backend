package gang.GNUtingBackend.user.dto;

import lombok.Getter;

@Getter
public class UserSetNewPasswordDto {

    private String email;
    private String password;
}
