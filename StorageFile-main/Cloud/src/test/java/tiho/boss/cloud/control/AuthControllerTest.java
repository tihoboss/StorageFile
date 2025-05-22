package tiho.boss.cloud.control;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import tiho.boss.cloud.controller.AuthController;
import tiho.boss.cloud.jwt.JwtTokenUtil;
import tiho.boss.cloud.model.User;
import tiho.boss.cloud.service.UserService;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private JwtTokenUtil jwtUtil;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_Success() {
        User newUser = new User();
        newUser.setLogin("testuser");

        when(userService.register(anyString(), anyString())).thenReturn(newUser);
        when(jwtUtil.generateToken(anyString())).thenReturn("generated-token");

        ResponseEntity<?> response = authController.register(
                Map.of("login", "testuser", "password", "password"));

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("generated-token",
                ((Map<?, ?>) response.getBody()).get("auth-token"));
    }

    @Test
    void register_MissingCredentials() {
        ResponseEntity<?> response = authController.register(
                Map.of("login", "testuser"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Login, password are required",
                ((Map<?, ?>) response.getBody()).get("message"));
    }

    @Test
    void login_Success() {
        Authentication auth = mock(Authentication.class);
        when(authManager.authenticate(any())).thenReturn(auth);
        when(jwtUtil.generateToken(anyString())).thenReturn("generated-token");

        ResponseEntity<?> response = authController.login(
                Map.of("login", "testuser", "password", "password"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("generated-token",
                ((Map<?, ?>) response.getBody()).get("auth-token"));
    }

    @Test
    void login_InvalidCredentials() {
        when(authManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        ResponseEntity<?> response = authController.login(
                Map.of("login", "testuser", "password", "wrong"));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Bad credentials",
                ((Map<?, ?>) response.getBody()).get("message"));
    }

    @Test
    void logout_Success() {
        ResponseEntity<?> response = authController.logout("valid-token");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}