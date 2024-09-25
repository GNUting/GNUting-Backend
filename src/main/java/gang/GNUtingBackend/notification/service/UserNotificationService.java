package gang.GNUtingBackend.notification.service;

import gang.GNUtingBackend.board.dto.ApplicationStatusResponseDto;
import gang.GNUtingBackend.board.dto.BoardApplyLeaderDto;
import gang.GNUtingBackend.board.entity.ApplyUsers;
import gang.GNUtingBackend.board.entity.BoardApplyLeader;
import gang.GNUtingBackend.board.entity.BoardParticipant;
import gang.GNUtingBackend.board.entity.enums.ApplyShowStatus;
import gang.GNUtingBackend.board.repository.BoardApplyLeaderRepository;
import gang.GNUtingBackend.chat.domain.ChatRoom;
import gang.GNUtingBackend.chat.domain.ChatRoomUser;
import gang.GNUtingBackend.chat.dto.ChatNotificationResponseDto;
import gang.GNUtingBackend.chat.repository.ChatRoomRepository;
import gang.GNUtingBackend.chat.repository.ChatRoomUserRepository;
import gang.GNUtingBackend.exception.handler.BoardHandler;
import gang.GNUtingBackend.exception.handler.ChatRoomHandler;
import gang.GNUtingBackend.exception.handler.ChatRoomUserHandler;
import gang.GNUtingBackend.exception.handler.UserHandler;
import gang.GNUtingBackend.notification.dto.NotificationChatSettingDto;
import gang.GNUtingBackend.notification.dto.NotificationSettingDto;
import gang.GNUtingBackend.notification.dto.UserNotificationResponseDto;
import gang.GNUtingBackend.notification.entity.UserNotification;
import gang.GNUtingBackend.notification.entity.enums.NotificationSetting;
import gang.GNUtingBackend.notification.entity.enums.NotificationStatus;
import gang.GNUtingBackend.notification.repository.UserNotificationRepository;
import gang.GNUtingBackend.response.code.status.ErrorStatus;
import gang.GNUtingBackend.user.domain.User;
import gang.GNUtingBackend.user.dto.UserSearchResponseDto;
import gang.GNUtingBackend.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserNotificationService {

    private final UserNotificationRepository userNotificationRepository;
    private final UserRepository userRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final BoardApplyLeaderRepository boardApplyLeaderRepository;
    private final ChatRoomRepository chatRoomRepository;

    public void saveNotification(User user, String title,String body,String location,Long locationId) {
        UserNotification userNotification = UserNotification.builder()
                .userId(user)
                .title(title)
                .body(body)
                .location(location)
                .locationId(locationId)
                .build();
        userNotificationRepository.save(userNotification);
    }

    public List<UserNotificationResponseDto> showNotification(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        List<UserNotification> userNotifications = userNotificationRepository.findByUserId(user, Sort.by(Sort.Order.desc("createdDate")));
//        for (UserNotification userNotification:userNotifications) {
//            if(userNotification.getStatus()==null) {
                userNotificationRepository.markNotificationsAsRead(user);
//                userNotification.setStatus(NotificationStatus.READ);
//                userNotificationRepository.save(userNotification);
//            }
//        }

        return userNotifications.stream().map(UserNotificationResponseDto::toDto).collect(Collectors.toList());
    }

    @Transactional
    public String deleteNotification(String email, Long id) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        UserNotification userNotification=userNotificationRepository.findById(id)
                .orElseThrow(()-> new BoardHandler(ErrorStatus.NOT_FOUND_NOTIFICATION));
        if(user!=userNotification.getUserId()){
            throw new BoardHandler(ErrorStatus.INVALID_ACCESS);
        }
        userNotificationRepository.deleteById(id);

        return id+"알림이 삭제되었습니다";
    }

    public boolean checkNotification(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        List<UserNotification> userNotifications = userNotificationRepository.findByUserId(user, Sort.by(Sort.Order.desc("createdDate")));
        boolean hasNewNotification = userNotifications.stream()
                .anyMatch(notification -> notification.getStatus() == null);
        if (hasNewNotification) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * 사용자 전체 알림 끄기 / 켜기
     * @param email
     * @param notificationSetting
     * @return
     */
    @Transactional
    public boolean updateNotificationSetting(String email, NotificationSetting notificationSetting) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        user.updateNotificationSetting(notificationSetting);
        userRepository.save(user);

        return true;
    }

    public NotificationSettingDto myAllNotificationSetting(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        NotificationSettingDto notificationSettingDto = new NotificationSettingDto(user.getNotificationSetting());

        return notificationSettingDto;
    }

    public NotificationSettingDto checkChatRoomNotificationSetting(Long chatRoomId, String email) {
        ChatRoomUser chatRoomUser = chatRoomUserRepository.findByChatRoomIdAndUserEmail(chatRoomId, email)
                .orElseThrow(() -> new ChatRoomUserHandler(ErrorStatus.NOT_FOUND_CHAT_ROOM_USER));

        NotificationSettingDto notificationSettingDto = new NotificationSettingDto(
                chatRoomUser.getNotificationSetting());

        return notificationSettingDto;
    }

    public ApplicationStatusResponseDto notificationApplicationClickAction(String email, Long applicationId) {
        User user=userRepository.findByEmail(email)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        BoardApplyLeader boardApplyLeader=boardApplyLeaderRepository.findById(applicationId)
                .orElseThrow(() -> new BoardHandler(ErrorStatus.NOT_FOUND_BOARDAPPLYUSER));


        if(boardApplyLeader.getLeaderId()==user&&boardApplyLeader.getApplyShowStatus()==ApplyShowStatus.HIDE){
           throw new BoardHandler(ErrorStatus.HIDE_APPLY);
        }
        if(boardApplyLeader.getBoardId().getUserId()==user&&boardApplyLeader.getReceiveShowStatus()==ApplyShowStatus.HIDE){
            throw new BoardHandler(ErrorStatus.HIDE_APPLY);
        }

        if(boardApplyLeader.getLeaderId()==user||boardApplyLeader.getBoardId().getUserId()==user) {

            return ApplicationStatusResponseDto.toDto(boardApplyLeader.getId(),
                    boardApplyLeader.getBoardId().getBoardParticipant().stream()
                            .map(BoardParticipant::getUserId)
                            .map(UserSearchResponseDto::toDto)
                            .collect(Collectors.toList()),
                    boardApplyLeader.getApplyUsers().stream()
                            .map(ApplyUsers::getUserId)
                            .map(UserSearchResponseDto::toDto)
                            .collect(Collectors.toList()),
                    boardApplyLeader.getBoardId().getUserId().getDepartment(),
                    boardApplyLeader.getLeaderId().getDepartment(),
                    boardApplyLeader.getStatus(), boardApplyLeader.getCreatedDate(), boardApplyLeader.getModifiedDate());
        }else{
            throw new UserHandler(ErrorStatus.USER_NOT_AUTHORITY);
        }

    }

    public ChatNotificationResponseDto notificationChatClickAction(String email, Long id) {
        User user=userRepository.findByEmail(email)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        ChatRoom chatRoom=chatRoomRepository.findById(id)
                .orElseThrow(() -> new ChatRoomHandler(ErrorStatus.CHAT_ROOM_NOT_FOUND));

        List<ChatRoomUser> chatRoomUserList=chatRoom.getChatRoomUsers();
        boolean result=false;
        //채팅방에 유저가 포함되어있는지 확인
        for (ChatRoomUser chatRoomUser:chatRoomUserList) {
            if(chatRoomUser.getUser()==user) {
                result=true;
                break;
            }
        }

        if(result==false){
            throw new ChatRoomUserHandler(ErrorStatus.NOT_FOUND_CHAT_ROOM_IN_USER);
        }

        return ChatNotificationResponseDto.builder()
                .title(chatRoom.getTitle())
                .applyLeaderDepartment(chatRoom.getApplyLeaderDepartment())
                .leaderUserDepartment(chatRoom.getLeaderUserDepartment())
                .build();
    }
}
