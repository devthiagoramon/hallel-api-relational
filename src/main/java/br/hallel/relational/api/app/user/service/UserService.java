package br.hallel.relational.api.app.user.service;

import br.hallel.relational.api.app.user.dto.UserLoginDTO;
import br.hallel.relational.api.app.user.interfaces.UserInterface;
import br.hallel.relational.api.app.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserInterface {

    @Autowired
    private PasswordEncoder encoder;

    //private final JwtService jwtService;

    //private final AuthenticationManager authenticationManager;

    @Override
    public User singUpUser(UserLoginDTO userRequestDTO) {
        return null;
    }

    @Override
    public User loginUser(UserLoginDTO userRequestDTO) {
        return null;
    }
}
