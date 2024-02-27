package gang.GNUtingBackend.board.service;

import gang.GNUtingBackend.board.dto.BoardApplyUsersDto;
import gang.GNUtingBackend.board.dto.BoardResponseDto;
import gang.GNUtingBackend.board.dto.BoardParticipantDto;
import gang.GNUtingBackend.board.entity.BoardApplyUsers;
import gang.GNUtingBackend.board.entity.BoardParticipant;
import gang.GNUtingBackend.board.entity.enums.ApplyStatus;
import gang.GNUtingBackend.board.entity.enums.Status;
import gang.GNUtingBackend.board.repository.BoardApplyUsersRepository;
import gang.GNUtingBackend.board.repository.BoardRepository;
import gang.GNUtingBackend.board.repository.BoardParticipantRepository;
import gang.GNUtingBackend.board.dto.BoardRequestDto;
import gang.GNUtingBackend.board.entity.Board;
import gang.GNUtingBackend.user.domain.User;
import gang.GNUtingBackend.user.domain.enums.Gender;
import gang.GNUtingBackend.user.dto.UserSearchRequestDto;
import gang.GNUtingBackend.user.dto.UserSearchResponseDto;
import gang.GNUtingBackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardParticipantRepository boardParticipantRepository;
    private final UserRepository userRepository;
    private final BoardApplyUsersRepository boardApplyUsersRepository;

       /*
    게시글 모두 보기
     사용자의 성별을 가져와서 확인후 반대되는 성별이 작성한 게시글만 조회
     */
    public List<BoardRequestDto> show(String email, Pageable pageable) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을수 없습니다 토큰오류"));
        Gender gender = user.getGender();
        int page = pageable.getPageNumber() - 1;
        int pageLimit = 20;
        Page<Board> links = boardRepository.findByGenderNot(gender,
                PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "createdDate")));

        return links.stream()
                .map(BoardRequestDto::toDto)
                .collect(Collectors.toList());
    }

       /*
    게시글 작성
    게시글의 내용 저장 및 리스트형태로 받아온 User들 저장
     */
    @Transactional
    public BoardRequestDto save(BoardRequestDto boardRequestDto, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을수 없습니다 토큰오류"));
        boardRequestDto.setStatus(Status.OPEN); //생성이기 때문에 open으로 바로 설정
        boardRequestDto.setUserId(user);
        boardRequestDto.setGender(user.getGender());
        Board boardSave = boardRequestDto.toEntity();
        boardRepository.save(boardSave);

        for (User member : boardRequestDto.getInUser()) {
            BoardParticipantDto boardParticipantDto = BoardParticipantDto.toDto(boardSave, member);
            BoardParticipant boardParticipantSave = boardParticipantDto.toEntity();
            boardParticipantRepository.save(boardParticipantSave);
        }

        return BoardRequestDto.toDto(boardSave); //굳이 리턴값을 줄필요 없을듯 ???


    }
    
     /*
    게시글 삭제
    게시글이 삭제될시 boardParticipant 테이블에 저장된 유저들 삭
     */
    @Transactional
    public Board delete(Long id, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을수 없습니다 토큰오류"));
        Board boardDelete = boardRepository.findById(id).orElse(null);
        if (boardDelete == null) {
            return null;
        }
        if (boardDelete.getId() != user.getId()) {
            return null;
        }
        // 다대다 테이블 삭제
        boardParticipantRepository.deleteByBoardId(boardDelete);
        //대상 삭제
        boardRepository.delete(boardDelete);
        return boardDelete;
    }


     /*
    글 보기
    게시글과 해당게시글의 참여자들 보기
     */
    @Transactional(readOnly = true)
    public BoardResponseDto inshow(Long id) {
        Board board = boardRepository.findById(id).orElseThrow();

        List<BoardParticipant> users = boardParticipantRepository.findByBoardId(board);
        List<User> members = new ArrayList<>();
        for (BoardParticipant user : users) {
            members.add(user.getUserId());
        }

        List<UserSearchResponseDto> userSearchResponseDtos =
                members.stream()
                        .map(UserSearchResponseDto::toDto)
                        .collect(Collectors.toList());

        BoardResponseDto boardResponseDto = BoardResponseDto.toDto(board, userSearchResponseDtos);
        return boardResponseDto;
    }

     /*
    게시글 수정
    게시글에 참여자들도 수정가능하게 구현함
     */
    @Transactional
    public String edit(Long id, BoardRequestDto boardRequestDto, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을수 없습니다 토큰오류"));
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을수 없습니다 토큰오류"));
        if (board.getUserId().getId() == user.getId()) {
            BoardRequestDto changeBoard = BoardRequestDto.toDto(board);
            changeBoard.setTitle(boardRequestDto.getTitle());
            changeBoard.setDetail(boardRequestDto.getDetail());
            changeBoard.setInUser(boardRequestDto.getInUser());
            changeBoard.setUserId(user);
            Board changedBoard = changeBoard.toEntity();
            boardRepository.save(changedBoard);

            List<BoardParticipant> users = boardParticipantRepository.findByBoardId(board);
            boardParticipantRepository.deleteAll(users);

            for (User member : changeBoard.getInUser()) {
                BoardParticipantDto boardParticipantDto = BoardParticipantDto.toDto(changedBoard, member);
                BoardParticipant boardParticipantSave = boardParticipantDto.toEntity();
                boardParticipantRepository.save(boardParticipantSave);
            }

            return board.getId() + "번 게시글이 수정되었습니다";
        }
        return "잘못된 사용자입니다";  //예외처리 필요
    }


     /*
    유저 검색
    게시글작성할 때 유저를 추가하기위한 검색
     */
    @Transactional(readOnly = true)
    public UserSearchResponseDto userSearch(String email, String nickname) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을수 없습니다 토큰오류"));
        Gender gender = user.getGender();

        User finduser = userRepository.findByUserSearch(gender, nickname); //nullpoint 에러처리 필요

        UserSearchResponseDto userSearchResponseDto = UserSearchResponseDto.toDto(finduser);
        return userSearchResponseDto;
    }

      /*
    게시글에 과팅신청
     */
    @Transactional
    public String apply(Long id, List<UserSearchRequestDto> userSearchRequestDto, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을수 없습니다 토큰오류"));
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을수 없습니다 토큰오류"));
        String usersName = "";
        String overlap = "";
        List<BoardApplyUsers> boardApplyUsers = boardApplyUsersRepository.findByBoardId(board);

        //게시글의 참여자 인원과 신청자 인원이 맞지않을경우 예외처리 필요
        if (board.getInUserCount() != userSearchRequestDto.size()) {
            return "인원을 정확하게 추가해주세요";
        }

        //게시글에 이미 신청한 유저가 있을경우 신청이 안되게 구현
        //중복검사하는 건데 이쁘게 코드수정 필요 ㅠㅠ... 예외처리 필요
        for (UserSearchRequestDto userApply : userSearchRequestDto) {
            User member = userRepository.findById(userApply.getId())
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을수 없습니다"));
            for (BoardApplyUsers boardApplyAll : boardApplyUsers) {
                if (member.getId() == boardApplyAll.getUserId().getId()) {
                    overlap = overlap + " " + member.getNickname();
                }
            }
        }
        // 만약 overlap변수에 글이 1개라도 있을시(유저가1명이라도 있을시) 이미신청한 유저
        if (overlap.length() > 1) {
            return overlap + " 유저가 이미 신청했습니다"; //이미 신청한 유저가 있을경우 예외처리 필요
        }

        // 게시글에 신청하는 유저 저장
        for (UserSearchRequestDto userApply : userSearchRequestDto) {
            User member = userRepository.findById(userApply.getId())
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을수 없습니다"));
            ;
            BoardApplyUsersDto boardApplyUsersDto = new BoardApplyUsersDto();
            boardApplyUsersDto.setStatus(ApplyStatus.대기중);
            BoardApplyUsers boardApplyUsersToEntity = boardApplyUsersDto.toEntity(board, user, member);
            boardApplyUsersRepository.save(boardApplyUsersToEntity);
            usersName = usersName + " " + userApply.getNickname();
        }
        // + 작성자에게 알림 날려줘야함 추가 알림코드 구현해야함
        return board.getId() + "게시물에 " + usersName + "유저들 신청완료";


    }

}
