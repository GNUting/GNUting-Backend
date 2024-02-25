package gang.GNUtingBackend.user.filter;

import gang.GNUtingBackend.user.token.TokenProvider;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

/**
 * jwt 토큰의 유효성을 검증하고, 인증하기 위한 클래스
 */
@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {

    private final TokenProvider tokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        try {
            // 헤더에서 토큰 가져오기
            String token = resolveToken((HttpServletRequest) request);

            // 토큰이 유효한 경우 Security Context에 인증 정보 설정
            if (token != null && tokenProvider.validateToken(token)) {
                Authentication authentication = tokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Security context에 설정되지 않음", e);
        }

        chain.doFilter(request, response);

    }

    /*
    HttpServeletRequest로 부터 Authorization 헤더를 추출하고,
    "Bearer " 접두어를 제거하고 실제 토큰값을 반환
     */
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}