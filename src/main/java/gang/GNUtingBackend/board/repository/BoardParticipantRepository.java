package gang.GNUtingBackend.board.repository;

import gang.GNUtingBackend.board.entity.Board;
import gang.GNUtingBackend.board.entity.BoardParticipant;
import gang.GNUtingBackend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.nio.file.attribute.UserPrincipal;
import java.util.List;

public interface BoardParticipantRepository extends JpaRepository<BoardParticipant, Long> {
    void deleteByBoardId(Board boardDelete);
    List<BoardParticipant> findByBoardId(Board id);
}
