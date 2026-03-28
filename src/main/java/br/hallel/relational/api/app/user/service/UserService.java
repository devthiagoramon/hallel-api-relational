package br.hallel.relational.api.app.user.service;

import br.hallel.relational.api.app.auth.exception.AuthRequestException;
import br.hallel.relational.api.app.event.model.MemberEventScale;
import br.hallel.relational.api.app.event.repository.MemberEventScaleRepository;
import br.hallel.relational.api.app.global.service.google.GoogleBucketService;
import br.hallel.relational.api.app.global.utils.GoogleBucketUtils;
import br.hallel.relational.api.app.messaging.mobile.model.DeviceNotification;
import br.hallel.relational.api.app.messaging.mobile.service.FCMSenderService;
import br.hallel.relational.api.app.security.dto.TokenDTO;
import br.hallel.relational.api.app.security.model.Role;
import br.hallel.relational.api.app.security.repository.RoleRepository;
import br.hallel.relational.api.app.security.utils.JwtTokenProvider;
import br.hallel.relational.api.app.user.dto.*;
import br.hallel.relational.api.app.user.dto.mapper.UserMapper;
import br.hallel.relational.api.app.user.exceptions.RoleNotFoundException;
import br.hallel.relational.api.app.user.exceptions.UpdateRoleUserException;
import br.hallel.relational.api.app.user.exceptions.UserNotFoundException;
import br.hallel.relational.api.app.user.interfaces.UserInterface;
import br.hallel.relational.api.app.user.model.*;
import br.hallel.relational.api.app.user.repository.LastAcessLogRepository;
import br.hallel.relational.api.app.user.repository.UserRepository;
import br.hallel.relational.api.app.user.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserInterface {


    private final UserRepository userRepository;
    private final GoogleBucketService bucketService;
    private final MemberEventScaleRepository memberEventScaleRepository;
    private final LastAcessLogRepository lastAcessLogRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final FCMSenderService fcmSenderService;
    private final UserMapper userMapper;


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
                userById.getStatus(),
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
    public Page<UserProfileResponseWithRole> listAllUsers(int page, int size, String nameFiltered,
                                                          FilterAuthorietiesDTO filterAuthorietiesDTO) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = this.userRepository.searchAllByOrderByNameAsc(nameFiltered,
                filterAuthorietiesDTO != null ? filterAuthorietiesDTO.toString() : null, pageable);
        return users.map((user -> {
            Date date = getLastAccessDate(user);
            return new UserProfileResponseWithRole(user.getId(), user.getName(), user.getEmail(), user.getPhoneNumber(),
                    user.getDateBirth(), user.getFileImageUrl(), user.getCpf(), user.getStatus(), null, date,
                    user.getRoles().stream().map(Role::getDescription).toList());
        }));
    }

    @Override
    public Page<UserProfileResponseWithRole> listAllUsersByName(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        //
//        if (pageResponse.isEmpty()) {
//            throw new UserNotFoundException("User not found by name: " + name);
//        }

        Page<User> users = this.userRepository.searchUserProfilesByName(name, pageable);
        return users.map(user -> {
            Date date = getLastAccessDate(user);
            return new UserProfileResponseWithRole(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getPhoneNumber(),
                    user.getDateBirth(),
                    user.getFileImageUrl(),
                    user.getCpf(),
                    user.getStatus(),
                    null,
                    date,
                    user.getRoles().stream().map(Role::getDescription).toList()
            );
        });
    }

    @Override
    public UserProfileResponse getUserProfileByToken(String token) {
        log.info("Get User Profile By Token {}...", token);
        User user = this.userRepository.findByToken(token)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado pelo token: {0}",
                        token));
        return new UserProfileResponse(user.getId(), user.getName(), user.getEmail(), user.getPhoneNumber(),
                user.getDateBirth(), user.getFileImageUrl(), user.getCpf(), user.getStatus(), null, null);
    }

    public UserEditProfileDTO editCPF(UUID idUser, String cpf) {

        User user =
                this.userRepository.findById(idUser).orElseThrow(() ->
                        new UserNotFoundException("user.not.found", idUser.toString()));

        if (cpf == null || cpf.isEmpty()) {
            throw new IllegalArgumentException("O CPF não pode ser nulo e nem vazio");
        }

        user.setCpf(cpf.replaceAll("\\D", ""));
        this.userRepository.save(user);

        return new UserEditProfileDTO(user.getName(), user.getEmail(), user.getPhoneNumber(),
                user.getDateBirth(), user.getCpf());
    }

    public UserEditProfileDTO editPhoneNumber(UUID idUser, String phoneNumber) {

        User user =
                this.userRepository.findById(idUser).orElseThrow(() ->
                        new UserNotFoundException("user.not.found", idUser.toString()));

        if (phoneNumber == null || phoneNumber.isEmpty()) {
            throw new IllegalArgumentException("O número não pode ser nulo e nem vazio");
        }

        user.setPhoneNumber(phoneNumber.replaceAll("\\D", ""));
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

    public UserProfileResponse createUser(CreateEditUser dto) {
        log.info("Creating User: " + dto.getName());

        if (userRepository.
                findByEmail(dto.getEmail()).isPresent()) {
            throw new AuthRequestException("User already exists in Database");
        }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setDateBirth(dto.getDateBirth());
        user.setCpf(dto.getCpf());
        user.setStatus(UserAccountStatus.ENABLED);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        User userSaved = this.userRepository.save(user);
        Date date = getLastAccessDate(user);

        // Resolve roles: use the provided list or default to USER
        List<String> roleNames = (dto.getRoles() != null && !dto.getRoles().isEmpty())
                ? dto.getRoles()
                : List.of("USER");

        List<Role> rolesToAssign = roleRepository.findByDescriptionIn(roleNames);
        if (rolesToAssign.isEmpty()) {
            throw new RoleNotFoundException("Nenhum papel encontrado para atribuir ao usuário.");
        }

        for (Role role : rolesToAssign) {
            UserRoleIds userRoleIds = new UserRoleIds(userSaved.getId(), role.getId());
            userRoleRepository.save(new UserRole(userRoleIds));
        }

        return new UserProfileResponse(
                userSaved.getId(),
                userSaved.getName(),
                userSaved.getEmail(),
                userSaved.getPhoneNumber(),
                userSaved.getDateBirth(),
                userSaved.getFileImageUrl(),
                userSaved.getCpf(),
                user.getStatus(),
                null,
                date
        );
    }

    public UserProfileResponse editUser(UUID id, CreateEditUser dto) {
        log.info("Editing User: " + id);
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("user.not.found", id.toString()));
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setDateBirth(dto.getDateBirth());
        user.setCpf(dto.getCpf());

        // Sync roles if provided
        if (dto.getRoles() != null) {
            List<Role> newRoles = roleRepository.findByDescriptionIn(dto.getRoles());
            if (newRoles.size() != dto.getRoles().size()) {
                throw new RoleNotFoundException("Um ou mais papéis informados não foram encontrados.");
            }
            user.setRoles(new HashSet<>(newRoles));
        }

        User userSaved = this.userRepository.save(user);
        Date date = getLastAccessDate(user);
        return new UserProfileResponse(
                userSaved.getId(),
                userSaved.getName(),
                userSaved.getEmail(),
                userSaved.getPhoneNumber(),
                userSaved.getDateBirth(),
                userSaved.getFileImageUrl(),
                userSaved.getCpf(),
                user.getStatus(),
                null,
                date
        );
    }

    private Date getLastAccessDate(User user) {
        LocalDateTime lastAcessLog = this.getLastAcessLog(user);
        Date date = null;
        if (lastAcessLog != null) {
            date = Date.from(lastAcessLog.atZone(ZoneId.systemDefault()).toInstant());
        }
        return date;
    }

    public UserProfileResponse disableUser(UUID idUser) {
        log.info("Disabling User: " + idUser);
        User user = this.userRepository.findById(idUser)
                .orElseThrow(() -> new UserNotFoundException("user.not.found", idUser.toString()));

        user.setStatus(UserAccountStatus.DISABLED);
        User userSaved = this.userRepository.save(user);
        Date date = getLastAccessDate(user);
        return new UserProfileResponse(
                userSaved.getId(),
                userSaved.getName(),
                userSaved.getEmail(),
                userSaved.getPhoneNumber(),
                userSaved.getDateBirth(),
                userSaved.getFileImageUrl(),
                userSaved.getCpf(),
                user.getStatus(),
                null,
                date
        );
    }

    public UserProfileResponse activateUser(UUID idUser) {
        log.info("Activating User: " + idUser);
        User user = this.userRepository.findById(idUser)
                .orElseThrow(() -> new UserNotFoundException("user.not.found", idUser.toString()));

        user.setStatus(UserAccountStatus.ENABLED);
        User userSaved = this.userRepository.save(user);
        Date date = getLastAccessDate(user);
        return new UserProfileResponse(
                userSaved.getId(),
                userSaved.getName(),
                userSaved.getEmail(),
                userSaved.getPhoneNumber(),
                userSaved.getDateBirth(),
                userSaved.getFileImageUrl(),
                userSaved.getCpf(),
                user.getStatus(),
                null,
                date
        );
    }

    public UserProfileWithPasswordResponse getUserForAdmin(UUID idUser) {
        log.info("Listing user for admin with id: " + idUser);
        User userById = this.getUserById(idUser);

        return new UserProfileWithPasswordResponse(userById.getId(), userById.getName(), userById.getEmail(),
                userById.getPhoneNumber(), userById.getPassword(), userById.getDateBirth(), userById.getFileImageUrl(),
                userById.getCpf(),
                userById.getStatus(),
                null, null);

    }

    public UserEditProfileDTO editDateBirth(UUID userId, DateBirthUserDTO dto) {
        User user =
                this.userRepository.findById(userId).orElseThrow(() ->
                        new UserNotFoundException("user.not.found", userId.toString()));

        if (dto.getDateBirth() == null || dto.getDateBirth().isAfter(OffsetDateTime.now())) {
            throw new IllegalArgumentException("Data de aniversário não pode estar no futuro e nem ser nula");
        }

        user.setDateBirth(Date.from(dto.getDateBirth().toInstant()));
        this.userRepository.save(user);

        return new UserEditProfileDTO(user.getName(), user.getEmail(), user.getPhoneNumber(),
                user.getDateBirth(), user.getCpf());
    }

    public UserProfileResponse updateRoleOfUser(UpdateRoleUserDTO dto) {
        User user = this.userRepository.findById(dto.getUserId())
                .orElseThrow(
                        () -> new UserNotFoundException("Usuário não encontrado pelo id", dto.getUserId().toString()));

        Set<Role> currentUserRoles = user.getRoles();

        if (dto.getRoleNameAdd() != null && !dto.getRoleNameAdd().isEmpty()) {

            List<Role> rolesToAdd = this.roleRepository.findByDescriptionIn(dto.getRoleNameAdd());


            if (rolesToAdd.size() != dto.getRoleNameAdd().size()) {
                throw new RoleNotFoundException("Um ou mais papéis para adicionar não foram encontrados.");
            }

            for (Role role : rolesToAdd) {
                if (currentUserRoles.contains(role)) {
                    throw new UpdateRoleUserException("Usuário já poussi o papel: " + role.getDescription());
                } else {
                    user.getRoles().add(role);
                }

            }
        }

        if (dto.getRoleNameRemove() != null && !dto.getRoleNameRemove().isEmpty()) {
            Set<String> rolesToremoveNames = dto.getRoleNameRemove().stream()
                    .map(String::toUpperCase)
                    .collect(Collectors.toSet());

            currentUserRoles.removeIf(role ->
                    rolesToremoveNames.contains(role.getDescription().toUpperCase()));
        }

        this.userRepository.save(user);

        return new UserProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getDateBirth(),
                user.getFileImageUrl(),
                user.getCpf(),
                user.getStatus(),
                null,
                null
        );
    }

    public UserRoleResponseDTO getUserRole(UUID userId) {
        log.info("Getting user role for user: " + userId);
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("user.not.found", userId.toString())
        );
        List<String> roles = user.getRoles().stream().map(Role::getDescription).collect(Collectors.toList());
        log.info(roles.toString());
        return new UserRoleResponseDTO(userId, roles);
    }
}
