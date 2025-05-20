package tiho.boss.cloud.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import tiho.boss.cloud.jwt.JwtRequestFilter;
import tiho.boss.cloud.jwt.JwtTokenUtil;
import tiho.boss.cloud.service.UserService;

@Configuration
public class JwtConfig {

    @Bean
    public JwtRequestFilter jwtRequestFilter(
            JwtTokenUtil jwtTokenUtil,
            UserDetailsService userDetailsService) {
        return new JwtRequestFilter(jwtTokenUtil, (UserService) userDetailsService);
    }
}