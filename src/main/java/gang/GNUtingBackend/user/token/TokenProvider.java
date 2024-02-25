package gang.GNUtingBackend.user.token;

import gang.GNUtingBackend.user.domain.enums.UserRole;
import gang.GNUtingBackend.user.auth.PrincipalDetails;
import gang.GNUtingBackend.user.auth.PrincipalDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * JWT 토큰을 생성하고 관리하는 컴포넌트
 */
@Component
@Slf4j
public class TokenProvider {

    // 토큰 만료 시간 (밀리초 단위)
    private final long expiredDate = 60 * 60 * 1000;

    private final PrincipalDetailsService principalDetailsService;

    private Key key;

    public TokenProvider(
            @Value("${jwt.secret}") String secret,
            PrincipalDetailsService principalDetailsService) {
        this.principalDetailsService = principalDetailsService;

        // 시크릿 값을 decode해서 키 변수에 할당
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 사용자 이메일, 역할을 바탕으로 jwt 토큰 생성
     * @param email, 토큰의 subject
     * @param role,  토큰의 claim에 추가
     * @return 토큰 생성
     */
    public String createToken(String email, UserRole role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiredDate);

        return Jwts.builder()
                .setSubject(email)
                .claim("role", role.name()) // enum값을 스트링으로 변환
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    // 토큰을 파싱하여 사용자의 이메일(토큰의 subject)을 반환한다
    public String getUserEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * 토큰에서 사용자의 이메일을 추출하여 PrincipalDetails를 로드한 다음, UsernamePasswordAuthenticationToken을 생성하여 반환한다.
     * @param token
     * @return UsernamePasswordAuthenticationToken
     */
    public Authentication getAuthentication(String token) {
        PrincipalDetails userDetails = (PrincipalDetails) principalDetailsService.loadUserByUsername(
                this.getUserEmail(token));

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    /**
     * 토큰의 유효성을 검사한다.
     *
     * @param token
     * @return
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }
}
