package gang.GNUtingBackend.repository;

import gang.GNUtingBackend.entity.Board;
import gang.GNUtingBackend.entity.BoardUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardUserRepository extends JpaRepository<BoardUser,Long> {
    void deleteByBoardId(Board boardDelete);
}
