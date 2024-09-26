package gang.GNUtingBackend.memoThing.repository;

import gang.GNUtingBackend.memoThing.entity.MemoApplyRemaining;
import gang.GNUtingBackend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemoApplyRemainingRepository extends JpaRepository<MemoApplyRemaining,Long> {

    MemoApplyRemaining findByUserId(User user);

    void deleteByUserId(User user);
}
