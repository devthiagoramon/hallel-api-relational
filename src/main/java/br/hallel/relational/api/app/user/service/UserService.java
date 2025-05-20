package br.hallel.relational.api.app.user.service;

import br.hallel.relational.api.app.global.service.google.GoogleBucketService;
import br.hallel.relational.api.app.global.utils.GoogleBucketUtils;
import br.hallel.relational.api.app.user.dto.UserEditProfileDTO;
import br.hallel.relational.api.app.user.dto.UserProfileResponse;
import br.hallel.relational.api.app.user.dto.UserLoginDTO;
import br.hallel.relational.api.app.user.dto.mapper.UserMapper;
import br.hallel.relational.api.app.user.exceptions.UserNotFoundException;
import br.hallel.relational.api.app.user.interfaces.UserInterface;
import br.hallel.relational.api.app.user.model.User;
import br.hallel.relational.api.app.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class UserService implements UserInterface {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private GoogleBucketService bucketService;

    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public User singUpUser(UserLoginDTO userRequestDTO) {
        return null;
    }

    @Override
    public User loginUser(UserLoginDTO userRequestDTO) {
        return null;
    }

    @Override
    public UserProfileResponse editProfile(UUID idUser, UserEditProfileDTO userDto) {
        log.info("Edit Profile...");

        User user = this.getUserById(idUser);
        user.setName(userDto.name());
        user.setEmail(userDto.email());
        user.setDateBirth(userDto.dateBirth());
        user.setPhoneNumber(userDto.phoneNumber());
        user.setCpf(userDto.cpf());
        LocalDate birthLocalDate = userDto.dateBirth().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        int age = Period.between(birthLocalDate, LocalDate.now()).getYears();
        user.setAge(age);

        User userUpdated = userRepository.save(user);
        log.info("Profile Updated successfully!");

        return userMapper.userEditProfileToResponse(userUpdated);
    }

    @Override
    public User getUserById(UUID idUser) {
        return this.userRepository.findById(idUser).orElseThrow(() -> new UserNotFoundException("User not find by id: " + idUser));
    }

    @Override
    public UserProfileResponse editImageProfile(UUID idUser, MultipartFile fileImageUrl) {
        log.info("Edit Image Profile...");
        User user = this.getUserById(idUser);

        String imageUrl = "";
        try {

            if (user.getFileImageUrl() != null) {
                imageUrl = this.bucketService.updateImageOfBucket(fileImageUrl,
                        GoogleBucketUtils.getImageName(user.getId().toString(), User.class.getSimpleName())
                );
            } else {
                imageUrl = this.bucketService.sendImageToBucket(
                        fileImageUrl,
                        GoogleBucketUtils.getImageName(user.getId().toString(), User.class.getSimpleName())
                );
            }
            user.setFileImageUrl(imageUrl);
            this.userRepository.save(user);
            log.info("Image Profile Updated successfully!");
            return userMapper.userEditProfileToResponse(user);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserProfileResponse getUserProfile(UUID idUser) {
        User userById = this.getUserById(idUser);
        return new UserProfileResponse(userById.getId(), userById.getName(), userById.getEmail(), userById.getPhoneNumber(), userById.getDateBirth(), userById.getFileImageUrl(), userById.getCpf());
    }

    @Override
    public List<UserProfileResponse> listAllUsers() {
        List<User> users = this.userRepository.findAll();
        List<UserProfileResponse> response = new ArrayList<>();
        for (User user : users) {
            response.add(new UserProfileResponse(user.getId(), user.getName(), user.getEmail(), user.getPhoneNumber(), user.getDateBirth(), user.getFileImageUrl(), user.getCpf()));
        }
        return response;
    }
}
