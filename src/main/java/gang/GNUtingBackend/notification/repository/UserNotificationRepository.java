package gang.GNUtingBackend.notification.repository;

import gang.GNUtingBackend.notification.entity.UserNotification;
import gang.GNUtingBackend.user.domain.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserNotificationRepository extends JpaRepository<UserNotification,Long> {
    List<UserNotification> findByUserId(User user, Sort createdDate);
    @Modifying
    @Transactional
    @Query("UPDATE UserNotification n SET n.status = 0 WHERE n.userId = :user AND n.status = null")
    void markNotificationsAsRead(User user);
}
