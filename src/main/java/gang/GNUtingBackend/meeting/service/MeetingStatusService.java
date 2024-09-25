//package gang.GNUtingBackend.meeting.service;
//
//
//import gang.GNUtingBackend.board.dto.ChatMemberDto;
//import gang.GNUtingBackend.board.entity.Board;
//import gang.GNUtingBackend.board.entity.BoardApplyLeader;
//import gang.GNUtingBackend.board.entity.enums.ApplyStatus;
//import gang.GNUtingBackend.chat.dto.ChatRoomResponseDto;
//import gang.GNUtingBackend.chat.service.ChatRoomService;
//import gang.GNUtingBackend.exception.handler.BoardHandler;
//import gang.GNUtingBackend.exception.handler.MeetingHandler;
//import gang.GNUtingBackend.exception.handler.UserHandler;
//import gang.GNUtingBackend.meeting.entity.Meeting;
//import gang.GNUtingBackend.meeting.entity.MeetingApplyLeader;
//import gang.GNUtingBackend.meeting.repository.MeetingApplyLeaderRepository;
//import gang.GNUtingBackend.meeting.repository.MeetingRepository;
//import gang.GNUtingBackend.notification.service.FCMService;
//import gang.GNUtingBackend.response.code.status.ErrorStatus;
//import gang.GNUtingBackend.user.domain.User;
//import gang.GNUtingBackend.user.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class MeetingStatusService {
//
//    private final UserRepository userRepository;
//    private final MeetingApplyLeaderRepository meetingApplyLeaderRepository;
//    private final FCMService fcmService;
//    private final ChatRoomService chatRoomService;
//    private final MeetingRepository meetingRepository;
//    public String accept(String email, Long id) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
//        MeetingApplyLeader meetingApplyLeader = meetingApplyLeaderRepository.findById(id)
//                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_APPLY));
//        if (meetingApplyLeader.getMeetingUserId() != user) {
//            throw new BoardHandler(ErrorStatus.USER_NOT_AUTHORITY);
//        }
//        if (meetingApplyLeader.getStatus() == ApplyStatus.승인) {
//            throw new BoardHandler(ErrorStatus.ALREADY_SUCCESS_APPLY);
//        }
//        List<User> participantUserList=new ArrayList<>();
//        List<User> applyUserList=new ArrayList<>();
//
//        participantUserList.add(meetingApplyLeader.getMeetingUserId());
//        applyUserList.add(meetingApplyLeader.getLeaderId());
//        String applyUserDepartment = meetingApplyLeader.getLeaderId().getDepartment();
//        String participantUserDepartment = meetingApplyLeader.getMeetingUserId().getDepartment();
//        ChatMemberDto chatMemberDto = ChatMemberDto.toDto("1:1", applyUserDepartment, participantUserDepartment, applyUserList,
//                participantUserList);
//
//        ChatRoomResponseDto chatRoomResponseDto=chatRoomService.createChatRoom(chatMemberDto);
//
//
//        List<User> notificationUser = new ArrayList<>();
//        notificationUser.addAll(chatMemberDto.getApplyUser());
//        notificationUser.addAll(chatMemberDto.getParticipantUser());
//
//        fcmService.sendAllMessage(notificationUser, "1:1 매칭이 성사되었습니다", chatMemberDto.getApplyUserDepartment() + "와 " + chatMemberDto.getParticipantUserDepartment() + "의 1:1 매칭이이 성사되어 채팅방이 만들어졌습니다.","chat",chatRoomResponseDto.getId());
//        meetingApplyLeader.setStatus(ApplyStatus.승인);
//        Meeting meeting = meetingRepository.findById(meetingApplyLeader.getMeeting().getId())
//                .orElseThrow(() -> new BoardHandler(ErrorStatus.BOARD_NOT_FOUND));
//        meeting.closeState();
//        meetingRepository.save(meeting);
//        meetingApplyLeaderRepository.save(meetingApplyLeader);
//
//        return "1:1 매칭이 성사되었습니다.";
//    }
//
//    public String refuse(Long id, String email) {
//
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
//        MeetingApplyLeader meetingApplyLeader = meetingApplyLeaderRepository.findById(id)
//                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_APPLY));
//        if (meetingApplyLeader.getMeetingUserId() != user) {
//            throw new UserHandler(ErrorStatus.USER_NOT_AUTHORITY);
//        }
//        meetingApplyLeader.setStatus(ApplyStatus.거절);
//        meetingApplyLeaderRepository.save(meetingApplyLeader);
//        fcmService.sendMessageTo(meetingApplyLeader.getLeaderId(), "1:1 매칭신청이 거절되었습니다", user.getDepartment() + " " + user.getNickname() + "님이 1:1 매칭을 거절했습니다.","meetingRefuse",meetingApplyLeader.getId());
//        return meetingApplyLeader.getId() + "번 신청이 거절되었습니다.";
//
//    }
//
//    public String cancel(Long id, String email) {
//
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
//        MeetingApplyLeader meetingApplyLeader = meetingApplyLeaderRepository.findById(id)
//                .orElseThrow();
//        if (meetingApplyLeader == null) {
//            throw new MeetingHandler(ErrorStatus.USER_NOT_APPLY);
//        }
//        if (meetingApplyLeader.getLeaderId() != user) {
//            throw new UserHandler(ErrorStatus.USER_NOT_APPLY);
//        }
//        meetingApplyLeaderRepository.delete(meetingApplyLeader);
//        fcmService.sendMessageTo(meetingApplyLeader.getLeaderId(), "1:1 매칭 신청자가 신청을 취소했습니다.", user.getDepartment() + user.getNickname() + "님이 매칭을 취소했습니다.","meetingCancel",id);
//        return meetingApplyLeader.getMeetingUserId().getDepartment() + "학과 신청이 취소되었습니다.";
//    }
//
//    public String applyStateHide(String email, Long id) {
//
//        return null;
//    }
//
//    public String receivedStateHide(String email, Long id) {
//
//        return null;
//    }
//}
