package gang.GNUtingBackend.notification.dto;

import gang.GNUtingBackend.user.domain.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestDto {
    private User targetToken;
    private String title;
    private String body;
}
