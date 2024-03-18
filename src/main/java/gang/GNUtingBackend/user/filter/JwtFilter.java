package gang.GNUtingBackend.user.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import gang.GNUtingBackend.exception.handler.TokenHandler;
import gang.GNUtingBackend.response.ApiResponse;
import gang.GNUtingBackend.user.token.TokenProvider;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    private final ObjectMapper objectMapper;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        try {
            String token = resolveToken(httpServletRequest);

            if (token != null && tokenProvider.validateToken(token)) {
                Authentication authentication = tokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (TokenHandler e) {
            logger.error(e.getMessage());
            // 만료된 토큰에 대한 응답 설정
            httpServletResponse.setStatus(e.getErrorReasonHttpStatus().getHttpStatus().value());
            httpServletResponse.setContentType("application/json;charset=UTF-8");
            ApiResponse<Object> apiResponse = ApiResponse.onFailure(e.getErrorReason().getCode(), e.getErrorReason().getMessage());
            String json = objectMapper.writeValueAsString(apiResponse);
            httpServletResponse.getWriter().write(json);
            return; // 필터 체인의 나머지 부분을 실행하지 않고 반환
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