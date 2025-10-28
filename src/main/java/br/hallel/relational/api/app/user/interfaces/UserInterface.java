package br.hallel.relational.api.app.user.interfaces;

import br.hallel.relational.api.app.user.dto.*;
import br.hallel.relational.api.app.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface UserInterface {
    User singUpUser(UserLoginDTO userRequestDTO);
    User loginUser(UserLoginDTO userRequestDTO);
    UserProfileResponseWithToken editProfile(UUID idUser, UserEditProfileDTO user);
    User getUserById(UUID idUser);
    UserProfileResponse editImageProfile(UUID idUser, MultipartFile fileImageUrl);
    UserProfileResponse getUserProfile(UUID idUser, UUID idEventScale);
    Page<UserProfileResponseWithRole> listAllUsers(int page, int size, String nameFiltered,
                                                   FilterAuthorietiesDTO filterAuthorietiesDTO);
    Page<UserProfileResponseWithRole> listAllUsersByName(String name, int page, int size);

    UserProfileResponse getUserProfileByToken(String token);
}
