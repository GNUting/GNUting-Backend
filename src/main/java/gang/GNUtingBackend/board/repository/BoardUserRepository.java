package gang.GNUtingBackend.board.repository;

import gang.GNUtingBackend.board.entity.Board;
import gang.GNUtingBackend.board.entity.BoardParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardUserRepository extends JpaRepository<BoardParticipant,Long> {
    void deleteByBoardId(Board boardDelete);
}
