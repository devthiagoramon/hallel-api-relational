package br.hallel.relational.api.app.messaging.mobile.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.UUID;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class UserDeviceNotificationId {

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "device_notification_id")
    private UUID deviceNotificationId;
}
