package gang.GNUtingBackend.memoThing.repository;

import gang.GNUtingBackend.memoThing.entity.Memo;
import gang.GNUtingBackend.user.domain.User;
import gang.GNUtingBackend.user.domain.enums.Gender;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface MemoRepository extends JpaRepository<Memo,Long> {


    @Query("select m from Memo m where m.status = 'open' and m.gender != :userGender ORDER BY m.createdDate DESC ")
    Page<Memo> findByMemo(Gender userGender, Pageable pageable);

    @Query("SELECT m FROM Memo m WHERE m.userId = :user AND m.status = 'OPEN'")
    Memo findByUserIdAndStatus(User user);
    @Modifying
    @Transactional
    @Query("UPDATE Memo m SET m.status = 'CLOSE' WHERE m.status = 'OPEN'")
    void updateMemoStatusToClose();
}
