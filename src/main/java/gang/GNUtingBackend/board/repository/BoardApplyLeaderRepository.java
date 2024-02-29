package gang.GNUtingBackend.board.repository;

import gang.GNUtingBackend.board.entity.Board;
import gang.GNUtingBackend.board.entity.BoardApplyLeader;
import gang.GNUtingBackend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardApplyLeaderRepository extends JpaRepository<BoardApplyLeader, Long> {

    List<BoardApplyLeader> findByBoardId(Board id);
    void deleteByBoardId(Board boardDelete);

    List<BoardApplyLeader> findByLeaderId(User user);
}
