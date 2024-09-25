package gang.GNUtingBackend.eventFunction.repository;

import gang.GNUtingBackend.eventFunction.entity.EventServerState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventServerStateRepository extends JpaRepository<EventServerState,Long > {
    EventServerState findById(long l);
}
