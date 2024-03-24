package gang.GNUtingBackend.board.service;

import gang.GNUtingBackend.board.dto.ApplicationStatusResponseDto;
import gang.GNUtingBackend.board.dto.BoardShowAllResponseDto;
import gang.GNUtingBackend.board.dto.ChatMemberDto;
import gang.GNUtingBackend.board.entity.ApplyUsers;
import gang.GNUtingBackend.board.entity.Board;
import gang.GNUtingBackend.board.entity.BoardApplyLeader;
import gang.GNUtingBackend.board.entity.BoardParticipant;
import gang.GNUtingBackend.board.entity.enums.ApplyStatus;
import gang.GNUtingBackend.board.repository.BoardApplyLeaderRepository;
import gang.GNUtingBackend.board.repository.BoardParticipantRepository;
import gang.GNUtingBackend.board.repository.BoardRepository;
import gang.GNUtingBackend.chat.service.ChatRoomService;
import gang.GNUtingBackend.exception.handler.BoardHandler;
import gang.GNUtingBackend.exception.handler.UserHandler;
import gang.GNUtingBackend.notification.service.FCMService;
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
    private final FCMService fcmService;
    private final ChatRoomService chatRoomService;


    /**
     * 내글에 신청한 현황보기
     * 1. 유저가 쓴글들을 가져오고
     * 2. 쓴글들의 참여자목록을 가져온다
     * 3. 글에 신청한 유저들을 가져온다
     * 4. 게시글에 대표로 신청한 리더를 찾아서 그 리더들을 기준으로 게시글에 신청한 유저들의 리스트를 만든다
     * 5. 참여자와 게시글에 신청한 유저들을 리스트에 합쳐서 반환한다
     * @param email
     * @return
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
                        ApplicationStatusResponseDto.toDto(boardApplyLeader.getId(), participantsUsers, applyUsers,
                                boardApplyLeader.getLeaderId().getDepartment(), participantDepartment,
                                boardApplyLeader.getStatus());
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

            List<BoardParticipant> boardParticipantList = boardParticipantRepository.findByBoardId(
                    boardApplyLeaders.getBoardId());
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
                            (boardApplyLeaders.getId(), participantsUsers, applyUsers,
                                    boardApplyLeaders.getLeaderId().getDepartment(),
                                    boardApplyLeaders.getBoardId().getUserId().getDepartment(),
                                    boardApplyLeaders.getStatus());
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

    // 현재 BoardResponserDto에
    public List<BoardShowAllResponseDto> myBoard(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        List<Board> board = boardRepository.findByUserId(user);

        return board.stream()
                .map(BoardShowAllResponseDto::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 거절하기
     *
     * @param id
     * @param email
     * @return String
     */
    public String refuse(Long id, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        BoardApplyLeader boardApplyLeader = boardApplyLeaderRepository.findById(id)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_APPLY));
        if (boardApplyLeader.getBoardId().getUserId() != user) {
            throw new UserHandler(ErrorStatus.USER_NOT_AUTHORITY);
        }
        boardApplyLeader.setStatus(ApplyStatus.거절);
        boardApplyLeaderRepository.save(boardApplyLeader);
        fcmService.sendMessageTo(boardApplyLeader.getLeaderId(),"과팅신청이 거절되었습니다",user.getDepartment()+" "+user.getNickname()+"님이 과팅을 거절했습니다.");

        return boardApplyLeader.getId() + "번 신청이 거절되었습니다.";
    }

    public String cancel(Long id, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        BoardApplyLeader boardApplyLeader = boardApplyLeaderRepository.findById(id)
                .orElseThrow();
        if (boardApplyLeader == null) {
            throw new BoardHandler(ErrorStatus.USER_NOT_APPLY);
        }
        if(boardApplyLeader.getLeaderId()!=user){
            throw new BoardHandler(ErrorStatus.USER_NOT_APPLY);
        }
        boardApplyLeaderRepository.delete(boardApplyLeader);
        fcmService.sendMessageTo(boardApplyLeader.getBoardId().getUserId(), "과팅신청자가 과팅을 취소했습니다.", user.getDepartment() + user.getNickname()+"님이 과팅을 취소했습니다.");
        return boardApplyLeader.getBoardId().getUserId().getDepartment() + "학과 신청이 취소되었습니다.";
    }

    public String accept(String email, Long id) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        BoardApplyLeader boardApplyLeader = boardApplyLeaderRepository.findById(id)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_APPLY));
        if (boardApplyLeader.getBoardId().getUserId() != user) {
            throw new BoardHandler(ErrorStatus.USER_NOT_AUTHORITY);
        }
        List<User> applyUserList = boardApplyLeader.getApplyUsers().stream()
                .map(ApplyUsers::getUserId)
                .collect(Collectors.toList());

        List<User> participantUserList = boardApplyLeader.getBoardId().getBoardParticipant().stream()
                .map(BoardParticipant::getUserId)
                .collect(Collectors.toList());
        String applyUserDepartment = boardApplyLeader.getLeaderId().getDepartment();
        String participantUserDepartment = boardApplyLeader.getBoardId().getUserId().getDepartment();
        ChatMemberDto chatMemberDto = ChatMemberDto.toDto(boardApplyLeader.getBoardId(), applyUserDepartment, participantUserDepartment, applyUserList,
                participantUserList);

        chatRoomService.createChatRoom(chatMemberDto);

        //알림 날려주고
        return null;
    }

}
