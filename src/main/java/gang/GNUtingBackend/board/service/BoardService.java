package gang.GNUtingBackend.board.service;

import gang.GNUtingBackend.board.dto.BoardResponseDto;
import gang.GNUtingBackend.board.dto.BoardUserDto;
import gang.GNUtingBackend.board.entity.BoardParticipant;
import gang.GNUtingBackend.board.entity.enums.Status;
import gang.GNUtingBackend.board.repository.BoardRepository;
import gang.GNUtingBackend.board.repository.BoardParticipantRepository;
import gang.GNUtingBackend.board.dto.BoardRequestDto;
import gang.GNUtingBackend.board.entity.Board;
import gang.GNUtingBackend.user.domain.User;
import gang.GNUtingBackend.user.domain.enums.Gender;
import gang.GNUtingBackend.user.dto.UserSearchResponseDto;
import gang.GNUtingBackend.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private BoardParticipantRepository boardParticipantRepository;

    @Autowired
    private UserRepository userRepository;

    public List<BoardRequestDto> show(String email) {
        User user= userRepository.findByEmail(email).orElseThrow(()->new IllegalArgumentException("사용자를 찾을수 없습니다 토큰오류"));
        Gender gender=user.getGender();
        List<Board> links = boardRepository.findByGenderNot(gender);

        return links.stream()
                .map(BoardRequestDto::toDto)
                .collect(Collectors.toList());
    }
    @Transactional
    public BoardRequestDto save(BoardRequestDto boardRequestDto, String email) {

        User user= userRepository.findByEmail(email).orElseThrow(()->new IllegalArgumentException("사용자를 찾을수 없습니다 토큰오류"));
        boardRequestDto.setStatus(Status.OPEN); //생성이기 때문에 open으로 바로 설정
        boardRequestDto.setUserId(user);
        boardRequestDto.setGender(user.getGender());
        Board boardSave = boardRequestDto.toEntity();
        boardRepository.save(boardSave);


        //리스트로 받아온 유저들을 게시글 테이블에 저장 모르겟다 이건
        for (User member: boardRequestDto.getInUser()) {
            BoardUserDto boardUserDto=BoardUserDto.toDto(boardSave,member);
            BoardParticipant boardParticipantSave =boardUserDto.toEntity();
            boardParticipantRepository.save(boardParticipantSave);
        }

        return BoardRequestDto.toDto(boardSave); //굳이 리턴값을 줄필요 없을듯 ???


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
        boardParticipantRepository.deleteByBoardId(boardDelete);
        //대상 삭제
        boardRepository.delete(boardDelete);
        return boardDelete;
    }


    //dto를 새로만들어서 보여줘야할듯
    public BoardResponseDto inshow(Long id) {
        Board board=boardRepository.findById(id).orElseThrow();

        List<BoardParticipant> users= boardParticipantRepository.findByBoardId(board);
        List<User> members = new ArrayList<>();
        for (BoardParticipant user:users) {
          members.add(user.getUserId());
        }

        List<UserSearchResponseDto> userSearchResponseDtos=
                members.stream()
                .map(UserSearchResponseDto::toDto)
                .collect(Collectors.toList());

        BoardResponseDto boardResponseDto = BoardResponseDto.toDto(board,userSearchResponseDtos);
        return boardResponseDto;
    }

    public String edit(Long id, BoardRequestDto boardRequestDto, String email) {
        User user= userRepository.findByEmail(email).orElseThrow(()->new IllegalArgumentException("사용자를 찾을수 없습니다 토큰오류"));
        Board board=boardRepository.findById(id).orElseThrow(()->new IllegalArgumentException("게시물을 찾을수 없습니다 토큰오류"));
        if(board.getUserId().getId()==user.getId()){
            BoardRequestDto changeBoard= BoardRequestDto.toDto(board);
            changeBoard.setTitle(boardRequestDto.getTitle());
            changeBoard.setDetail(boardRequestDto.getDetail());
            changeBoard.setInUser(boardRequestDto.getInUser());
            changeBoard.setUserId(user);
            Board changedBoard=changeBoard.toEntity();
            boardRepository.save(changedBoard);

            List<BoardParticipant> users= boardParticipantRepository.findByBoardId(board);
            boardParticipantRepository.deleteAll(users);

            for (User member: changeBoard.getInUser()) {
                BoardUserDto boardUserDto=BoardUserDto.toDto(changedBoard,member);
                BoardParticipant boardParticipantSave =boardUserDto.toEntity();
                boardParticipantRepository.save(boardParticipantSave);
            }

            return board.getId()+"번 게시글이 수정되었습니다";
        }
        return "잘못된 사용자입니다";  //예외처리 필요
    }


    //똑같은 성별의 닉네임만 찾을 수 있도록
    public UserSearchResponseDto userSearch(String email,String nickname) {
        User user= userRepository.findByEmail(email).orElseThrow(()->new IllegalArgumentException("사용자를 찾을수 없습니다 토큰오류"));
        Gender gender=user.getGender();

        User finduser = userRepository.findByUserSearch(gender, nickname); //nullpoint 에러처리 필요

        UserSearchResponseDto userSearchResponseDto =UserSearchResponseDto.toDto(finduser);
        return userSearchResponseDto;
    }
}
