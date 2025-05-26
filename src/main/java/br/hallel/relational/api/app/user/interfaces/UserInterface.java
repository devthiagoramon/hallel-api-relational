package br.hallel.relational.api.app.user.interfaces;

import br.hallel.relational.api.app.user.dto.UserEditProfileDTO;
import br.hallel.relational.api.app.user.dto.UserProfileResponse;
import br.hallel.relational.api.app.user.dto.UserLoginDTO;
import br.hallel.relational.api.app.user.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface UserInterface {
    User singUpUser(UserLoginDTO userRequestDTO);
    User loginUser(UserLoginDTO userRequestDTO);
    UserProfileResponse editProfile(UUID idUser, UserEditProfileDTO user);
    User getUserById(UUID idUser);
    UserProfileResponse editImageProfile(UUID idUser, MultipartFile fileImageUrl);
    UserProfileResponse getUserProfile(UUID idUser);
    List<UserProfileResponse> listAllUsers();

    UserProfileResponse getUserProfileByToken(String token);
}
