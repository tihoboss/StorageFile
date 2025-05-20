package tiho.boss.cloud.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tiho.boss.cloud.jwt.JwtTokenUtil;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthenticationManager authManager;
    private final JwtTokenUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials, HttpServletResponse response) {
        log.info("Attempting login for user: {}", credentials.get("login"));
        try {
            String username = credentials.get("login");
            String password = credentials.get("password");

            if (username == null || password == null) {
                log.warn("Login attempt with missing credentials");
                return ResponseEntity.badRequest().body(Map.of("error", "Missing credentials"));
            }

            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            log.debug("Authentication successful for user: {}", username);

            String token = jwtUtil.generateToken(username);
            log.info("JWT token generated for user: {}", username);

            response.setHeader("Access-Control-Allow-Origin", "http://localhost:8081");
            response.setHeader("Access-Control-Expose-Headers", "auth-token");

            return ResponseEntity.ok()
                    .header("auth-token", token)
                    .body(Map.of("token", token, "login", username));

        } catch (BadCredentialsException e) {
            log.error("Invalid credentials for user: {}", credentials.get("login"), e);
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        } catch (Exception e) {
            log.error("Unexpected error during login", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Server error"));
        }
    }
}