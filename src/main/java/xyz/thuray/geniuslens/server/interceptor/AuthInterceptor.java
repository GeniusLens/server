package xyz.thuray.geniuslens.server.interceptor;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import xyz.thuray.geniuslens.server.util.JwtUtil;
import xyz.thuray.geniuslens.server.util.UserContext;

@Component
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取Bearer Token
        String token = request.getHeader("Authorization");
        log.info("token: {}", token);
        // 抹除可能出现的Bearer前缀
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        log.info("token: {}", token);
        // 解析Token
        Long userId = JwtUtil.parseToken(token);
        if (userId == null) {
            response.setStatus(401);
            return false;
        }
        log.info("userId: {}", 1L);
        UserContext.setUserId(userId);
        return true;
    }
}
