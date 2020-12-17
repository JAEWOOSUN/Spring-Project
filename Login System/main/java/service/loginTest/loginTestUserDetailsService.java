package solis.pl.service.loginTest;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import solis.pl.domain.loginTest.loginTestUserDetails;
import solis.pl.repository.LoginTestMapper;

import javax.inject.Inject;

@Service
public class loginTestUserDetailsService implements UserDetailsService {

    @Inject
    LoginTestMapper loginTestMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        loginTestUserDetails user = loginTestMapper.getUserById(username);
        if(user==null){
            throw new UsernameNotFoundException(username);
        }
        return user;
    }
}
