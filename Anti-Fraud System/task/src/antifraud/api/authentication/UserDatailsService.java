package antifraud.api.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDatailsService implements UserDetailsService {

    @Autowired
    UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findUser(username);

        if (user == null) {
            throw new UsernameNotFoundException("Not found: " + username);
        }

        antifraud.api.authentication.UserDetails userDetails = new antifraud.api.authentication.UserDetails(user);
        return userDetails;
    }
}
