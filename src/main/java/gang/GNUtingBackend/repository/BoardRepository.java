package gang.GNUtingBackend.repository;

import gang.GNUtingBackend.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board,Long> {
}
