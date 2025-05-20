package tiho.boss.cloud.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import tiho.boss.cloud.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User findByLogin(String login);
    User save(User user);

    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

}