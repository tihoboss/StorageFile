package tiho.boss.cloud.service.iml;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tiho.boss.cloud.jwt.JwtTokenUtil;
import tiho.boss.cloud.model.User;
import tiho.boss.cloud.repository.UserRepository;
import tiho.boss.cloud.service.UserService;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByLogin(username);
        return new org.springframework.security.core.userdetails.User(
                user.getLogin(),
                user.getPassword(),
                Collections.emptyList()
        );
    }

    @Override
    public User findByLogin(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with login: " + login));
    }

    @Override
    public User findByToken(String token) {
        String username = jwtTokenUtil.getUsernameFromToken(token);
        return findByLogin(username);
    }

    @Override
    public User register(String login, String password) {
        Optional<User> existingUser = userRepository.findByLogin(login);
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("User with login '" + login + "' already exists");
        }
        User newUser = new User();
        newUser.setLogin(login);
        newUser.setPassword(passwordEncoder.encode(password));
        return userRepository.save(newUser);
    }
}