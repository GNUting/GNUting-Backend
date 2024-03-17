package gang.GNUtingBackend.user.token;

import gang.GNUtingBackend.exception.handler.TokenHandler;
import gang.GNUtingBackend.exception.handler.UserHandler;
import gang.GNUtingBackend.response.code.status.ErrorStatus;
import gang.GNUtingBackend.user.domain.Token;
import gang.GNUtingBackend.user.domain.User;
import gang.GNUtingBackend.user.repository.UserRepository;
import java.util.Set;
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
    private final RedisTemplate<String, String> redisTemplate;

    private String generateKey(String email, String refreshToken) {
        return String.format("refreshToken:%s:%s", email, refreshToken);
    }

    public void saveToken(String email, String refreshToken, String accessToken) {
        String key = generateKey(email, refreshToken);
        redisTemplate.opsForValue().set(key, accessToken, expiredDate, TimeUnit.MILLISECONDS);
    }

    public Token findTokenByRefreshToken(String refreshToken, String email) {
        String key = generateKey(email, refreshToken);
        String accessToken = redisTemplate.opsForValue().get(key);
        if (accessToken != null) {
            return new Token(email, refreshToken, accessToken, expiredDate);
        } else {
            throw new TokenHandler(ErrorStatus.INVALID_REFRESH_TOKEN);
        }
    }

    public User getUserByRefreshToken(String refreshToken, String email) {
        String key = generateKey(email, refreshToken);
        Token token = findTokenByRefreshToken(refreshToken, email);
        if (token.getExpiration() > 0) {
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        }
        throw new TokenHandler(ErrorStatus.INVALID_REFRESH_TOKEN);
    }

    public void updateToken(String refreshToken, String accessToken, long expiration, String email) {
        String key = generateKey(email, refreshToken);
        redisTemplate.opsForValue().set(key, accessToken, expiration, TimeUnit.MILLISECONDS);
    }

    public void logout(String refreshToken, String email) {
        String key = generateKey(email, refreshToken);
        Boolean result = redisTemplate.delete(key);
        if (Boolean.FALSE.equals(result)) {
            throw new TokenHandler(ErrorStatus.INVALID_REFRESH_TOKEN);
        }
    }

    public void deleteUserRefreshTokens(String email) {
        Set<String> keys = redisTemplate.keys("refreshToken:" + email + ":*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

}
