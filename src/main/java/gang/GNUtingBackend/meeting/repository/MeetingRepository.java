package gang.GNUtingBackend.meeting.repository;

import gang.GNUtingBackend.meeting.entity.Meeting;
import gang.GNUtingBackend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MeetingRepository extends JpaRepository<Meeting,Long> {

    @Query("SELECT m FROM Meeting m WHERE m.userId = :user AND m.status = 'OPEN'")

    Meeting findByUserAndStatusOpen(User user);
}
