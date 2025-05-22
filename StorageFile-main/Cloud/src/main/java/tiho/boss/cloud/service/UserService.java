package tiho.boss.cloud.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import tiho.boss.cloud.model.User;

public interface UserService extends UserDetailsService {

    User findByLogin(String login);
    User findByToken(String token);
    User register(String login, String password);

    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

}