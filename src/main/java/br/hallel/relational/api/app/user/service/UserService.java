package br.hallel.relational.api.app.user.service;

import br.hallel.relational.api.app.event.model.MemberEventScale;
import br.hallel.relational.api.app.event.repository.MemberEventScaleRepository;
import br.hallel.relational.api.app.global.service.google.GoogleBucketService;
import br.hallel.relational.api.app.global.utils.GoogleBucketUtils;
import br.hallel.relational.api.app.user.dto.*;
import br.hallel.relational.api.app.user.dto.mapper.UserMapper;
import br.hallel.relational.api.app.user.exceptions.UserNotFoundException;
import br.hallel.relational.api.app.user.interfaces.UserInterface;
import br.hallel.relational.api.app.user.model.User;
import br.hallel.relational.api.app.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@Service
public class UserService implements UserInterface {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private GoogleBucketService bucketService;

    @Autowired
    private MemberEventScaleRepository memberEventScaleRepository;

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
        if (userDto.dateBirth() != null) {
            user.setDateBirth(userDto.dateBirth());
        }
        user.setPhoneNumber(userDto.phoneNumber());
        user.setCpf(userDto.cpf());
        if (userDto.dateBirth() != null) {
            LocalDate birthLocalDate = userDto.dateBirth().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            int age = Period.between(birthLocalDate, LocalDate.now()).getYears();
            user.setAge(age);
        }

        User userUpdated = userRepository.save(user);
        log.info("Profile Updated successfully!");

        return userMapper.userEditProfileToResponse(userUpdated);
    }

    @Override
    public User getUserById(UUID idUser) {
        return this.userRepository.findById(idUser)
                .orElseThrow(() -> new UserNotFoundException("User not find by id: " + idUser));
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
    public UserProfileResponse getUserProfile(UUID idUser, UUID eventScaleId) {
        log.info("Get User Profile by id: {}...", idUser);
        Date date_view_invite = null;

        if (eventScaleId != null) {
            System.out.println("EventScaleId: " + eventScaleId);
            Optional<MemberEventScale> optional = this.memberEventScaleRepository.findByMemberMinistry_IdAndEventScale_Id(idUser,
                    eventScaleId);
            if (optional.isPresent()) {
                MemberEventScale member = optional.get();
                date_view_invite = member.getDate_view();
                System.out.println(date_view_invite);
            }
        }

        User userById = this.getUserById(idUser);

        UserProfileResponse user = new UserProfileResponse(userById.getId(), userById.getName(), userById.getEmail(),
                userById.getPhoneNumber(), userById.getDateBirth(), userById.getFileImageUrl(), userById.getCpf(),
                date_view_invite);
        System.out.println(user);
        return user;
    }

    public UserProfilePreferencesResponse getUserPreferences(UUID userId) {
        User user = this.getUserById(userId);
        return new UserProfilePreferencesResponse(user.getId(), user.getPushNotification());
    }

    public UserProfilePreferencesResponse updateUserPreferences(UUID userId, UserPreferencesDTO dto){
        User user = this.getUserById(userId);
        if (dto.pushNotification() != null){
            user.setPushNotification(!dto.pushNotification());
        }
        userRepository.save(user);
        return new UserProfilePreferencesResponse(user.getId(), user.getPushNotification());
    }


    @Override
    public List<UserProfileResponse> listAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = this.userRepository.findAll(pageable);
        List<UserProfileResponse> response = new ArrayList<>();
        for (User user : users) {
            response.add(new UserProfileResponse(user.getId(), user.getName(), user.getEmail(), user.getPhoneNumber(),
                    user.getDateBirth(), user.getFileImageUrl(), user.getCpf(), null));
        }
        return response;
    }

    @Override
    public List<UserProfileResponse> listAllUsersByName(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        List<UserProfileResponse> list = this.userRepository.findAllByNameContainingIgnoreCase(name, pageable);
        if (list.isEmpty()) {
            throw new UserNotFoundException("User not found by name: " + name);
        }

        return list;
    }

    @Override
    public UserProfileResponse getUserProfileByToken(String token) {
        log.info("Get User Profile By Token {}...", token);
        User user = this.userRepository.findByToken(token)
                .orElseThrow(() -> new UserNotFoundException("User not find by token: " + token));
        return new UserProfileResponse(user.getId(), user.getName(), user.getEmail(), user.getPhoneNumber(),
                user.getDateBirth(), user.getFileImageUrl(), user.getCpf(), null);
    }
}
