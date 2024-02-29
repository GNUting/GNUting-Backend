package gang.GNUtingBackend.board.service;

import gang.GNUtingBackend.board.dto.ApplicationStatusResponseDto;
import gang.GNUtingBackend.board.dto.BoardResponseDto;
import gang.GNUtingBackend.board.entity.ApplyUsers;
import gang.GNUtingBackend.board.entity.Board;
import gang.GNUtingBackend.board.entity.BoardApplyLeader;
import gang.GNUtingBackend.board.entity.BoardParticipant;
import gang.GNUtingBackend.board.entity.enums.ApplyStatus;
import gang.GNUtingBackend.board.repository.BoardApplyLeaderRepository;
import gang.GNUtingBackend.board.repository.BoardParticipantRepository;
import gang.GNUtingBackend.board.repository.BoardRepository;
import gang.GNUtingBackend.exception.handler.UserHandler;
import gang.GNUtingBackend.response.code.status.ErrorStatus;
import gang.GNUtingBackend.user.domain.User;
import gang.GNUtingBackend.user.dto.UserSearchResponseDto;
import gang.GNUtingBackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationStatusService {

    private final BoardRepository boardRepository;
    private final BoardParticipantRepository boardParticipantRepository;
    private final UserRepository userRepository;
    private final BoardService boardService;
    private final BoardApplyLeaderRepository boardApplyLeaderRepository;

    /*
  내글에 신청한 현황보기 s
  1. 유저가 쓴글들을 가져오고
  2. 쓴글들의 참여자목록을 가져온다
  3. 글에 신청한 유저들을 가져온다
  4. 게시글에 대표로 신청한 리더를 찾아서 그 리더들을 기준으로 게시글에 신청한 유저들의 리스트를 만든다
  5. 참여자와 게시글에 신청한 유저들을 리스트에 합쳐서 반환한다
   */
    public List<ApplicationStatusResponseDto> receiveState(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        List<Board> boardList = boardRepository.findByUserId(user);
        List<ApplicationStatusResponseDto> allUsersByLeader = new ArrayList<>();
        String participantDepartment = user.getDepartment();

        for (Board boards : boardList) {  //내가작성한 글에서 참여자와 신청자 가져오기
            List<BoardParticipant> boardParticipantList = boardParticipantRepository.findByBoardId(boards);
            List<BoardApplyLeader> boardApplyLeaderList = boardApplyLeaderRepository.findByBoardId(boards);
            for (BoardApplyLeader boardApplyLeader : boardApplyLeaderList) { //게시판에 신청한 리더 가져오기
                List<ApplyUsers> applyUsersList = boardApplyLeader.getApplyUsers();
                List<User> userList = new ArrayList<>();
                for (ApplyUsers applyUsers : applyUsersList) {  //리더안에 유저들 가져오기
                    userList.add(applyUsers.getUserId());
                }
                List<UserSearchResponseDto> participantsUsers = boardParticipantList.stream()
                        .map(BoardParticipant::getUserId)
                        .map(UserSearchResponseDto::toDto)
                        .collect(Collectors.toList());

                List<UserSearchResponseDto> applyUsers = userList.stream()
                        .map(UserSearchResponseDto::toDto)
                        .collect(Collectors.toList());

                ApplicationStatusResponseDto savedResponseDto =
                        ApplicationStatusResponseDto.toDto(boardApplyLeader.getId(), participantsUsers, applyUsers, boardApplyLeader.getLeaderId().getDepartment(), participantDepartment);
                allUsersByLeader.add(savedResponseDto);
            }
        }
        return allUsersByLeader;
    }

    /**
     * 신청현황
     *
     * @param email
     * @return
     */
    public List<ApplicationStatusResponseDto> applyState(String email) {

        List<ApplicationStatusResponseDto> allUsersByLeader = new ArrayList<>();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        List<BoardApplyLeader> boardApplyLeaderList = boardApplyLeaderRepository.findByLeaderId(user);

        for (BoardApplyLeader boardApplyLeaders : boardApplyLeaderList) {

            List<BoardParticipant> boardParticipantList = boardParticipantRepository.findByBoardId(boardApplyLeaders.getBoardId());
            List<ApplyUsers> applyUsersList = boardApplyLeaders.getApplyUsers();
            List<User> userList = new ArrayList<>();
            for (ApplyUsers applyUsers : applyUsersList) {
                userList.add(applyUsers.getUserId());
            }
            List<UserSearchResponseDto> participantsUsers = boardParticipantList.stream()
                    .map(BoardParticipant::getUserId)
                    .map(UserSearchResponseDto::toDto)
                    .collect(Collectors.toList());
            List<UserSearchResponseDto> applyUsers = userList.stream()
                    .map(UserSearchResponseDto::toDto)
                    .collect(Collectors.toList());
            ApplicationStatusResponseDto savedResponseDto =
                    ApplicationStatusResponseDto.toDto
                            (boardApplyLeaders.getId(), participantsUsers, applyUsers, boardApplyLeaders.getLeaderId().getDepartment(), boardApplyLeaders.getBoardId().getUserId().getDepartment());
            allUsersByLeader.add(savedResponseDto);
        }
        return allUsersByLeader;
    }




    /**
     * 내가 쓴 글
     *
     * @param email
     * @return
     */
    public List<BoardResponseDto> myBoard(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        List<Board> board = boardRepository.findByUserId(user);
        List<BoardResponseDto> boardResponseDtos = new ArrayList<>();
        for (Board boards : board) {
            BoardResponseDto boardResponseDto = boardService.inshow(boards.getId());
            boardResponseDtos.add(boardResponseDto);
        }
        return boardResponseDtos;
    }

    public String refuse(Long id, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        BoardApplyLeader boardApplyLeader=boardApplyLeaderRepository.findById(id)
                .orElseThrow(()->new UserHandler(ErrorStatus.USER_NOT_FOUND));
        if(boardApplyLeader.getBoardId().getUserId()!=user){
            throw new UserHandler(ErrorStatus.USER_NOT_AUTHORITY);
        }
        boardApplyLeader.setStatus(ApplyStatus.거절);
        boardApplyLeaderRepository.save(boardApplyLeader);

        return boardApplyLeader.getId()+"번 신청이 거절되었습니다.";
    }


    //이건 반환 클래스를 사용하지않고 리스트형으로 유저들만 반환하는 메소드
//    public List<List<UserSearchResponseDto>> applyStatus(String email) {
//        User user= userRepository.findByEmail(email).orElseThrow(()->new IllegalArgumentException("사용자를 찾을수 없습니다 토큰오류"));
//        List<Board> board=boardRepository.findByUserId(user);
//        List<List<UserSearchResponseDto>> allUsersByLeader = new ArrayList<>();
//
//        for (Board findBoard:board) {
//            List<BoardParticipant> boardParticipants=boardParticipantRepository.findByBoardId(findBoard);
//            List<BoardApplyUsers> boardApplyUsers=boardApplyUsersRepository.findByBoardId(findBoard);
//            Map<User, List<BoardApplyUsers>> groupedByLeaderId = boardApplyUsers.stream()
//                    .collect(Collectors.groupingBy(BoardApplyUsers::getLeader));
//
//            List<UserSearchResponseDto> participantsUsers=boardParticipants.stream()
//                    .map(BoardParticipant::getUserId)
//                    .map(UserSearchResponseDto::toDto)
//                    .collect(Collectors.toList());
//
//            groupedByLeaderId.forEach((leader, users) -> {
//                List<UserSearchResponseDto> userDtos = users.stream()
//                        .map(BoardApplyUsers::getUserId)
//                        .map(UserSearchResponseDto::toDto)
//                        .collect(Collectors.toList());
//                userDtos.addAll(participantsUsers);
//                allUsersByLeader.add(userDtos);
//            });
//        }
//        return allUsersByLeader;
//    }


}
