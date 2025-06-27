package br.hallel.relational.api.app.messaging.mobile.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "user_device_notification")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDeviceNotification {

    @EmbeddedId
    private UserDeviceNotificationId userDeviceNotificationId;

}
