package gang.GNUtingBackend.service;

import gang.GNUtingBackend.dto.BoardDto;
import gang.GNUtingBackend.dto.BoardUserDto;
import gang.GNUtingBackend.entity.Board;
import gang.GNUtingBackend.entity.BoardUser;
import gang.GNUtingBackend.repository.BoardRepository;
import gang.GNUtingBackend.repository.BoardUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private BoardUserRepository boardUserRepository;
    public List<BoardDto> show() {
        List<Board> links=boardRepository.findAll();
        return links.stream()
                .map(BoardDto::toDto)
                .collect(Collectors.toList());
    }

    public BoardDto save(BoardDto boardDto) {
        Board boardSave = boardDto.toEntity();
        boardRepository.save(boardSave);

        for (Integer user:boardDto.getInUser()) { //int 대신 User형태로
            BoardUserDto boardUserDto=BoardUserDto.toDto(boardSave,user);
            BoardUser boardUserSave=boardUserDto.toEntity();
            boardUserRepository.save(boardUserSave);
        }

        return BoardDto.toDto(boardSave);


    }
    @Transactional
    public Board delete(Long id) {
        Board boardDelete=boardRepository.findById(id).orElse(null);
        if (boardDelete == null){
            return null;
        }
        // 다대다 테이블 삭제
        boardUserRepository.deleteByBoardId(boardDelete);
        //대상 삭제
        boardRepository.delete(boardDelete);
        return boardDelete;
    }
}
