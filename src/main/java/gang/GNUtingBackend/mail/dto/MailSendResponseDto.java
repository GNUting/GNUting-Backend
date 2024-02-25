package gang.GNUtingBackend.mail.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MailSendResponseDto {

    private String number;

    public void setNumber(String number) {
        this.number = number;
    }
}
