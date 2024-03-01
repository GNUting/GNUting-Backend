package gang.GNUtingBackend.board.repository;

import gang.GNUtingBackend.board.dto.BoardSearchResultDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchBoardRepository {

    Page<BoardSearchResultDto> searchByTitleOrDepartment(String keyWord, String email, Pageable pageable);

}
