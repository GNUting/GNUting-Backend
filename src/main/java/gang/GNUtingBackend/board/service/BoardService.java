package gang.GNUtingBackend.board.service;

import gang.GNUtingBackend.board.dto.*;
import gang.GNUtingBackend.board.entity.ApplyUsers;
import gang.GNUtingBackend.board.entity.BoardApplyLeader;
import gang.GNUtingBackend.board.entity.BoardParticipant;
import gang.GNUtingBackend.board.entity.enums.Status;
import gang.GNUtingBackend.board.repository.ApplyUsersRepository;
import gang.GNUtingBackend.board.repository.BoardApplyLeaderRepository;
import gang.GNUtingBackend.board.repository.BoardRepository;
import gang.GNUtingBackend.board.repository.BoardParticipantRepository;
import gang.GNUtingBackend.board.entity.Board;
import gang.GNUtingBackend.exception.handler.BoardHandler;
import gang.GNUtingBackend.exception.handler.UserHandler;
import gang.GNUtingBackend.response.code.status.ErrorStatus;
import gang.GNUtingBackend.user.domain.User;
import gang.GNUtingBackend.user.domain.enums.Gender;
import gang.GNUtingBackend.user.dto.UserSearchRequestDto;
import gang.GNUtingBackend.user.dto.UserSearchResponseDto;
import gang.GNUtingBackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardParticipantRepository boardParticipantRepository;
    private final UserRepository userRepository;
    private final BoardApplyLeaderRepository boardApplyLeaderRepository;
    private final ApplyUsersRepository applyUsersRepository;

    /**
     * 게시글 모두 보기
     * @param email 현재 사용자
     * @param pageable 페이지번호
     * @return 로그인한 유저의 성별과 반대되는 성별이 쓴 글들
     */
    public List<BoardShowAllResponseDto> show(String email, Pageable pageable) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        Gender gender = user.getGender();
        int page = pageable.getPageNumber() - 1;
        int pageLimit = 20;
        Page<Board> links = boardRepository.findByGenderNot(gender,
                PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "createdDate")));  //추후 close된 글들도 아래로 정렬

        return links.stream()
                .map(BoardShowAllResponseDto::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 게시글 저장
     * @param boardRequestDto 작성한 글 전체 내용
     * @param email
     * @return 게시글제목이 작성되었다는 맨트
     */
    @Transactional
    public String save(BoardRequestDto boardRequestDto, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        boardRequestDto.setStatus(Status.OPEN); //생성이기 때문에 open으로 바로 설정
        boardRequestDto.setUserId(user);
        boardRequestDto.setGender(user.getGender());
        Board boardSave = boardRequestDto.toEntity();
        boardRepository.save(boardSave);

        //참여자 테이블에 저장
        for (User member : boardRequestDto.getInUser()) {
            BoardParticipantDto boardParticipantDto = BoardParticipantDto.toDto(boardSave, member);
            BoardParticipant boardParticipantSave = boardParticipantDto.toEntity();
            boardParticipantRepository.save(boardParticipantSave);
        }
       // return BoardRequestDto.toDto(boardSave);
        return boardSave.getTitle()+"게시글이 작성되었습니다."; //굳이 리턴값을 줄필요 없을듯 ???
    }

    /**
     * 게시글 삭제
     * @param id 게시글 번호
     * @param email
     * @return 게시글이 삭제되었다는 맨트
     */
    @Transactional
    public String delete(Long id, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        Board boardDelete = boardRepository.findById(id).orElseThrow(() -> new BoardHandler(ErrorStatus.BOARD_NOT_FOUND));;

        if (boardDelete.getUserId().getId() != user.getId()) {
            throw new BoardHandler(ErrorStatus.USER_NOT_FOUND_IN_BOARD);
        }
        // 다대다 테이블 삭제 (추후 어노테이션으로 변경)
        boardParticipantRepository.deleteByBoardId(boardDelete);
        boardApplyLeaderRepository.deleteByBoardId(boardDelete);
        //대상 삭제
        boardRepository.delete(boardDelete);
        return boardDelete.getId().toString();
    }

    /*
   글 보기
   게시글과 해당게시글의 참여자들 보기
    */

    /**
     * 특정글 보기 (참여맴버 API랑 나눠야하는지 ?)
     * @param id 게시글 번호
     * @return
     */
    @Transactional(readOnly = true)
    public BoardResponseDto inshow(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardHandler(ErrorStatus.BOARD_NOT_FOUND));
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

    /**
     * 게시글 수정
     * @param id 게시글 번호
     * @param boardRequestDto 수정한 내용
     * @param email
     * @return 수정되었다는 맨트
     */
    @Transactional
    public String edit(Long id, BoardRequestDto boardRequestDto, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardHandler(ErrorStatus.BOARD_NOT_FOUND));
        if (board.getUserId().getId() == user.getId()) {
            BoardRequestDto changeBoard = BoardRequestDto.toDto(board);
            changeBoard.setTitle(boardRequestDto.getTitle());
            changeBoard.setDetail(boardRequestDto.getDetail());
            changeBoard.setInUser(boardRequestDto.getInUser());
            changeBoard.setUserId(user);
            Board changedBoard = changeBoard.toEntity();
            changedBoard.setCreatedDate(board.getCreatedDate()); //기존 게시글생산시간 유지
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
        else{
            throw new BoardHandler(ErrorStatus.USER_NOT_FOUND_IN_BOARD);
        }
    }

    /**
     * 유저검색
     * @param email
     * @param nickname 닉네임으로 검색
     * @return 검색한 유저 반환
     */
    @Transactional(readOnly = true)
    public UserSearchResponseDto userSearch(String email, String nickname) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        Gender gender = user.getGender();
        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByUserSearch(gender, nickname));
        User finduser = optionalUser.orElseThrow(() -> new UserHandler(ErrorStatus.USER_GENDER_NOT_MATCH));
//      User finduser = userRepository.findByUserSearch(gender, nickname);
        UserSearchResponseDto userSearchResponseDto = UserSearchResponseDto.toDto(finduser); //한줄소개 ResponseDto에 추가
        return userSearchResponseDto;
    }

    /**
     * 게시글에 과팅신청
     * @param id
     * @param userSearchRequestDto
     * @param email
     * @return
     */
    @Transactional
    public String apply(Long id, List<UserSearchRequestDto> userSearchRequestDto, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardHandler(ErrorStatus.BOARD_NOT_FOUND));
        String nickname = "";
       // String overlap = "";
        List<BoardApplyLeader> boardApplyUsers = boardApplyLeaderRepository.findByBoardId(board);

        //게시글의 참여자 인원과 신청자 인원이 맞지않을경우 예외처리 필요
        if (board.getInUserCount() != userSearchRequestDto.size()) {
            throw new BoardHandler(ErrorStatus.INCORRECT_NUMBER_OF_PEOPLE);
        }

        //게시글에 이미 신청한 유저가 있을경우 신청이 안되게 구현
        //중복검사하는 건데 이쁘게 코드수정 필요 ㅠㅠ...
//        for (UserSearchRequestDto userApply : userSearchRequestDto) {
//            User member = userRepository.findById(userApply.getId())
//                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을수 없습니다"));
//            for (BoardApplyLeader boardApplyAll : boardApplyUsers) {
//                for (ApplyUsers applyUsers:boardApplyAll.getApplyUsers()) {
//                    if(applyUsers.getUserId()==member){
//                        throw new BoardHandler(ErrorStatus.ALREADY_IN_USER);
//                    }
//                }
//            }
//        }
        for (UserSearchRequestDto userApply : userSearchRequestDto) {
            User member = userRepository.findById(userApply.getId())
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을수 없습니다"));
            // 사용자가 이미 해당 게시판에 신청했는지 확인
            boolean isUserAlreadyApplied = boardApplyUsers.stream()
                    .flatMap(boardApplyLeader -> boardApplyLeader.getApplyUsers().stream())
                    .anyMatch(applyUsers -> applyUsers.getUserId().equals(member));

            if (isUserAlreadyApplied) {
                throw new BoardHandler(ErrorStatus.ALREADY_IN_USER);
            }
        }

        // 만약 overlap변수에 글이 1개라도 있을시(유저가1명이라도 있을시) 이미신청한 유저
        //이미신청한 유저이름들을 같이 넘겨주게.. 고쳐주세요
//        if (overlap.length() > 1) {
//            //String message=overlap+"유저가 이미 신청했습니다";
//            throw new BoardHandler(ErrorStatus.ALREADY_IN_USER);
//        }

        BoardApplyLeaderDto boardApplyLeaderDto=new BoardApplyLeaderDto();
        boardApplyLeaderDto.setBoardId(board);
        boardApplyLeaderDto.setLeaderId(user);
        BoardApplyLeader savedBoardApplyLeader=boardApplyLeaderRepository.save(boardApplyLeaderDto.toEntity());

        // 게시글에 신청하는 유저 저장
        for (UserSearchRequestDto userApply : userSearchRequestDto) {
            User member = userRepository.findById(userApply.getId())
                    .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
            ApplyUsersDto applyUsers=new ApplyUsersDto();
            applyUsers.setBoardApplyLeaderId(savedBoardApplyLeader);
            applyUsers.setUserId(member);
            applyUsersRepository.save(applyUsers.toEntity());
            nickname = nickname + " " + member.getNickname();
        }
//        BoardApplyLeaderDto boardApplyLeaderDto=new BoardApplyLeaderDto();
//        boardApplyLeaderRepository.save(boardApplyLeaderDto.toEntity(board,user));

        // + 작성자에게 알림 날려줘야함 추가 알림코드 구현해야함
        return board.getId() + "게시물에 " + nickname + "유저들 신청완료";
    }

}
