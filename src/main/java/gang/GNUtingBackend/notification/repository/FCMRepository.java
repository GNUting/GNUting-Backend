package gang.GNUtingBackend.notification.repository;

import gang.GNUtingBackend.notification.entity.FCM;
import gang.GNUtingBackend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FCMRepository extends JpaRepository<FCM,Long> {
    FCM findByUserId(User findId);
}
