package br.hallel.relational.api.app.messaging.mobile.dto;

import br.hallel.relational.api.app.messaging.mobile.model.DeviceOperationSystem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceNotificationDTO {
    private String ipAddress;
    private String fcmToken;
    private DeviceOperationSystem operationSystem;
    private UUID deviceId;
}
