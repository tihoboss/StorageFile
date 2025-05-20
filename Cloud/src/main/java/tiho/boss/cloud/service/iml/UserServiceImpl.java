package tiho.boss.cloud.service.iml;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tiho.boss.cloud.model.User;
import tiho.boss.cloud.repository.UserRepository;
import tiho.boss.cloud.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with login: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getLogin(),
                user.getPassword(),
                Collections.emptyList()
        );
    }

    @Override
    public User findByLogin(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }
}