package gang.GNUtingBackend.notification.repository;

import gang.GNUtingBackend.notification.entity.FCM;
import gang.GNUtingBackend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public interface FCMRepository extends JpaRepository<FCM,Long> {
    List<FCM> findByUserId(User findId);

    void deleteByFcmToken(String fcmToken);


   // List<FCM> findByCreatedDateBefore(LocalDateTime date);

    List<FCM> findByFcmToken(String fcmToken);
}
