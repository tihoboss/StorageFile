package tiho.boss.cloud.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import tiho.boss.cloud.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(JwtRequestFilter.class);
    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userService;

    public JwtRequestFilter(JwtTokenUtil jwtTokenUtil, UserService userService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        log.debug("Processing authentication for: {}", request.getRequestURL());

        String token = extractToken(request);
        if (token == null) {
            log.trace("No JWT token found in request");
            chain.doFilter(request, response);
            return;
        }

        try {
            String username = jwtTokenUtil.getUsernameFromToken(token);
            log.debug("Extracted username from token: {}", username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                log.debug("Loading user details for: {}", username);
                UserDetails userDetails = userService.loadUserByUsername(username);

                if (jwtTokenUtil.validateToken(token, userDetails)) {
                    log.info("Valid JWT token for user: {}", username);

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("Authenticated user: {}", username);
                }
            }
        } catch (Exception e) {
            log.error("Authentication error", e);
            SecurityContextHolder.clearContext();
        }

        chain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String token = request.getHeader("auth-token");
        if (StringUtils.hasText(token)) {
            token = token.trim();
            if (token.startsWith("Bearer ")) {
                return token.substring(7);
            }
            return token;
        }

        token = request.getHeader("Authorization");
        if (StringUtils.hasText(token)) {
            token = token.trim();
            if (token.startsWith("Bearer ")) {
                return token.substring(7);
            }
            return token;
        }

        return null;
    }
}