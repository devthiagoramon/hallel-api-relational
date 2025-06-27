package br.hallel.relational.api.app.messaging.mobile.repository;

import br.hallel.relational.api.app.messaging.mobile.model.UserDeviceNotification;
import br.hallel.relational.api.app.messaging.mobile.model.UserDeviceNotificationId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDeviceNotificationRepository extends CrudRepository<UserDeviceNotification, UserDeviceNotificationId> {
}
