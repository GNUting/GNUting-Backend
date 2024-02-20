package gang.GNUtingBackend.board.repository;

import gang.GNUtingBackend.board.entity.Board;
import gang.GNUtingBackend.board.entity.BoardApplyUsers;
import gang.GNUtingBackend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.BitSet;
import java.util.List;

public interface BoardApplyUsersRepository extends JpaRepository<BoardApplyUsers,Long> {


    List<BoardApplyUsers> findByBoardId(Board id);

    List<BoardApplyUsers> findByLeader(User user);
}
