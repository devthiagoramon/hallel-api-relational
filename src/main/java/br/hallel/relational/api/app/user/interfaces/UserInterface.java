package br.hallel.relational.api.app.user.interfaces;

import br.hallel.relational.api.app.user.dto.UserEditProfileDTO;
import br.hallel.relational.api.app.user.dto.UserEditProfileResponse;
import br.hallel.relational.api.app.user.dto.UserLoginDTO;
import br.hallel.relational.api.app.user.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface UserInterface {
    User singUpUser(UserLoginDTO userRequestDTO);
    User loginUser(UserLoginDTO userRequestDTO);
    UserEditProfileResponse editProfile(UUID idUser, UserEditProfileDTO user);
    User getUserById(UUID idUser);
    UserEditProfileResponse editImageProfile(UUID idUser, MultipartFile fileImageUrl);
}
