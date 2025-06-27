package br.hallel.relational.api.app.messaging.mobile.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDeviceNotificationResponse {
    private UUID id;
    private UUID deviceNotificationId;
    private String fcmToken;
    private String email;
}
