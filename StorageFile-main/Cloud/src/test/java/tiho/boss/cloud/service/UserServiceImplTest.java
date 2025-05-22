package tiho.boss.cloud.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import tiho.boss.cloud.jwt.JwtTokenUtil;
import tiho.boss.cloud.model.User;
import tiho.boss.cloud.repository.UserRepository;
import tiho.boss.cloud.service.iml.UserServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setLogin("testuser");
        testUser.setPassword("encodedPassword");
    }

    @Test
    void loadUserByUsername_Success() {
        when(userRepository.findByLogin("testuser")).thenReturn(Optional.of(testUser));

        UserDetails userDetails = userService.loadUserByUsername("testuser");

        assertEquals("testuser", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        verify(userRepository, times(1)).findByLogin("testuser");
    }

    @Test
    void loadUserByUsername_UserNotFound() {
        when(userRepository.findByLogin("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("unknown");
        });
    }

    @Test
    void findByLogin_Success() {
        when(userRepository.findByLogin("testuser")).thenReturn(Optional.of(testUser));

        User result = userService.findByLogin("testuser");

        assertEquals("testuser", result.getLogin());
        verify(userRepository, times(1)).findByLogin("testuser");
    }

    @Test
    void findByToken_Success() {
        when(jwtTokenUtil.getUsernameFromToken("validToken")).thenReturn("testuser");
        when(userRepository.findByLogin("testuser")).thenReturn(Optional.of(testUser));

        User result = userService.findByToken("validToken");

        assertEquals("testuser", result.getLogin());
        verify(jwtTokenUtil, times(1)).getUsernameFromToken("validToken");
    }

    @Test
    void register_Success() {
        when(userRepository.findByLogin("newuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.register("newuser", "password");

        assertEquals("testuser", result.getLogin());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_UserAlreadyExists() {
        when(userRepository.findByLogin("existing")).thenReturn(Optional.of(testUser));

        assertThrows(IllegalArgumentException.class, () -> {
            userService.register("existing", "password");
        });
    }
}