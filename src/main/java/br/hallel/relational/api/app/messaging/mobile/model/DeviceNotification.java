package br.hallel.relational.api.app.messaging.mobile.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "device_notification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceNotification {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "ip_address", nullable = false)
    private String ipAddress;

    @Column(name = "operation_system", nullable = false)
    private DeviceOperationSystem operationSystem;

    @Column(name = "fcm_token", nullable = false)
    private String fcmToken;

    @Column(name = "device_id", nullable = false, unique = true)
    private UUID deviceId;

}
