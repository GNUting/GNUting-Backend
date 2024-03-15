package gang.GNUtingBackend.notification.repository;

import gang.GNUtingBackend.notification.entity.UserNotification;
import gang.GNUtingBackend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserNotificationRepository extends JpaRepository<UserNotification,Long> {
    List<UserNotification> findByUserId(User user);
}
