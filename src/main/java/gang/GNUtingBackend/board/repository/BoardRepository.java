package gang.GNUtingBackend.board.repository;

import gang.GNUtingBackend.board.entity.Board;
import gang.GNUtingBackend.user.domain.enums.Gender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board,Long> {
  List<Board> findByGenderNot(Gender male);

}
