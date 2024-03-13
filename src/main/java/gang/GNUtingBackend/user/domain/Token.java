package gang.GNUtingBackend.user.domain;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Token {

    @Id
    private String email;

    private String refreshToken;

    private String accessToken;

    @TimeToLive(unit = TimeUnit.MILLISECONDS)
    private long expiration;

    public static String createRefreshToken() {
        return UUID.randomUUID().toString();
    }

}
