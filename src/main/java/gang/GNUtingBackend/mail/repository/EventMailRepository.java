package gang.GNUtingBackend.mail.repository;

import gang.GNUtingBackend.mail.entity.EventMailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventMailRepository extends JpaRepository<EventMailEntity,Long> {
}
