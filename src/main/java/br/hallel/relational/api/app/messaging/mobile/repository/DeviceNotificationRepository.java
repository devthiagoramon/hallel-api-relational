package br.hallel.relational.api.app.messaging.mobile.repository;

import br.hallel.relational.api.app.messaging.mobile.model.DeviceNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeviceNotificationRepository extends JpaRepository<DeviceNotification, UUID> {
    List<DeviceNotification> findByIpAddress(String ipAddress);

    Optional<DeviceNotification> findByDeviceId(UUID deviceId);
}
