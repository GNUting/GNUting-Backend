package gang.GNUtingBackend.board.service;

import gang.GNUtingBackend.board.dto.BoardUserDto;
import gang.GNUtingBackend.board.entity.BoardParticipant;
import gang.GNUtingBackend.board.entity.enums.Status;
import gang.GNUtingBackend.board.repository.BoardRepository;
import gang.GNUtingBackend.board.repository.BoardUserRepository;
import gang.GNUtingBackend.board.dto.BoardDto;
import gang.GNUtingBackend.board.entity.Board;
import gang.GNUtingBackend.user.domain.User;
import gang.GNUtingBackend.user.domain.enums.Gender;
import gang.GNUtingBackend.user.repository.UserRepository;
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

    @Autowired
    private UserRepository userRepository;

    public List<BoardDto> show(String email) {
        User user= userRepository.findByEmail(email).orElseThrow(()->new IllegalArgumentException("사용자를 찾을수 없습니다 토큰오류"));
        Gender gender=user.getGender();
        List<Board> links = boardRepository.findByGenderNot(gender);

        return links.stream()
                .map(BoardDto::toDto)
                .collect(Collectors.toList());
    }
    @Transactional
    public BoardDto save(BoardDto boardDto,String email) {

        User user= userRepository.findByEmail(email).orElseThrow(()->new IllegalArgumentException("사용자를 찾을수 없습니다 토큰오류"));
        boardDto.setStatus(Status.OPEN); //생성이기 때문에 open으로 바로 설정
        boardDto.setUserId(user);
        boardDto.setGender(user.getGender());
        Board boardSave = boardDto.toEntity();
        boardRepository.save(boardSave);

        //리스트로 받아온 유저들을 게시글 테이블에 저장 모르겟다 이건
        for (User member:boardDto.getInUser()) { //int 대신 User형태로
            BoardUserDto boardUserDto=BoardUserDto.toDto(boardSave,member);
            BoardParticipant boardParticipantSave =boardUserDto.toEntity();
            boardUserRepository.save(boardParticipantSave);
        }

        return BoardDto.toDto(boardSave);


    }
    @Transactional
    public Board delete(Long id,String email) {
        User user= userRepository.findByEmail(email).orElseThrow(()->new IllegalArgumentException("사용자를 찾을수 없습니다 토큰오류"));
        Board boardDelete=boardRepository.findById(id).orElse(null);
        if (boardDelete == null){
            return null;
        }
        if (boardDelete.getId()!=user.getId()){
            return null;
        }
        // 다대다 테이블 삭제
        boardUserRepository.deleteByBoardId(boardDelete);
        //대상 삭제
        boardRepository.delete(boardDelete);
        return boardDelete;
    }


    //dto를 새로만들어서 보여줘야할듯
    public BoardDto inshow(Long id) {
        Board board=boardRepository.findById(id).orElseThrow();
        BoardDto boardDto=BoardDto.toDto(board);
        return boardDto;
    }

    public String edit(Long id, BoardDto boardDto, String email) {
        User user= userRepository.findByEmail(email).orElseThrow(()->new IllegalArgumentException("사용자를 찾을수 없습니다 토큰오류"));
        Board board=boardRepository.findById(id).orElseThrow(()->new IllegalArgumentException("게시물을 찾을수 없습니다 토큰오류"));
        if(board.getUserId().getId()==user.getId()){
            BoardDto changeBoard=BoardDto.toDto(board);
            changeBoard.setTitle(boardDto.getTitle());
            changeBoard.setDetail(boardDto.getDetail());
            changeBoard.setInUser(boardDto.getInUser());
            changeBoard.setUserId(user);
            Board changedBoard=changeBoard.toEntity();
            boardRepository.save(changedBoard);
            return boardDto.getId()+"번 게시글이 수정되었습니다";
        }
        return null;
    }
}
