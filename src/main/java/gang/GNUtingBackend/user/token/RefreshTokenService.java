package gang.GNUtingBackend.user.token;

import gang.GNUtingBackend.exception.handler.TokenHandler;
import gang.GNUtingBackend.exception.handler.UserHandler;
import gang.GNUtingBackend.response.code.status.ErrorStatus;
import gang.GNUtingBackend.user.domain.Token;
import gang.GNUtingBackend.user.domain.User;
import gang.GNUtingBackend.user.repository.UserRepository;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    // refreshToken 만료시간 (밀리초 단위)
    // 14일 (2주)
    private final long expiredDate = 60 * 60 * 1000 * 24 * 14;

    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public void saveToken(String email, String refreshToken, String accessToken) {
        Token token = Token.builder()
                .email(email)
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .expiration(expiredDate)
                .build();

        redisTemplate.opsForValue().set(refreshToken, token, expiredDate, TimeUnit.MILLISECONDS);
    }

    public Token findTokenByRefreshToken(String refreshToken) {
        Token token = (Token) redisTemplate.opsForValue().get(refreshToken);
        if (token != null) {
            return token;
        }
        throw new TokenHandler(ErrorStatus.INVALID_REFRESH_TOKEN);
    }

    public User getUserByRefreshToken(String refreshToken) {
        Token token = findTokenByRefreshToken(refreshToken);
        if (token.getExpiration() > 0) {
            String email = token.getEmail();
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        }
        throw new TokenHandler(ErrorStatus.INVALID_REFRESH_TOKEN);
    }

    public void updateToken(Token token) {
        redisTemplate.opsForValue().set(token.getRefreshToken(), token, token.getExpiration(), TimeUnit.MILLISECONDS);
    }

}
