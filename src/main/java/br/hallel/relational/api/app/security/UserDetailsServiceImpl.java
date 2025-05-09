package br.hallel.relational.api.app.security;

import br.hallel.relational.api.app.security.exceptions.CredentialsAuthException;
import br.hallel.relational.api.app.security.model.Role;
import br.hallel.relational.api.app.user.model.User;
import br.hallel.relational.api.app.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws
            UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                                  .orElseThrow(() -> new CredentialsAuthException("User with this email does not exist"));

        return org.springframework.security.core.userdetails.User.builder()
                                                                 .username(user.getUsername())
                                                                 .password(user.getPassword())
                                                                 .authorities(user.getRoles()
                                                                                  .stream()
                                                                                  .map(Role::getDescription)
                                                                                  .toArray(String[]::new))
                                                                 .build();
    }
}
