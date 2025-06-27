package br.hallel.relational.api.app.messaging.mobile.service;

import br.hallel.relational.api.app.messaging.mobile.dto.DeviceNotificationDTO;
import br.hallel.relational.api.app.messaging.mobile.dto.UserDeviceNotificationResponse;
import br.hallel.relational.api.app.messaging.mobile.exception.DeviceAlreadySavedException;
import br.hallel.relational.api.app.messaging.mobile.exception.DeviceNotificationNotFoundException;
import br.hallel.relational.api.app.messaging.mobile.model.DeviceNotification;
import br.hallel.relational.api.app.messaging.mobile.model.UserDeviceNotification;
import br.hallel.relational.api.app.messaging.mobile.model.UserDeviceNotificationId;
import br.hallel.relational.api.app.messaging.mobile.repository.DeviceNotificationRepository;
import br.hallel.relational.api.app.messaging.mobile.repository.UserDeviceNotificationRepository;
import br.hallel.relational.api.app.user.exceptions.UserNotFoundException;
import br.hallel.relational.api.app.user.model.User;
import br.hallel.relational.api.app.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDeviceNotificationService {

    private final UserDeviceNotificationRepository userDeviceNotificationRepository;
    private final DeviceNotificationRepository deviceNotificationRepository;
    private final UserRepository userRepository;

    public UserDeviceNotificationResponse saveUserDeviceForNotification(UUID userId,
                                                                        DeviceNotificationDTO deviceNotificationDTO) {
        log.info("Saving user {} device for push notification", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found by id %s".formatted(userId)));
        if (user.getPushNotification() == false) user.setPushNotification(true);
        if (user.getLgpdConsent() == false) user.setLgpdConsent(true);
        if (user.getLgpdConsentDate() == null) user.setLgpdConsentDate(new Date());

        Optional<DeviceNotification> optionalDevice = deviceNotificationRepository.findByDeviceId(
                deviceNotificationDTO.getDeviceId());
        if (optionalDevice.isPresent()) {
            throw new DeviceAlreadySavedException("Device already exists for push notification");
        }
        DeviceNotification deviceNotification = new DeviceNotification();
        deviceNotification.setFcmToken(deviceNotificationDTO.getFcmToken());
        deviceNotification.setIpAddress(deviceNotificationDTO.getIpAddress());
        deviceNotification.setOperationSystem(deviceNotificationDTO.getOperationSystem());

        try {
            DeviceNotification savedDevice = deviceNotificationRepository.save(deviceNotification);


            userDeviceNotificationRepository.save(
                    new UserDeviceNotification(new UserDeviceNotificationId(user.getId(), savedDevice.getId())));

            return new UserDeviceNotificationResponse(userId, savedDevice.getId(), deviceNotification.getFcmToken(),
                    user.getEmail());
        } catch (DataIntegrityViolationException e) {
            if (e.getCause().getCause().getClass().equals(SQLIntegrityConstraintViolationException.class)) {
                throw new DeviceAlreadySavedException("Device already exists for push notification");
            }
        }
        throw new DeviceAlreadySavedException("Device already exists for push notification");
    }

    public void removeUserDevice(UUID deviceNotificationId) {
        log.info("Removing user device {}", deviceNotificationId);
        DeviceNotification deviceNotification = deviceNotificationRepository.findById(deviceNotificationId).orElseThrow(
                () -> new DeviceNotificationNotFoundException("Device %s not found".formatted(deviceNotificationId)));
        deviceNotificationRepository.delete(deviceNotification);
    }


}
