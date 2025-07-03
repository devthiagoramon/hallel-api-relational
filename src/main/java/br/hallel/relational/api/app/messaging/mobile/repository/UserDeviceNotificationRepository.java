package br.hallel.relational.api.app.messaging.mobile.repository;

import br.hallel.relational.api.app.messaging.mobile.model.UserDeviceNotification;
import br.hallel.relational.api.app.messaging.mobile.model.UserDeviceNotificationId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserDeviceNotificationRepository extends CrudRepository<UserDeviceNotification, UserDeviceNotificationId> {
    List<UserDeviceNotification> findUserDeviceNotificationByUserDeviceNotificationId_(UserDeviceNotificationId userDeviceNotificationId);

    List<UserDeviceNotification> findUserDeviceNotificationByUserDeviceNotificationId(UserDeviceNotificationId userDeviceNotificationId);

    List<UserDeviceNotification> findUserDeviceNotificationByUserDeviceNotificationId_DeviceNotificationId(UUID deviceNotificationId);
}
