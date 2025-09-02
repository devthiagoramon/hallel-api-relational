package br.hallel.relational.api.app.user.service;

import br.hallel.relational.api.app.event.model.MemberEventScale;
import br.hallel.relational.api.app.event.repository.MemberEventScaleRepository;
import br.hallel.relational.api.app.global.service.google.GoogleBucketService;
import br.hallel.relational.api.app.global.utils.GoogleBucketUtils;
import br.hallel.relational.api.app.messaging.mobile.model.DeviceNotification;
import br.hallel.relational.api.app.messaging.mobile.repository.DeviceNotificationRepository;
import br.hallel.relational.api.app.messaging.mobile.service.FCMSenderService;
import br.hallel.relational.api.app.security.dto.TokenDTO;
import br.hallel.relational.api.app.security.model.Role;
import br.hallel.relational.api.app.security.utils.JwtTokenProvider;
import br.hallel.relational.api.app.user.dto.*;
import br.hallel.relational.api.app.user.dto.mapper.UserMapper;
import br.hallel.relational.api.app.user.exceptions.UserNotFoundException;
import br.hallel.relational.api.app.user.interfaces.UserInterface;
import br.hallel.relational.api.app.user.model.LastAccessLog;
import br.hallel.relational.api.app.user.model.User;
import br.hallel.relational.api.app.user.repository.LastAcessLogRepository;
import br.hallel.relational.api.app.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.*;
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
    @Autowired
    private LastAcessLogRepository lastAcessLogRepository;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private DeviceNotificationRepository deviceNotificationRepository;
    @Autowired
    private FCMSenderService fcmSenderService;

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
    public UserProfileResponseWithToken editProfile(UUID idUser, UserEditProfileDTO userDto) {
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
        TokenDTO newToken = jwtTokenProvider.createAccessToken(
                userUpdated.getId(),
                userUpdated.getEmail(),
                userUpdated.getRoles().stream().map(Role::getDescription).toList()
        );

        userUpdated.setToken(newToken.getAccessToken());
        userRepository.save(userUpdated);

        return new UserProfileResponseWithToken(
                newToken,
                userMapper.userEditProfileToResponse(userUpdated)
        );

    }

    @Override
    public User getUserById(UUID idUser) {
        return this.userRepository.findById(idUser)
                .orElseThrow(() -> new UserNotFoundException("user.not.found", idUser.toString()));
    }

    @Override
    public UserProfileResponse editImageProfile(UUID idUser, MultipartFile fileImageUrl) {
        log.info("Edit Image Profile...");
        User user = this.getUserById(idUser);

        String imageUrl = "";
        if (user.getFileImageUrl() != null) {
            imageUrl = this.bucketService.updateFileOfBucket(fileImageUrl,
                    GoogleBucketUtils.getImageName(user.getId().toString(), User.class.getSimpleName())
            );
        } else {
            imageUrl = this.bucketService.sendFileToBucket(
                    fileImageUrl,
                    GoogleBucketUtils.getImageName(user.getId().toString(), User.class.getSimpleName())
            );
        }
        user.setFileImageUrl(imageUrl);
        this.userRepository.save(user);
        log.info("Image Profile Updated successfully!");
        return userMapper.userEditProfileToResponse(user);

    }

    @Override
    public UserProfileResponse getUserProfile(UUID idUser, UUID eventScaleId) {
        log.info("Get User Profile by id: {}...", idUser);
        Date date_view_invite = null;

        if (eventScaleId != null) {
            System.out.println("EventScaleId: " + eventScaleId);
            Optional<MemberEventScale> optional = this.memberEventScaleRepository.findByMemberMinistry_User_IdAndEventScale_Id(
                    idUser,
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
                date_view_invite, null);
        System.out.println(user);
        return user;
    }

    public UserProfilePreferencesResponse getUserPreferences(UUID userId) {
        User user = this.getUserById(userId);
        return new UserProfilePreferencesResponse(user.getId(), user.getPushNotification());
    }

    public UserProfilePreferencesResponse updateUserPreferences(UUID userId, UserPreferencesDTO dto) {
        User user = this.getUserById(userId);
        if (dto.pushNotification() != null) {
            user.setPushNotification(!dto.pushNotification());
        }
        userRepository.save(user);
        return new UserProfilePreferencesResponse(user.getId(), user.getPushNotification());
    }


    @Override
    public Page<UserProfileResponse> listAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = this.userRepository.searchAllByOrderByNameAsc(pageable);
        return users.map((user -> {
            LocalDateTime lastAcessLog = this.getLastAcessLog(user);
            Date date = null;
            if (lastAcessLog != null) {
                date = Date.from(lastAcessLog.atZone(ZoneId.systemDefault()).toInstant());
            }
            return new UserProfileResponse(user.getId(), user.getName(), user.getEmail(), user.getPhoneNumber(),
                    user.getDateBirth(), user.getFileImageUrl(), user.getCpf(), null, date);
        }));
    }

    @Override
    public Page<UserProfileResponse> listAllUsersByName(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        //
//        if (pageResponse.isEmpty()) {
//            throw new UserNotFoundException("User not found by name: " + name);
//        }

        return this.userRepository.searchUserProfilesByName(name, pageable);
    }

    @Override
    public UserProfileResponse getUserProfileByToken(String token) {
        log.info("Get User Profile By Token {}...", token);
        User user = this.userRepository.findByToken(token)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado pelo token: {0}",
                        token.toString()));
        return new UserProfileResponse(user.getId(), user.getName(), user.getEmail(), user.getPhoneNumber(),
                user.getDateBirth(), user.getFileImageUrl(), user.getCpf(), null, null);
    }

    public UserEditProfileDTO editCPF(UUID idUser, String cpf) {

        User user =
                this.userRepository.findById(idUser).orElseThrow(() ->
                        new UserNotFoundException("user.not.found", idUser.toString()));

        if (cpf == null || cpf.isEmpty()) {
            throw new IllegalArgumentException("CPF cannot be null or empty");
        }

        user.setCpf(cpf);
        this.userRepository.save(user);

        return new UserEditProfileDTO(user.getName(), user.getEmail(), user.getPhoneNumber(),
                user.getDateBirth(), user.getCpf());
    }

    public void registerLastActivity(String email, String token) {
        User user = (email != null)
                ? userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("user.not.found.email",
                        email.toString()))
                : userRepository.findByToken(token)
                .orElseThrow(() -> new UserNotFoundException("user.not.found.token", token.toString()));

        LocalDateTime lastAccess = lastAcessLogRepository.findLastAccessDateByUser(user);

        boolean shouldLog = (lastAccess == null)
                || Duration.between(lastAccess, LocalDateTime.now()).toHours() >= 1;

        if (shouldLog) {
            LastAccessLog log = new LastAccessLog(user, LocalDateTime.now());
            System.out.println("Last Access Log: " + log.getAccessedAt());
            lastAcessLogRepository.save(log);
        }

    }

    public LocalDateTime getLastAcessLog(User user) {
        LocalDateTime lastAcess =
                this.lastAcessLogRepository.findLastAccessDateByUser(user);
//        System.out.println("Get Last Access: " + lastAcess + " | Name: " + user.getName());
        return lastAcess;
    }

    public void sendNotificationBirthDayMessage(User user) {
        System.out.println("Send Notification of Missing Users: " + user.getName());
        List<DeviceNotification> devicesUser = user.getDevicesUser();

        devicesUser.forEach(device -> {
            fcmSenderService.sendNotification(
                    device.getFcmToken(),
                    "\uD83C\uDF89 Parabéns, %s!".formatted(user.getName().trim()),
                    "Deus tem visto o seu esforço! Continue firme — sua dedicação faz a diferença na obra." +
                            "\nQue Ele te abençoe grandemente nessa jornada! \uD83D\uDE4C",
                    Map.of(
                            "type", "open_app",
                            "action", "birth_day_notification"
                    )
            );
        });
        System.out.println("Notification sending with success!");
    }

    public void sendNotificationOfMissingUsers(User user) {
        System.out.println("Send Notification of Missing Users: " + user.getName());
        List<DeviceNotification> devicesUser = user.getDevicesUser();

        devicesUser.forEach(device -> {
            fcmSenderService.sendNotification(
                    device.getFcmToken(),
                    "Sua presença faz a diferença!",
                    "Acesse o app Hallel e veja onde você pode servir. Deus tem algo preparado pra você.",
                    Map.of(
                            "type", "open_app",
                            "action", "reminder_notification"
                    )
            );
        });
        System.out.println("Notification sending with success!");
    }

}
