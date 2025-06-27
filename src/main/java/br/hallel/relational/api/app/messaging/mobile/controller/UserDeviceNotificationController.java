package br.hallel.relational.api.app.messaging.mobile.controller;

import br.hallel.relational.api.app.messaging.mobile.dto.DeviceNotificationDTO;
import br.hallel.relational.api.app.messaging.mobile.dto.UserDeviceNotificationResponse;
import br.hallel.relational.api.app.messaging.mobile.service.UserDeviceNotificationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user/notification")
@RequiredArgsConstructor
@Tag(name = "User Notification", description = "User part for controller of notification in mobile")
public class UserDeviceNotificationController {

    private final UserDeviceNotificationService userDeviceNotificationService;

    @PostMapping("/save-device/{user-id}")
    public ResponseEntity<UserDeviceNotificationResponse> saveUserDeviceNotification(
            @PathVariable(name = "user-id") UUID userId, @RequestBody DeviceNotificationDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.userDeviceNotificationService.saveUserDeviceForNotification(userId, dto));
    }

    @DeleteMapping("/delete-device/{device-notification-id}")
    public ResponseEntity<?> deleteUserDeviceNotification(@PathVariable(name = "device-notification-id") UUID deviceId) {
        userDeviceNotificationService.removeUserDevice(deviceId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
