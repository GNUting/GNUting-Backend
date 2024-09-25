package gang.GNUtingBackend.board.repository;

import gang.GNUtingBackend.board.entity.Board;
import gang.GNUtingBackend.board.entity.enums.Status;
import gang.GNUtingBackend.user.domain.User;
import gang.GNUtingBackend.user.domain.enums.Gender;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Page<Board> findByGenderNot(Gender gender, Pageable pageable);
    List<Board> findByUserId(User user);

    @Query("SELECT b FROM Board b WHERE b.userId = :user ORDER BY CASE WHEN b.status = 'OPEN' THEN 1 ELSE 0 END DESC, b.createdDate DESC")
    List<Board> findByUserIdMyboard(User user);


    @Query("SELECT b FROM Board b WHERE b.userId = :user ORDER BY b.createdDate DESC")
    List<Board> findRecentBoardsByUser(User user, Pageable pageable);

}
