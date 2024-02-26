package gang.GNUtingBackend.board.service;

import gang.GNUtingBackend.board.dto.ApplicationStatusResponseDto;
import gang.GNUtingBackend.board.dto.BoardResponseDto;
import gang.GNUtingBackend.board.entity.Board;
import gang.GNUtingBackend.board.entity.BoardApplyUsers;
import gang.GNUtingBackend.board.entity.BoardParticipant;
import gang.GNUtingBackend.board.repository.BoardApplyUsersRepository;
import gang.GNUtingBackend.board.repository.BoardParticipantRepository;
import gang.GNUtingBackend.board.repository.BoardRepository;
import gang.GNUtingBackend.user.domain.User;
import gang.GNUtingBackend.user.dto.UserSearchResponseDto;
import gang.GNUtingBackend.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ApplicationStatusService {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private BoardParticipantRepository boardParticipantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardService boardService;

    @Autowired
    private BoardApplyUsersRepository boardApplyUsersRepository;


    public List<ApplicationStatusResponseDto> received(String email) {
        User user= userRepository.findByEmail(email).orElseThrow(()->new IllegalArgumentException("사용자를 찾을수 없습니다 토큰오류"));
        List<Board> board=boardRepository.findByUserId(user);
        List<ApplicationStatusResponseDto> allUsersByLeader = new ArrayList<>();
        String participantDepartment=user.getDepartment();

        for (Board findBoard:board) {
            List<BoardParticipant> boardParticipants=boardParticipantRepository.findByBoardId(findBoard);
            List<BoardApplyUsers> boardApplyUsers=boardApplyUsersRepository.findByBoardId(findBoard);
            Map<User, List<BoardApplyUsers>> groupedByLeaderId = boardApplyUsers.stream()
                    .collect(Collectors.groupingBy(BoardApplyUsers::getLeader));

             List<UserSearchResponseDto> participantsUsers=boardParticipants.stream()
                     .map(BoardParticipant::getUserId)
                     .map(UserSearchResponseDto::toDto)
                     .collect(Collectors.toList());

            groupedByLeaderId.forEach((leader, users) -> {
                String applyDepartment=leader.getDepartment();
                List<UserSearchResponseDto> receivedUsers = users.stream()
                        .map(BoardApplyUsers::getUserId)
                        .map(UserSearchResponseDto::toDto)
                        .collect(Collectors.toList());
//                ApplicationStatusResponseDto applicationStatusResponseDto=new ApplicationStatusResponseDto();
//                applicationStatusResponseDto.setApplyUser(receivedUsers);
//                applicationStatusResponseDto.setApplyUserDepartment(applyDepartment);
//                applicationStatusResponseDto.setParticipantUser(participantsUsers);
//                applicationStatusResponseDto.setParticipantUserDepartment(participantDepartment);
                allUsersByLeader.add(ApplicationStatusResponseDto.toDto(participantsUsers,receivedUsers,applyDepartment,participantDepartment));
            });
        }
        return allUsersByLeader;
    }
    public List<ApplicationStatusResponseDto> apply(String email) {

        List<ApplicationStatusResponseDto> allUsersByBoard = new ArrayList<>();

        User user= userRepository.findByEmail(email).orElseThrow(()->new IllegalArgumentException("사용자를 찾을수 없습니다 토큰오류"));
        List<BoardApplyUsers> boardApplyUsers=boardApplyUsersRepository.findByLeader(user);

        Map<Board, List<BoardApplyUsers>> groupedByBoardId = boardApplyUsers.stream()
                .collect(Collectors.groupingBy(BoardApplyUsers::getBoardId));

        groupedByBoardId.forEach((board, users) -> {
            String participantDepartment=board.getUserId().getDepartment();

            List<BoardParticipant> participantUser=boardParticipantRepository.findByBoardId(board);
            List<UserSearchResponseDto> participantsUsers=participantUser.stream()
                    .map(BoardParticipant::getUserId)
                    .map(UserSearchResponseDto::toDto)
                    .collect(Collectors.toList());

            List<UserSearchResponseDto> applyUsers = users.stream()
                    .map(BoardApplyUsers::getUserId)
                    .map(UserSearchResponseDto::toDto)
                    .collect(Collectors.toList());
            allUsersByBoard.add(ApplicationStatusResponseDto.toDto(participantsUsers,applyUsers,user.getDepartment(),participantDepartment));
        });

        return allUsersByBoard;
    }


    public List<BoardResponseDto> myBoard(String email) {
        User user= userRepository.findByEmail(email).orElseThrow(()->new IllegalArgumentException("사용자를 찾을수 없습니다 토큰오류"));
        List<Board> board=boardRepository.findByUserId(user);
        List<BoardResponseDto> boardResponseDtos=new ArrayList<>();
        for (Board boards:board) {
            BoardResponseDto boardResponseDto=boardService.inshow(boards.getId());
            boardResponseDtos.add(boardResponseDto);
        }
        return boardResponseDtos;
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