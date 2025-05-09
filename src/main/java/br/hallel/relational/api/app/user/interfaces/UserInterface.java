package br.hallel.relational.api.app.user.interfaces;

import br.hallel.relational.api.app.user.dto.UserLoginDTO;
import br.hallel.relational.api.app.user.model.User;

public interface UserInterface {
    User singUpUser(UserLoginDTO userRequestDTO);
    User loginUser(UserLoginDTO userRequestDTO);

}
