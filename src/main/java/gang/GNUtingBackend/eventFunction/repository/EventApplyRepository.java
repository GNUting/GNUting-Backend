package gang.GNUtingBackend.eventFunction.repository;

import gang.GNUtingBackend.eventFunction.entity.EventApply;
import gang.GNUtingBackend.eventFunction.entity.EventServerState;
import gang.GNUtingBackend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventApplyRepository extends JpaRepository<EventApply,Long> {
    EventApply findByUserId(User user);
}
